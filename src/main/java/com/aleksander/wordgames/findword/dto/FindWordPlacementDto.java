package com.aleksander.wordgames.findword.dto;

import com.aleksander.wordgames.findword.enums.FindWordDirection;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FindWordPlacementDto {

    private String word;

    private String clue;

    private int row;
    private int col;

    private FindWordDirection direction;
}