package com.aleksander.wordgames.word.dto.meta;

import java.util.List;

public record FilterMetaDto(
        Integer minLength,
        Integer maxLength,
        String startsWith,
        String endsWith,
        List<String> contains,
        List<String> notContains,
        List<String> includeCategories,
        List<String> excludeCategories,
        String pattern,
        List<String> excludedWords) {
}