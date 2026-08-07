package com.aleksander.wordgames.wordsearch.engine.postprocess;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.aleksander.wordgames.common.enums.LetterCase;
import com.aleksander.wordgames.wordsearch.dto.CellCoordinate;
import com.aleksander.wordgames.wordsearch.engine.GridSymbols;
import com.aleksander.wordgames.wordsearch.enums.FillAlphabet;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GridPostProcessor {

    private final Random random = new Random();

    private static final char[] ESTONIAN_LETTERS = {
            'a', 'b', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'r', 's', 'š', 'z', 'ž', 't', 'u', 'v', 'õ', 'ä', 'ö', 'ü'
    };

    private static final char[] ENGLISH_LETTERS = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    private static final char[] RUSSIAN_LETTERS = {
            'а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л',
            'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч',
            'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я'
    };

    public void fillRandom(char[][] grid, FillAlphabet alphabet) {
        char[] letters = switch (alphabet) {
            case ESTONIAN -> ESTONIAN_LETTERS;
            case ENGLISH -> ENGLISH_LETTERS;
            case RUSSIAN -> RUSSIAN_LETTERS;
        };

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] == GridSymbols.EMPTY) {
                    grid[row][col] = letters[random.nextInt(letters.length)];
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

    public void applyBlockedCells(
            char[][] grid,
            List<CellCoordinate> blockedCells) {

        for (CellCoordinate cell : blockedCells) {
            grid[cell.row()][cell.col()] = GridSymbols.BLOCKED;
        }
    }
}