package com.aleksander.wordgames.wordsearch.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.aleksander.wordgames.word.dto.request.WordFilterRequest;
import com.aleksander.wordgames.word.dto.request.WordSortRequest;
import com.aleksander.wordgames.word.enums.SortOrder;
import com.aleksander.wordgames.word.enums.SortType;
import com.aleksander.wordgames.wordsearch.dto.WordSearchRequest;
import com.aleksander.wordgames.wordsearch.dto.WordSearchResponse;
import com.aleksander.wordgames.wordsearch.service.WordSearchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/games/word-search")
public class WordSearchController {

    private final WordSearchService wordSearchService;

    @GetMapping("/generate")
    public WordSearchResponse generate(
            @RequestParam(defaultValue = "10") int rows,
            @RequestParam(defaultValue = "10") int cols,
            @RequestParam(defaultValue = "5") int wordsCount,
            @RequestParam(required = false) Boolean allowIncomplete,

            // filters
            @RequestParam(required = false) Integer minLength,
            @RequestParam(required = false) Integer maxLength,
            @RequestParam(required = false) String startsWith,
            @RequestParam(required = false) String endsWith,
            @RequestParam(required = false) List<String> contains,
            @RequestParam(required = false) List<String> notContains,
            @RequestParam(required = false) List<String> includeCategories,
            @RequestParam(required = false) List<String> excludeCategories,
            @RequestParam(required = false) String pattern,
            @RequestParam(required = false) List<String> excludedWords,

            // sort
            @RequestParam(required = false) SortType sort,
            @RequestParam(required = false) SortOrder order) {
        WordFilterRequest filterRequest = new WordFilterRequest(
                minLength,
                maxLength,
                startsWith,
                endsWith,
                contains,
                notContains,
                includeCategories,
                excludeCategories,
                pattern,
                excludedWords);

        WordSortRequest sortRequest = new WordSortRequest(
                sort,
                order);

        WordSearchRequest request = new WordSearchRequest(
                rows,
                cols,
                wordsCount,
                allowIncomplete,
                filterRequest,
                sortRequest);

        return wordSearchService.generate(request);
    }
}