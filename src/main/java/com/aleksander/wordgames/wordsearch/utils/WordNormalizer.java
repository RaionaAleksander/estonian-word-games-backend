package com.aleksander.wordgames.wordsearch.utils;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class WordNormalizer {
    public List<String> normalize(List<String> words) {
        if (words == null) {
            return null;
        }

        return words.stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .toList();
    }
}