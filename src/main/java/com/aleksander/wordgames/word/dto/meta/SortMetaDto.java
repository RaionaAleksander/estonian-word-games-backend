package com.aleksander.wordgames.word.dto.meta;

import com.aleksander.wordgames.word.enums.SortOrder;
import com.aleksander.wordgames.word.enums.SortType;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortMetaDto {

    private SortType sort;

    private SortOrder order;
}