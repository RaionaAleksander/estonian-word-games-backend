package com.aleksander.wordgames.word.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;

import com.aleksander.wordgames.word.dto.meta.WordRequestMetaDto;
import com.aleksander.wordgames.word.dto.model.WordDto;

@Data
@AllArgsConstructor
public class WordResponse {

    private int count;
    private List<WordDto> words;
    private WordRequestMetaDto meta;
    private Instant generatedAt;
}