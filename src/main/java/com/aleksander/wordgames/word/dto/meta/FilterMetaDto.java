package com.aleksander.wordgames.word.dto.meta;

import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterMetaDto {

    private Integer minLength;
    private Integer maxLength;

    private String startsWith;
    private String endsWith;

    private List<String> contains;
    private List<String> notContains;

    private List<String> includeCategories;
    private List<String> excludeCategories;

    private String pattern;

    private List<String> excludedWords;
}