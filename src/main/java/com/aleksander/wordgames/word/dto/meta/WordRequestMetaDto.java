package com.aleksander.wordgames.word.dto.meta;

public record WordRequestMetaDto(
        FilterMetaDto filters,
        SortMetaDto sort) {
}