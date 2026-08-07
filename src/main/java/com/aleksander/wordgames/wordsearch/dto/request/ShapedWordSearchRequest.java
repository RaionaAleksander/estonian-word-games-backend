package com.aleksander.wordgames.wordsearch.dto.request;

import java.util.List;

import com.aleksander.wordgames.common.enums.LetterCase;
import com.aleksander.wordgames.word.dto.request.WordFilterRequest;
import com.aleksander.wordgames.word.dto.request.WordSortRequest;
import com.aleksander.wordgames.wordsearch.dto.CellCoordinate;
import com.aleksander.wordgames.wordsearch.engine.placer.WordPlacementOptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShapedWordSearchRequest {

    private int rows;
    private int cols;

    private List<CellCoordinate> blockedCells;

    private int wordsCount;

    private Boolean allowIncomplete;

    private LetterCase letterCase;

    private WordFilterRequest filter;

    private WordSortRequest sort;

    private WordPlacementOptions placement;
}