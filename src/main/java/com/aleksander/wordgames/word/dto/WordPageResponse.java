package com.aleksander.wordgames.word.dto;

import java.time.Instant;
import java.util.List;

import com.aleksander.wordgames.word.dto.meta.WordRequestMetaDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WordPageResponse {

    private long totalElements;

    private int totalPages;

    private int currentPage;

    private int pageSize;

    private int count;

    private List<WordDto> words;

    private WordRequestMetaDto meta;

    private Instant generatedAt;
}