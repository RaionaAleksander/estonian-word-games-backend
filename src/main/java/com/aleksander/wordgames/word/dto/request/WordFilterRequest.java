package com.aleksander.wordgames.word.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordFilterRequest {

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