package com.aleksander.wordgames.wordsearch.validation;

import com.aleksander.wordgames.word.dto.request.WordFilterRequest;
import com.aleksander.wordgames.wordsearch.dto.request.WordSearchRequest;
import com.aleksander.wordgames.wordsearch.exception.WordSearchValidationException;

public class WordSearchValidator {

    public static void validate(WordSearchRequest request) {

        int rows = request.getRows();
        int cols = request.getCols();

        int wordsCount = request.getWordsCount();

        WordFilterRequest filter = request.getFilter();

        Integer min = filter.getMinLength();
        Integer max = filter.getMaxLength();

        if (rows <= 0 || cols <= 0) {
            throw new WordSearchValidationException("Grid size must be positive");
        }

        if (min != null && min <= 0) {
            throw new WordSearchValidationException("minLength must be >= 1");
        }

        if (min != null && max != null && max < min) {
            throw new WordSearchValidationException("maxLength must be >= minLength");
        }

        int maxAllowed = Math.max(rows, cols);

        if (max != null && max > maxAllowed) {
            throw new WordSearchValidationException("maxLength exceeds grid size");
        }

        if (min != null && min > maxAllowed) {
            throw new WordSearchValidationException("minLength exceeds grid size");
        }

        if (wordsCount <= 0) {
            throw new WordSearchValidationException("wordsCount must be >= 1");
        }
    }
}