package com.aleksander.wordgames.wordsearch.utils;

import org.springframework.stereotype.Component;

import com.aleksander.wordgames.word.dto.request.WordSortRequest;
import com.aleksander.wordgames.word.enums.SortOrder;
import com.aleksander.wordgames.word.enums.SortType;

@Component
public class WordSortUtils {
    public boolean isGenerationSort(WordSortRequest request) {
        return request.getSort() == SortType.LENGTH
                && request.getOrder() == SortOrder.DESC;
    }
}