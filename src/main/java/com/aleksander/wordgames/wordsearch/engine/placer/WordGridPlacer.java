package com.aleksander.wordgames.wordsearch.engine.placer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.aleksander.wordgames.wordsearch.dto.PlacementDto;
import com.aleksander.wordgames.wordsearch.enums.WordSearchDirection;

@Component
public class WordGridPlacer {

    private final Random random = new Random();

    public PlacementDto tryPlaceWord(char[][] grid, String word) {

        int rows = grid.length;
        int cols = grid[0].length;

        List<WordSearchDirection> validDirections = getValidDirections(word, rows, cols);

        if (validDirections.isEmpty()) {
            return null;
        }

        for (int attempt = 0; attempt < 100; attempt++) {

            WordSearchDirection dir = validDirections.get(random.nextInt(validDirections.size()));

            int row;
            int col;

            switch (dir) {

                case RIGHT -> {
                    row = random.nextInt(rows);
                    col = random.nextInt((cols - word.length()) + 1);
                }

                case LEFT -> {
                    row = random.nextInt(rows);
                    col = random.nextInt(word.length() - 1, cols);
                }

                case DOWN -> {
                    row = random.nextInt((rows - word.length()) + 1);
                    col = random.nextInt(cols);
                }

                case UP -> {
                    row = random.nextInt(word.length() - 1, rows);
                    col = random.nextInt(cols);
                }

                case DOWN_RIGHT -> {
                    row = random.nextInt((rows - word.length()) + 1);
                    col = random.nextInt((cols - word.length()) + 1);
                }

                case DOWN_LEFT -> {
                    row = random.nextInt((rows - word.length()) + 1);
                    col = random.nextInt(word.length() - 1, cols);
                }

                case UP_RIGHT -> {
                    row = random.nextInt(word.length() - 1, rows);
                    col = random.nextInt((cols - word.length()) + 1);
                }

                case UP_LEFT -> {
                    row = random.nextInt(word.length() - 1, rows);
                    col = random.nextInt(word.length() - 1, cols);
                }

                default -> throw new IllegalStateException("Unexpected direction: " + dir);
            }

            if (!canPlaceBounds(word, row, col, dir, rows, cols)) {
                continue;
            }

            if (!canPlaceWord(grid, word, row, col, dir)) {
                continue;
            }

            place(grid, word, row, col, dir);

            return new PlacementDto(word, row, col, dir);
        }

        return null;
    }

    private List<WordSearchDirection> getValidDirections(String word, int rows, int cols) {

        List<WordSearchDirection> directions = new ArrayList<>();

        int len = word.length();

        // horizontal
        if (len <= cols) {
            directions.add(WordSearchDirection.RIGHT);
            directions.add(WordSearchDirection.LEFT);
        }

        // vertical
        if (len <= rows) {
            directions.add(WordSearchDirection.DOWN);
            directions.add(WordSearchDirection.UP);
        }

        // diagonal
        if (len <= rows && len <= cols) {
            directions.add(WordSearchDirection.DOWN_RIGHT);
            directions.add(WordSearchDirection.DOWN_LEFT);
            directions.add(WordSearchDirection.UP_RIGHT);
            directions.add(WordSearchDirection.UP_LEFT);
        }

        return directions;
    }

    private boolean canPlaceBounds(String word, int row, int col, WordSearchDirection dir, int rows, int cols) {

        int endRow = dir.nextRow(row, word.length() - 1);
        int endCol = dir.nextCol(col, word.length() - 1);

        return endRow >= 0
                && endRow < rows
                && endCol >= 0
                && endCol < cols;
    }

    private boolean canPlaceWord(char[][] grid, String word, int row, int col, WordSearchDirection dir) {

        for (int i = 0; i < word.length(); i++) {

            int r = dir.nextRow(row, i);
            int c = dir.nextCol(col, i);

            char existing = grid[r][c];

            if (existing != '\u0000' && existing != word.charAt(i)) {
                return false;
            }
        }

        return true;
    }

    private void place(char[][] grid, String word, int row, int col, WordSearchDirection dir) {

        for (int i = 0; i < word.length(); i++) {

            int r = dir.nextRow(row, i);
            int c = dir.nextCol(col, i);

            grid[r][c] = word.charAt(i);
        }
    }
}