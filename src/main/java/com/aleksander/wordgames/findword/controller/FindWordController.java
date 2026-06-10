package com.aleksander.wordgames.findword.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.aleksander.wordgames.common.enums.Direction;
import com.aleksander.wordgames.findword.dto.FindWordRequest;
import com.aleksander.wordgames.findword.dto.FindWordResponse;
import com.aleksander.wordgames.findword.service.FindWordService;
import com.aleksander.wordgames.word.dto.request.WordFilterRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/games/find-word")
public class FindWordController {

    private final FindWordService findWordService;

    @GetMapping("/generate")
    public FindWordResponse generate(
            @RequestParam String mainWord,
            @RequestParam(defaultValue = "10") Integer maxCrossLength,
            @RequestParam(required = false) Integer mainWordAxisIndex,
            @RequestParam(required = false) Direction mainWordDirection,

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
            @RequestParam(required = false) List<String> excludedWords) {

        WordFilterRequest filter = new WordFilterRequest(
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

        FindWordRequest request = new FindWordRequest(
                mainWord,
                maxCrossLength,
                mainWordAxisIndex,
                mainWordDirection,
                filter);

        return findWordService.generate(request);
    }
}