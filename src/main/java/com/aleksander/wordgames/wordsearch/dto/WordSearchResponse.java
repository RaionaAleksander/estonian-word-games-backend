package com.aleksander.wordgames.wordsearch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;

import com.aleksander.wordgames.common.enums.GameType;
import com.aleksander.wordgames.word.dto.meta.WordRequestMetaDto;

@Data
@AllArgsConstructor
public class WordSearchResponse {
    private GameType gameType;

    private int rows;

    private int cols;

    private char[][] grid;

    private List<String> words;

    private List<PlacementDto> placements;

    private WordRequestMetaDto meta;

    private Instant generatedAt;

    private String warning;
}