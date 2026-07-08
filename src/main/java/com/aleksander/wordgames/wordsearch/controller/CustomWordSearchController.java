package com.aleksander.wordgames.wordsearch.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aleksander.wordgames.common.enums.LetterCase;
import com.aleksander.wordgames.word.dto.request.WordSortRequest;
import com.aleksander.wordgames.word.enums.SortOrder;
import com.aleksander.wordgames.word.enums.SortType;
import com.aleksander.wordgames.wordsearch.dto.WordSearchResponse;
import com.aleksander.wordgames.wordsearch.dto.request.CustomWordSearchRequest;
import com.aleksander.wordgames.wordsearch.engine.placer.WordPlacementOptions;
import com.aleksander.wordgames.wordsearch.enums.WordSearchDirection;
import com.aleksander.wordgames.wordsearch.service.CustomWordSearchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/games/custom-word-search")
public class CustomWordSearchController {

    private final CustomWordSearchService customWordSearchService;

    @GetMapping("/generate")
    public WordSearchResponse generate(
            @RequestParam(defaultValue = "10") int rows,
            @RequestParam(defaultValue = "10") int cols,
            @RequestParam List<String> words,
            @RequestParam(defaultValue = "LOWER") LetterCase letterCase,
            @RequestParam(defaultValue = "true") Boolean allowIntersections,
            @RequestParam(required = false) List<WordSearchDirection> directions,

            // sort
            @RequestParam(required = false) SortType sort,
            @RequestParam(required = false) SortOrder order) {

        WordSortRequest sortRequest = new WordSortRequest(sort, order);

        WordPlacementOptions placementOptions = new WordPlacementOptions(
                allowIntersections,
                directions,
                100);

        CustomWordSearchRequest request = new CustomWordSearchRequest(
                rows,
                cols,
                words,
                letterCase,
                sortRequest,
                placementOptions);

        return customWordSearchService.generate(request);
    }
}