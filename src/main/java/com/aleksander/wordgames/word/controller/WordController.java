package com.aleksander.wordgames.word.controller;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.aleksander.wordgames.word.dto.WordDefinitionsResponse;
import com.aleksander.wordgames.word.dto.WordExistsResponse;
import com.aleksander.wordgames.word.dto.WordFilterRequest;
import com.aleksander.wordgames.word.dto.WordPatternResponse;
import com.aleksander.wordgames.word.dto.WordResponse;
import com.aleksander.wordgames.word.enums.SortOrder;
import com.aleksander.wordgames.word.enums.SortType;
import com.aleksander.wordgames.word.service.WordService;

@RestController
@RequestMapping("/api/words")
@RequiredArgsConstructor
public class WordController {

    private final WordService wordService;

    @GetMapping
    public WordResponse getWords(
            @RequestParam(required = false) Integer minLength,
            @RequestParam(required = false) Integer maxLength,
            @RequestParam(required = false) String startsWith,
            @RequestParam(required = false) String endsWith,
            @RequestParam(required = false) List<String> contains,
            @RequestParam(required = false) List<String> notContains,
            @RequestParam(required = false) String pattern,
            @RequestParam(required = false) List<String> excludedWords,
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(required = false) Boolean random,
            @RequestParam(required = false) SortType sort,
            @RequestParam(required = false) SortOrder order) {

        WordFilterRequest request = new WordFilterRequest(
                minLength,
                maxLength,
                startsWith,
                endsWith,
                contains,
                notContains,
                pattern,
                excludedWords,
                limit,
                random,
                sort,
                order);

        return wordService.getWordsResponse(request);
    }

    @GetMapping("/exists")
    public WordExistsResponse exists(@RequestParam String word) {
        return wordService.checkExists(word);
    }

    @GetMapping("/definitions")
    public WordDefinitionsResponse getDefinitions(
            @RequestParam String word,
            @RequestParam(required = false) Integer limit,
            @RequestParam(defaultValue = "false") boolean random) {

        return wordService.getDefinitionsResponse(word, limit, random);
    }

    @GetMapping("/pattern")
    public WordPatternResponse getPattern(
            @RequestParam String word,
            @RequestParam(required = false) Integer visibleLetters) {

        return wordService.getPatternResponse(word, visibleLetters);
    }
}