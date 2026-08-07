package com.aleksander.wordgames.wordsearch.validation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.aleksander.wordgames.wordsearch.dto.CellCoordinate;
import com.aleksander.wordgames.wordsearch.exception.WordSearchValidationException;

public class ShapeValidator {

    public static void validate(
            int rows,
            int cols,
            List<CellCoordinate> blockedCells) {

        if (blockedCells == null) {
            return;
        }

        Set<CellCoordinate> uniqueCells = new HashSet<>();

        for (CellCoordinate cell : blockedCells) {
            if (cell == null) {
                throw new WordSearchValidationException("Blocked cell must not be null");
            }

            if (cell.row() < 0 || cell.row() >= rows) {
                throw new WordSearchValidationException(
                        "Blocked cell row out of bounds: " + cell.row());
            }

            if (cell.col() < 0 || cell.col() >= cols) {
                throw new WordSearchValidationException(
                        "Blocked cell column out of bounds: " + cell.col());
            }

            if (!uniqueCells.add(cell)) {
                throw new WordSearchValidationException(
                        "Blocked cells contain duplicates: " + cell);
            }
        }
    }
}