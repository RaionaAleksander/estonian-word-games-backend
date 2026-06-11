package com.aleksander.wordgames.wordsearch.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WordSearchDirection {

    RIGHT(0, 1),
    LEFT(0, -1),
    DOWN(1, 0),
    UP(-1, 0),

    DOWN_RIGHT(1, 1),
    DOWN_LEFT(1, -1),
    UP_RIGHT(-1, 1),
    UP_LEFT(-1, -1);

    private final int rowStep;
    private final int colStep;

    public int nextRow(int row, int step) {
        return row + rowStep * step;
    }

    public int nextCol(int col, int step) {
        return col + colStep * step;
    }
}