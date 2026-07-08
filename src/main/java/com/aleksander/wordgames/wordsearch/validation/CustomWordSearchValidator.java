package com.aleksander.wordgames.wordsearch.validation;

import java.util.HashSet;
import java.util.List;

import com.aleksander.wordgames.wordsearch.dto.request.CustomWordSearchRequest;
import com.aleksander.wordgames.wordsearch.exception.WordSearchValidationException;

public class CustomWordSearchValidator {

    public static void validate(CustomWordSearchRequest request) {

        int rows = request.getRows();
        int cols = request.getCols();

        if (rows <= 0 || cols <= 0) {
            throw new WordSearchValidationException("Grid size must be positive");
        }

        List<String> words = request.getWords();

        if (words == null || words.isEmpty()) {
            throw new WordSearchValidationException("Words list must not be empty");
        }

        if (new HashSet<>(words).size() != words.size()) {
            throw new WordSearchValidationException("Words list contains duplicates");
        }

        int maxAllowedLength = Math.max(rows, cols);

        for (String word : words) {
            if (word == null || word.isBlank()) {
                throw new WordSearchValidationException("Words must not contain blank values");
            }

            if (word.length() > maxAllowedLength) {
                throw new WordSearchValidationException(
                        "Word '" + word + "' exceeds grid size");
            }
        }
    }
}