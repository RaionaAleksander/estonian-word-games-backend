package com.aleksander.wordgames.wordsearch.engine.placer;

import java.util.List;

import com.aleksander.wordgames.wordsearch.enums.WordSearchDirection;

public record WordPlacementOptions(
        boolean allowIntersections,

        List<WordSearchDirection> directions,

        int maxAttempts) {
}