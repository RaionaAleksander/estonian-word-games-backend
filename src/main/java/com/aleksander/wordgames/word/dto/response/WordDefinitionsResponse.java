package com.aleksander.wordgames.word.dto.response;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WordDefinitionsResponse {
    private String word;
    private int count;
    private boolean randomApplied;
    private List<String> definitions;
    private Instant fetchedAt;
}