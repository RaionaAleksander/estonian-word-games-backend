package com.aleksander.wordgames.findword.dto;

import com.aleksander.wordgames.findword.enums.FindWordDirection;
import com.aleksander.wordgames.word.dto.request.WordFilterRequest;

import lombok.*;

@Data
@AllArgsConstructor
public class FindWordRequest {
    private String mainWord;

    private Integer maxCrossLength;

    private Integer mainWordAxisIndex;

    private FindWordDirection mainWordDirection;

    private WordFilterRequest filter;
}