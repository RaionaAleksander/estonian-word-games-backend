package com.aleksander.wordgames.wordsearch.validation;

import com.aleksander.wordgames.wordsearch.exception.WordSearchValidationException;

public class GridValidator {
    public static void validateSize(int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new WordSearchValidationException(
                    "Grid size must be positive");
        }
    }
}