package com.aleksander.wordgames.wordsearch.engine.utils;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.aleksander.wordgames.word.dto.request.WordSortRequest;
import com.aleksander.wordgames.word.enums.SortOrder;
import com.aleksander.wordgames.wordsearch.dto.PlacementDto;

@Component
public class PlacementUtils {
    public List<PlacementDto> sortPlacements(
            List<PlacementDto> placements,
            WordSortRequest request) {

        if (request == null || request.getSort() == null) {
            return placements;
        }

        Comparator<PlacementDto> comparator = switch (request.getSort()) {
            case LENGTH -> Comparator.comparingInt(p -> p.getWord().length());
            case ALPHABET -> Comparator.comparing(PlacementDto::getWord);
        };

        if (request.getOrder() == SortOrder.DESC) {
            comparator = comparator.reversed();
        }

        return placements.stream()
                .sorted(comparator)
                .toList();
    }

    public List<String> extractWords(List<PlacementDto> placements) {
        return placements.stream()
                .map(PlacementDto::getWord)
                .toList();
    }
}