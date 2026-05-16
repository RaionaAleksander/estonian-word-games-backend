package com.aleksander.wordgames.word.dto.meta;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordRequestMetaDto {

    private FilterMetaDto filters;

    private SortMetaDto sort;

    private Boolean random;
}