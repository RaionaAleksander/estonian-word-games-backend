package com.aleksander.wordgames.word.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
public class WordListRequest {

    private WordFilterRequest filter;

    private WordSortRequest sort;

    private Integer limit;

    private Boolean random;
}