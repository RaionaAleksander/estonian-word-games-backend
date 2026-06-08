package com.aleksander.wordgames.word.dto.response;

import java.time.Instant;
import java.util.List;

public record WordDetailsResponse(
        String word,
        boolean exists,
        String lemma,
        Integer length,
        String category,
        List<String> definitions,
        Instant generatedAt) {
}