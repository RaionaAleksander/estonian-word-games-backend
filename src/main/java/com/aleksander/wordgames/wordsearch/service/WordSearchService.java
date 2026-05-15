package com.aleksander.wordgames.wordsearch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.aleksander.wordgames.common.enums.Direction;
import com.aleksander.wordgames.exception.InvalidDirectionException;
import com.aleksander.wordgames.generator.GameGenerator;
import com.aleksander.wordgames.word.dto.WordDto;
import com.aleksander.wordgames.word.dto.filter.WordFilterRequest;
import com.aleksander.wordgames.word.dto.filter.WordSortRequest;
import com.aleksander.wordgames.word.dto.request.WordListRequest;
import com.aleksander.wordgames.word.enums.SortOrder;
import com.aleksander.wordgames.word.enums.SortType;
import com.aleksander.wordgames.word.service.WordService;
import com.aleksander.wordgames.wordsearch.dto.PlacementDto;
import com.aleksander.wordgames.wordsearch.dto.WordSearchRequest;
import com.aleksander.wordgames.wordsearch.dto.WordSearchResponse;
import com.aleksander.wordgames.wordsearch.exception.NoWordsFoundException;
import com.aleksander.wordgames.wordsearch.exception.WordSearchGenerationException;
import com.aleksander.wordgames.wordsearch.validation.WordSearchValidator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class WordSearchService implements GameGenerator<WordSearchRequest, WordSearchResponse> {

    private final WordService wordService;

    private final Random random = new Random();

    private static final char[] ESTONIAN_LETTERS = {
            'a', 'b', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'r', 's', 'š', 'z', 'ž', 't', 'u', 'v', 'õ', 'ä', 'ö', 'ü'
    };

    public WordSearchResponse generate(WordSearchRequest request) {

        WordSearchValidator.validate(request);

        int rows = request.getRows();
        int cols = request.getCols();

        WordFilterRequest filter = request.getFilter();

        if (filter.getMaxLength() == null) {
            filter.setMaxLength(Math.max(rows, cols));
        }

        WordSortRequest sort = new WordSortRequest(
                SortType.LENGTH,
                SortOrder.DESC);

        WordListRequest listRequest = new WordListRequest(
                filter,
                sort,
                request.getWordsCount(),
                true);

        List<String> words = wordService.findWords(listRequest)
                .stream()
                .map(WordDto::getLemma)
                .toList();

        if (words.isEmpty()) {
            throw new NoWordsFoundException();
        }

        int requested = request.getWordsCount();
        int actual = words.size();

        String warning = null;

        if (actual < requested) {

            if (Boolean.TRUE.equals(request.getAllowIncomplete())) {

                warning = "Requested " + requested + ", but only " + actual + " words available";

            } else {
                throw new WordSearchGenerationException(
                        "Not enough words found: " + actual + "/" + requested);
            }
        }

        for (int attempt = 0; attempt < 100; attempt++) {

            char[][] grid = new char[rows][cols];
            List<PlacementDto> placements = new ArrayList<>();

            boolean success = true;

            for (String word : words) {

                PlacementDto placement = tryPlaceWord(grid, word);

                if (placement == null) {
                    success = false;
                    break;
                }

                placements.add(placement);
            }

            if (success) {

                fillRandom(grid);

                return new WordSearchResponse(
                        rows,
                        cols,
                        grid,
                        words,
                        placements,
                        Instant.now(),
                        warning);
            }
        }

        throw new WordSearchGenerationException("Failed to generate word search");
    }

    // ---------------- helpers ----------------

    private void fillRandom(char[][] grid) {

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {

                if (grid[i][j] == '\u0000') {
                    grid[i][j] = ESTONIAN_LETTERS[random.nextInt(ESTONIAN_LETTERS.length)];
                }
            }
        }
    }

    private List<Direction> getValidDirections(String word, int rows, int cols) {

        List<Direction> directions = new ArrayList<>();

        int len = word.length();

        // horizontal
        if (len <= cols) {
            directions.add(Direction.RIGHT);
            directions.add(Direction.LEFT);
        }

        // vertical
        if (len <= rows) {
            directions.add(Direction.DOWN);
            directions.add(Direction.UP);
        }

        // diagonal
        if (len <= rows && len <= cols) {
            directions.add(Direction.DOWN_RIGHT);
            directions.add(Direction.DOWN_LEFT);
            directions.add(Direction.UP_RIGHT);
            directions.add(Direction.UP_LEFT);
        }

        return directions;
    }

    private PlacementDto tryPlaceWord(char[][] grid, String word) {

        int rows = grid.length;
        int cols = grid[0].length;

        List<Direction> validDirections = getValidDirections(word, rows, cols);

        if (validDirections.isEmpty()) {
            return null;
        }

        for (int attempt = 0; attempt < 100; attempt++) {

            Direction dir = validDirections.get(random.nextInt(validDirections.size()));

            int row;
            int col;

            switch (dir) {

                case RIGHT -> {
                    row = random.nextInt(rows);
                    col = random.nextInt((cols - word.length()) + 1);
                }

                case LEFT -> {
                    row = random.nextInt(rows);
                    col = random.nextInt(word.length() - 1, cols);
                }

                case DOWN -> {
                    row = random.nextInt((rows - word.length()) + 1);
                    col = random.nextInt(cols);
                }

                case UP -> {
                    row = random.nextInt(word.length() - 1, rows);
                    col = random.nextInt(cols);
                }

                case DOWN_RIGHT -> {
                    row = random.nextInt((rows - word.length()) + 1);
                    col = random.nextInt((cols - word.length()) + 1);
                }

                case DOWN_LEFT -> {
                    row = random.nextInt((rows - word.length()) + 1);
                    col = random.nextInt(word.length() - 1, cols);
                }

                case UP_RIGHT -> {
                    row = random.nextInt(word.length() - 1, rows);
                    col = random.nextInt((cols - word.length()) + 1);
                }

                case UP_LEFT -> {
                    row = random.nextInt(word.length() - 1, rows);
                    col = random.nextInt(word.length() - 1, cols);
                }

                default -> throw new InvalidDirectionException(dir.name());
            }

            if (!canPlaceBounds(word, row, col, dir, rows, cols)) {
                continue;
            }

            if (!canPlaceWord(grid, word, row, col, dir)) {
                continue;
            }

            place(grid, word, row, col, dir);

            return new PlacementDto(word, row, col, dir);
        }

        return null;
    }

    private boolean canPlaceBounds(String word, int row, int col, Direction dir, int rows, int cols) {

        int endRow = dir.nextRow(row, word.length() - 1);
        int endCol = dir.nextCol(col, word.length() - 1);

        return endRow >= 0
                && endRow < rows
                && endCol >= 0
                && endCol < cols;
    }

    private boolean canPlaceWord(char[][] grid, String word, int row, int col, Direction dir) {

        for (int i = 0; i < word.length(); i++) {

            int r = dir.nextRow(row, i);
            int c = dir.nextCol(col, i);

            char existing = grid[r][c];

            if (existing != '\u0000' && existing != word.charAt(i)) {
                return false;
            }
        }

        return true;
    }

    private void place(char[][] grid, String word, int row, int col, Direction dir) {

        for (int i = 0; i < word.length(); i++) {

            int r = dir.nextRow(row, i);
            int c = dir.nextCol(col, i);

            grid[r][c] = word.charAt(i);
        }
    }
}