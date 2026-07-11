package com.aleksander.wordgames.wordsearch.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.aleksander.wordgames.common.enums.GameType;
import com.aleksander.wordgames.generator.GameGenerator;
import com.aleksander.wordgames.word.dto.meta.SortMetaDto;
import com.aleksander.wordgames.word.dto.meta.WordRequestMetaDto;
import com.aleksander.wordgames.word.dto.request.WordSortRequest;
import com.aleksander.wordgames.word.engine.meta.WordMetaBuilder;
import com.aleksander.wordgames.word.enums.SortOrder;
import com.aleksander.wordgames.word.enums.SortType;
import com.aleksander.wordgames.wordsearch.dto.PlacementDto;
import com.aleksander.wordgames.wordsearch.dto.WordSearchResponse;
import com.aleksander.wordgames.wordsearch.dto.request.CustomWordSearchRequest;
import com.aleksander.wordgames.wordsearch.engine.placer.WordGridPlacer;
import com.aleksander.wordgames.wordsearch.engine.placer.WordPlacementOptions;
import com.aleksander.wordgames.wordsearch.engine.postprocess.GridPostProcessor;
import com.aleksander.wordgames.wordsearch.engine.utils.PlacementUtils;
import com.aleksander.wordgames.wordsearch.enums.WordSearchDirection;
import com.aleksander.wordgames.wordsearch.exception.WordSearchGenerationException;
import com.aleksander.wordgames.wordsearch.validation.CustomWordSearchValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomWordSearchService implements GameGenerator<CustomWordSearchRequest, WordSearchResponse> {

    private final GridPostProcessor gridPostProcessor;
    private final PlacementUtils placementUtils;
    private final WordGridPlacer wordGridPlacer;
    private final WordMetaBuilder wordMetaBuilder;

    public WordSearchResponse generate(CustomWordSearchRequest request) {
        List<String> words = normalizeWords(request.getWords());

        request.setWords(words);

        CustomWordSearchValidator.validate(request);

        int rows = request.getRows();
        int cols = request.getCols();

        WordPlacementOptions placementOptions = request.getPlacement();

        WordSortRequest generationSort = new WordSortRequest(
                SortType.LENGTH,
                SortOrder.DESC);

        words = placementUtils.sortWords(
                words,
                generationSort);

        for (int attempt = 0; attempt < placementOptions.maxAttempts(); attempt++) {

            char[][] grid = new char[rows][cols];

            List<PlacementDto> placements = new ArrayList<>();

            boolean success = true;

            for (String word : words) {

                PlacementDto placement = wordGridPlacer.tryPlaceWord(
                        grid,
                        word,
                        placementOptions);

                if (placement == null) {
                    success = false;
                    break;
                }

                placements.add(placement);
            }

            if (success) {

                gridPostProcessor.fillRandom(grid);

                gridPostProcessor.applyLetterCase(
                        grid,
                        request.getLetterCase());

                WordSortRequest userSort = request.getSort();

                if (userSort == null || userSort.getSort() == null) {
                    userSort = generationSort;
                }

                if (!isGenerationSort(userSort)) {
                    placements = placementUtils.sortPlacements(
                            placements,
                            userSort);

                    words = placementUtils.extractWords(
                            placements);
                }

                List<WordSearchDirection> usedDirections = placementUtils.extractDirections(placements);

                SortMetaDto sortMetaDto = wordMetaBuilder.buildSortMeta(userSort);

                WordRequestMetaDto requestMeta = new WordRequestMetaDto(
                        null,
                        sortMetaDto);

                return new WordSearchResponse(
                        GameType.CUSTOM_WORD_SEARCH,
                        rows,
                        cols,
                        request.getLetterCase(),
                        placementOptions.allowIntersections(),
                        usedDirections,
                        grid,
                        words,
                        placements,
                        requestMeta,
                        Instant.now(),
                        null);
            }
        }

        throw new WordSearchGenerationException("Failed to generate custom word search");
    }

    // ---------------- helpers ----------------

    private boolean isGenerationSort(WordSortRequest request) {
        return request.getSort() == SortType.LENGTH
                && request.getOrder() == SortOrder.DESC;
    }

    private List<String> normalizeWords(List<String> words) {
        if (words == null) {
            return null;
        }

        return words.stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .toList();
    }
}
