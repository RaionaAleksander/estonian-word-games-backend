package com.aleksander.wordgames.word.dto.request;

import com.aleksander.wordgames.word.enums.SortOrder;
import com.aleksander.wordgames.word.enums.SortType;

import lombok.*;

@Data
@AllArgsConstructor
public class WordSortRequest {

    private SortType sort;
    private SortOrder order;
}