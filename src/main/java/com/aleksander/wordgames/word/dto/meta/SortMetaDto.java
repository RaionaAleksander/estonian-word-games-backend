package com.aleksander.wordgames.word.dto.meta;

import com.aleksander.wordgames.word.enums.SortOrder;
import com.aleksander.wordgames.word.enums.SortType;

public record SortMetaDto(
        SortType sort,
        SortOrder order) {
}