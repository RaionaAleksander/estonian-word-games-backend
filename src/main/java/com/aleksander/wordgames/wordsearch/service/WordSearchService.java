package com.aleksander.wordgames.wordsearch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.aleksander.wordgames.common.enums.GameType;
import com.aleksander.wordgames.generator.GameGenerator;
import com.aleksander.wordgames.word.dto.meta.FilterMetaDto;
import com.aleksander.wordgames.word.dto.meta.SortMetaDto;
import com.aleksander.wordgames.word.dto.meta.WordRequestMetaDto;
import com.aleksander.wordgames.word.dto.model.WordDto;
import com.aleksander.wordgames.word.dto.request.WordFilterRequest;
import com.aleksander.wordgames.word.dto.request.WordRandomListRequest;
import com.aleksander.wordgames.word.dto.request.WordSortRequest;
import com.aleksander.wordgames.word.enums.SortOrder;
import com.aleksander.wordgames.word.enums.SortType;
import com.aleksander.wordgames.word.service.WordService;
import com.aleksander.wordgames.wordsearch.dto.PlacementDto;
import com.aleksander.wordgames.wordsearch.dto.WordSearchRequest;
import com.aleksander.wordgames.wordsearch.dto.WordSearchResponse;
import com.aleksander.wordgames.wordsearch.engine.placer.WordGridPlacer;
import com.aleksander.wordgames.wordsearch.engine.postprocess.GridPostProcessor;
import com.aleksander.wordgames.wordsearch.engine.utils.PlacementUtils;
import com.aleksander.wordgames.wordsearch.exception.NoWordsFoundException;
import com.aleksander.wordgames.wordsearch.exception.WordSearchGenerationException;
import com.aleksander.wordgames.wordsearch.validation.WordSearchValidator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WordSearchService implements GameGenerator<WordSearchRequest, WordSearchResponse> {

    private final WordService wordService;
    private final GridPostProcessor gridPostProcessor;
    private final PlacementUtils placementUtils;
    private final WordGridPlacer wordGridPlacer;

    public WordSearchResponse generate(WordSearchRequest request) {

        WordSearchValidator.validate(request);

        int rows = request.getRows();
        int cols = request.getCols();

        WordFilterRequest filter = request.getFilter();

        if (filter.getMaxLength() == null) {
            filter.setMaxLength(Math.max(rows, cols));
        }

        WordSortRequest generationSort = new WordSortRequest(
                SortType.LENGTH,
                SortOrder.DESC);

        WordRandomListRequest listRequest = new WordRandomListRequest(
                filter,
                generationSort,
                request.getWordsCount());

        List<String> words = wordService.findRandomWords(listRequest)
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

                PlacementDto placement = wordGridPlacer.tryPlaceWord(grid, word);

                if (placement == null) {
                    success = false;
                    break;
                }

                placements.add(placement);
            }

            if (success) {

                gridPostProcessor.fillRandom(grid);

                gridPostProcessor.applyLetterCase(grid, request.getLetterCase());

                WordSortRequest userSort = request.getSort();

                if (userSort == null || userSort.getSort() == null) {
                    userSort = generationSort;
                }

                if (!isGenerationSort(userSort)) {
                    placements = placementUtils.sortPlacements(placements, userSort);
                    words = placementUtils.extractWords(placements);
                }

                FilterMetaDto filterMetaDto = wordService.buildFilterMeta(request.getFilter());

                SortMetaDto sortMetaDto = wordService.buildSortMeta(userSort);

                WordRequestMetaDto requestMeta = new WordRequestMetaDto(
                        filterMetaDto,
                        sortMetaDto);

                return new WordSearchResponse(
                        GameType.WORD_SEARCH,
                        rows,
                        cols,
                        request.getLetterCase(),
                        grid,
                        words,
                        placements,
                        requestMeta,
                        Instant.now(),
                        warning);
            }
        }

        throw new WordSearchGenerationException("Failed to generate word search");
    }

    // ---------------- helpers ----------------

    private boolean isGenerationSort(WordSortRequest request) {
        return request.getSort() == SortType.LENGTH
                && request.getOrder() == SortOrder.DESC;
    }
}