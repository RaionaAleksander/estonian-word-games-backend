package com.aleksander.wordgames.word.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;

import com.aleksander.wordgames.word.dto.meta.WordRequestMetaDto;

@Data
@AllArgsConstructor
public class WordResponse {

    private int count;
    private List<WordDto> words;
    private WordRequestMetaDto meta;
    private Instant generatedAt;
}