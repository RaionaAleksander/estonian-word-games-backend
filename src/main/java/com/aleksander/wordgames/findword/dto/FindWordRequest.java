package com.aleksander.wordgames.findword.dto;

import com.aleksander.wordgames.common.enums.Direction;
import com.aleksander.wordgames.word.dto.request.WordFilterRequest;

import lombok.*;

@Data
@AllArgsConstructor
public class FindWordRequest {
    private String mainWord;

    private Integer maxCrossLength;

    private Integer mainWordAxisIndex;

    private Direction mainWordDirection;

    private WordFilterRequest filter;
}