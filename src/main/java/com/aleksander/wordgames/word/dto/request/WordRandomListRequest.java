package com.aleksander.wordgames.word.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
public class WordRandomListRequest {

    private WordFilterRequest filter;

    private Integer limit;
}