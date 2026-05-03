package com.aleksander.wordgames.word.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WordExistsResponse {
    private String word;
    private boolean exists;
    private Instant checkedAt;
}