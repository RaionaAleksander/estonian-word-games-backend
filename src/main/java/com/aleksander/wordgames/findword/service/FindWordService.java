package com.aleksander.wordgames.findword.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.aleksander.wordgames.common.enums.Direction;
import com.aleksander.wordgames.common.enums.GameType;
import com.aleksander.wordgames.findword.dto.FindWordPlacementDto;
import com.aleksander.wordgames.findword.dto.FindWordRequest;
import com.aleksander.wordgames.findword.dto.FindWordResponse;
import com.aleksander.wordgames.findword.dto.MainWordPlacementDto;
import com.aleksander.wordgames.findword.exception.FindWordGenerationException;
import com.aleksander.wordgames.findword.exception.FindWordValidationException;
import com.aleksander.wordgames.findword.validation.FindWordValidator;
import com.aleksander.wordgames.generator.GameGenerator;
import com.aleksander.wordgames.word.dto.meta.FilterMetaDto;
import com.aleksander.wordgames.word.dto.model.WordDto;
import com.aleksander.wordgames.word.dto.request.WordFilterRequest;
import com.aleksander.wordgames.word.dto.request.WordRandomListRequest;
import com.aleksander.wordgames.word.dto.request.WordSortRequest;
import com.aleksander.wordgames.word.enums.SortOrder;
import com.aleksander.wordgames.word.enums.SortType;
import com.aleksander.wordgames.word.service.WordService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FindWordService implements GameGenerator<FindWordRequest, FindWordResponse> {

    private final WordService wordService;
    private final Random random = new Random();

    @Override
    public FindWordResponse generate(FindWordRequest request) {
        FindWordValidator.validate(request);
        String mainWord = wordService.normalize(request.getMainWord());

        if (!wordService.exists(mainWord)) {
            throw new FindWordValidationException("Main word not found in dictionary: " + mainWord);
        }

        Direction direction = request.getMainWordDirection();

        if (direction == null) {
            direction = pickMainWordDirection();
        }

        if (direction != Direction.RIGHT
                && direction != Direction.DOWN) {
            throw new IllegalStateException("Unexpected direction: " + direction.name());
        }

        int maxCrossLength = request.getMaxCrossLength();

        int axisIndex = request.getMainWordAxisIndex() != null
                ? request.getMainWordAxisIndex()
                : maxCrossLength / 2;

        if (axisIndex < 0 || axisIndex >= maxCrossLength) {
            throw new FindWordValidationException(
                    "Axis index out of bounds: " + axisIndex);
        }

        int rows;
        int cols;

        int mainRow;
        int mainCol;

        if (direction == Direction.RIGHT) {

            rows = maxCrossLength;
            cols = mainWord.length();

            mainRow = axisIndex;
            mainCol = 0;

        } else {

            rows = mainWord.length();
            cols = maxCrossLength;

            mainRow = 0;
            mainCol = axisIndex;
        }

        char[][] grid = createGrid(rows, cols);

        placeWord(
                grid,
                mainWord,
                mainRow,
                mainCol,
                direction);

        MainWordPlacementDto mainWordPlacement = new MainWordPlacementDto(
                mainWord,
                mainRow,
                mainCol,
                direction);

        Direction clueDirection = direction == Direction.RIGHT
                ? Direction.DOWN
                : Direction.RIGHT;

        for (int attempt = 0; attempt < 100; attempt++) {
            boolean success = true;
            List<FindWordPlacementDto> clues = new ArrayList<>();
            Set<String> usedWords = new HashSet<>();
            usedWords.add(mainWord);

            for (int i = 0; i < mainWord.length(); i++) {

                char letter = mainWord.charAt(i);

                FindWordPlacementDto placement = findWordForLetter(
                        letter,
                        i,
                        axisIndex,
                        clueDirection,
                        maxCrossLength,
                        usedWords,
                        request);

                if (placement == null) {
                    success = false;
                    break;
                }

                clues.add(placement);
                usedWords.add(placement.getWord());
            }

            if (success) {

                placeClueWords(grid, clues);

                List<FindWordPlacementDto> enrichedClues = enrichDefinitions(clues);

                FilterMetaDto meta = wordService.buildFilterMeta(request.getFilter());

                return new FindWordResponse(
                        GameType.FIND_WORD,
                        rows,
                        cols,
                        grid,
                        mainWordPlacement,
                        enrichedClues,
                        meta,
                        Instant.now());
            }
        }

        throw new FindWordGenerationException("Failed to generate find-word puzzle");
    }

    private FindWordPlacementDto findWordForLetter(
            char letter,
            int mainWordIndex,
            int axisIndex,
            Direction clueDirection,
            int crossLength,
            Set<String> usedWords,
            FindWordRequest baseRequest) {

        WordFilterRequest filter = buildFilter(baseRequest);

        filter.setMaxLength(
                Math.min(
                        Optional.ofNullable(baseRequest.getFilter().getMaxLength())
                                .orElse(crossLength),
                        crossLength));

        filter.setContains(mergeContains(baseRequest, letter));
        filter.setExcludedWords(mergeExcluded(baseRequest, usedWords));

        WordRandomListRequest request = new WordRandomListRequest(
                filter,
                new WordSortRequest(SortType.LENGTH, SortOrder.DESC),
                20);

        List<WordDto> candidates = wordService.findRandomWords(request);

        for (WordDto dto : candidates) {

            String word = dto.getLemma();

            List<Integer> validIndexes = findValidIndexes(word, letter, axisIndex, crossLength);

            if (validIndexes.isEmpty()) {
                continue;
            }

            int wordIndex = validIndexes.get(random.nextInt(validIndexes.size()));

            int row;
            int col;

            switch (clueDirection) {

                case DOWN -> {

                    row = axisIndex - wordIndex;
                    col = mainWordIndex;
                }

                case RIGHT -> {

                    row = mainWordIndex;
                    col = axisIndex - wordIndex;
                }

                default -> throw new IllegalStateException("Unexpected direction: " + clueDirection.name());
            }

            return new FindWordPlacementDto(
                    word,
                    null, // clue (enrich later)
                    row,
                    col,
                    clueDirection);
        }

        return null;
    }

    private List<Integer> findValidIndexes(String word, char letter, int mainWordGridIndex, int gridSize) {

        List<Integer> validIndexes = new ArrayList<>();

        for (int i = 0; i < word.length(); i++) {

            if (word.charAt(i) != letter) {
                continue;
            }

            int start = mainWordGridIndex - i;
            int end = start + word.length() - 1;

            if (start >= 0 && end < gridSize) {
                validIndexes.add(i);
            }
        }

        return validIndexes;
    }

    private Direction pickMainWordDirection() {
        return random.nextBoolean()
                ? Direction.RIGHT
                : Direction.DOWN;
    }

    private char[][] createGrid(int rows, int cols) {

        char[][] grid = new char[rows][cols];

        for (int row = 0; row < rows; row++) {
            Arrays.fill(grid[row], ' ');
        }

        return grid;
    }

    private void placeWord(
            char[][] grid,
            String word,
            int row,
            int col,
            Direction direction) {

        switch (direction) {

            case RIGHT -> {
                for (int i = 0; i < word.length(); i++) {
                    grid[row][col + i] = word.charAt(i);
                }
            }

            case DOWN -> {
                for (int i = 0; i < word.length(); i++) {
                    grid[row + i][col] = word.charAt(i);
                }
            }

            default -> throw new IllegalStateException("Unexpected direction: " + direction.name());
        }
    }

    private WordFilterRequest buildFilter(FindWordRequest baseRequest) {

        WordFilterRequest filter = new WordFilterRequest();

        if (baseRequest == null || baseRequest.getFilter() == null) {
            return filter;
        }

        WordFilterRequest baseFilter = baseRequest.getFilter();

        filter.setMinLength(baseFilter.getMinLength());
        filter.setMaxLength(baseFilter.getMaxLength());
        filter.setStartsWith(baseFilter.getStartsWith());
        filter.setEndsWith(baseFilter.getEndsWith());
        filter.setNotContains(baseFilter.getNotContains());
        filter.setPattern(baseFilter.getPattern());

        filter.setIncludeCategories(baseFilter.getIncludeCategories());
        filter.setExcludeCategories(baseFilter.getExcludeCategories());

        return filter;
    }

    private List<String> mergeContains(FindWordRequest baseRequest, char letter) {

        Set<String> contains = new LinkedHashSet<>();

        if (baseRequest != null
                && baseRequest.getFilter() != null
                && baseRequest.getFilter().getContains() != null) {

            contains.addAll(baseRequest.getFilter().getContains());
        }

        contains.add(String.valueOf(letter));

        return new ArrayList<>(contains);
    }

    private List<String> mergeExcluded(FindWordRequest baseRequest, Set<String> usedWords) {

        Set<String> excluded = new HashSet<>();

        if (baseRequest != null
                && baseRequest.getFilter() != null
                && baseRequest.getFilter().getExcludedWords() != null) {

            excluded.addAll(baseRequest.getFilter().getExcludedWords());
        }

        excluded.addAll(usedWords);

        return new ArrayList<>(excluded);
    }

    private void placeClueWords(
            char[][] grid,
            List<FindWordPlacementDto> clues) {

        for (FindWordPlacementDto clue : clues) {

            placeWord(
                    grid,
                    clue.getWord(),
                    clue.getRow(),
                    clue.getCol(),
                    clue.getDirection());
        }
    }

    private List<FindWordPlacementDto> enrichDefinitions(
            List<FindWordPlacementDto> clues) {

        List<FindWordPlacementDto> result = new ArrayList<>();

        for (FindWordPlacementDto clueDto : clues) {

            String clue = wordService.resolveClue(clueDto.getWord());

            result.add(new FindWordPlacementDto(
                    clueDto.getWord(),
                    clue,
                    clueDto.getRow(),
                    clueDto.getCol(),
                    clueDto.getDirection()));
        }

        return result;
    }
}
