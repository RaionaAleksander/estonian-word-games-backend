package com.aleksander.wordgames.word.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordPageRequest {

    private WordFilterRequest filter;

    private WordSortRequest sort;

    private Integer page;

    private Integer size;
}