package com.aleksander.wordgames.word.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WordPatternResponse {
    private String word;
    private String pattern;
    private int visibleLetters;
    private Instant generatedAt;
}