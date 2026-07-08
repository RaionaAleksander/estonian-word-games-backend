package com.aleksander.wordgames.wordsearch.dto.request;

import java.util.List;

import com.aleksander.wordgames.common.enums.LetterCase;
import com.aleksander.wordgames.word.dto.request.WordSortRequest;
import com.aleksander.wordgames.wordsearch.engine.placer.WordPlacementOptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomWordSearchRequest {
    private int rows;
    private int cols;

    private List<String> words;

    private LetterCase letterCase;

    private WordSortRequest sort;

    private WordPlacementOptions placement;
}