package com.aleksander.wordgames.wordsearch.dto;

import com.aleksander.wordgames.wordsearch.enums.WordSearchDirection;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlacementDto {

    private String word;

    private int row;
    private int col;

    private WordSearchDirection direction;
}