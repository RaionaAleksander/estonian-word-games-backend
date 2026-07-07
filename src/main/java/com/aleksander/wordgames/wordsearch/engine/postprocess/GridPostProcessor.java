package com.aleksander.wordgames.wordsearch.engine.postprocess;

import java.util.Random;

import org.springframework.stereotype.Component;

import com.aleksander.wordgames.common.enums.LetterCase;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GridPostProcessor {

    private final Random random = new Random();

    private static final char[] ESTONIAN_LETTERS = {
            'a', 'b', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'r', 's', 'š', 'z', 'ž', 't', 'u', 'v', 'õ', 'ä', 'ö', 'ü'
    };

    public void fillRandom(char[][] grid) {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] == '\u0000') {
                    grid[row][col] = ESTONIAN_LETTERS[random.nextInt(ESTONIAN_LETTERS.length)];
                }
            }
        }
    }

    public void applyLetterCase(char[][] grid, LetterCase letterCase) {
        if (letterCase != LetterCase.UPPER) {
            return;
        }

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                grid[row][col] = Character.toUpperCase(grid[row][col]);
            }
        }
    }
}