package com.aleksander.wordgames.word.engine.meta;

import org.springframework.stereotype.Component;

import com.aleksander.wordgames.word.dto.meta.FilterMetaDto;
import com.aleksander.wordgames.word.dto.meta.SortMetaDto;
import com.aleksander.wordgames.word.dto.meta.WordRequestMetaDto;
import com.aleksander.wordgames.word.dto.request.WordFilterRequest;
import com.aleksander.wordgames.word.dto.request.WordSortRequest;
import com.aleksander.wordgames.word.enums.SortOrder;

@Component
public class WordMetaBuilder {

    public FilterMetaDto buildFilterMeta(WordFilterRequest request) {

        if (request == null) {
            return null;
        }

        return new FilterMetaDto(
                request.getMinLength(),
                request.getMaxLength(),
                request.getStartsWith(),
                request.getEndsWith(),
                request.getContains(),
                request.getNotContains(),
                request.getIncludeCategories(),
                request.getExcludeCategories(),
                request.getPattern(),
                request.getExcludedWords());
    }

    public SortMetaDto buildSortMeta(WordSortRequest request) {

        if (request == null || request.getSort() == null) {
            return new SortMetaDto(null, null);
        }

        SortOrder order = request.getOrder();

        if (order == null) {
            order = SortOrder.ASC;
        }

        return new SortMetaDto(
                request.getSort(),
                order);
    }

    public WordRequestMetaDto buildWordRequestMeta(
            WordFilterRequest filter,
            WordSortRequest sort) {

        return new WordRequestMetaDto(
                buildFilterMeta(filter),
                buildSortMeta(sort));
    }
}