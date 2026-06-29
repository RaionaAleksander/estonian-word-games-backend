package com.aleksander.wordgames.wordsearch.dto;

import com.aleksander.wordgames.common.enums.LetterCase;
import com.aleksander.wordgames.word.dto.request.WordFilterRequest;
import com.aleksander.wordgames.word.dto.request.WordSortRequest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WordSearchRequest {

    private int rows;
    private int cols;

    private int wordsCount;

    private Boolean allowIncomplete;

    private LetterCase letterCase;

    private WordFilterRequest filter;

    private WordSortRequest sort;
}