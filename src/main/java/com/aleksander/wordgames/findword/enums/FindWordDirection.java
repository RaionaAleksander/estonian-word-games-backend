package com.aleksander.wordgames.findword.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FindWordDirection {

    RIGHT(0, 1),
    DOWN(1, 0);

    private final int rowStep;
    private final int colStep;

    public FindWordDirection opposite() {
        return this == RIGHT ? DOWN : RIGHT;
    }

    public int nextRow(int row, int step) {
        return row + rowStep * step;
    }

    public int nextCol(int col, int step) {
        return col + colStep * step;
    }
}