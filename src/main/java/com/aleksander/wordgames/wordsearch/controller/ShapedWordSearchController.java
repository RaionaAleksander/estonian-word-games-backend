package com.aleksander.wordgames.wordsearch.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aleksander.wordgames.common.enums.LetterCase;
import com.aleksander.wordgames.word.dto.request.WordFilterRequest;
import com.aleksander.wordgames.word.dto.request.WordSortRequest;
import com.aleksander.wordgames.word.enums.SortOrder;
import com.aleksander.wordgames.word.enums.SortType;
import com.aleksander.wordgames.wordsearch.dto.CellCoordinate;
import com.aleksander.wordgames.wordsearch.dto.WordSearchResponse;
import com.aleksander.wordgames.wordsearch.dto.request.ShapedWordSearchRequest;
import com.aleksander.wordgames.wordsearch.engine.placer.WordPlacementOptions;
import com.aleksander.wordgames.wordsearch.enums.WordSearchDirection;
import com.aleksander.wordgames.wordsearch.service.ShapedWordSearchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/games/shaped-word-search")
public class ShapedWordSearchController {

    private final ShapedWordSearchService shapedWordSearchService;

    @GetMapping("/generate")
    @Operation(summary = "Generate a shaped word search", description = "Generates a word search with blocked cells that define the shape of the grid. "
            + "Blocked cells are provided as row,col coordinates.")
    public WordSearchResponse generate(
            @RequestParam(defaultValue = "10") int rows,
            @RequestParam(defaultValue = "10") int cols,

            @Parameter(description = "Blocked cells coordinates. Format: row,col. Example: 1,1", array = @ArraySchema(schema = @Schema(type = "string"))) @RequestParam List<CellCoordinate> blockedCells,
            @RequestParam(defaultValue = "5") int wordsCount,
            @RequestParam(required = false) Boolean allowIncomplete,
            @RequestParam(defaultValue = "LOWER") LetterCase letterCase,

            // filters
            @RequestParam(required = false) Integer minLength,
            @RequestParam(required = false) Integer maxLength,
            @RequestParam(required = false) String startsWith,
            @RequestParam(required = false) String endsWith,
            @RequestParam(required = false) List<String> contains,
            @RequestParam(required = false) List<String> notContains,
            @RequestParam(required = false) List<String> includeCategories,
            @RequestParam(required = false) List<String> excludeCategories,
            @RequestParam(required = false) String pattern,
            @RequestParam(required = false) List<String> excludedWords,

            // sort
            @RequestParam(required = false) SortType sort,
            @RequestParam(required = false) SortOrder order,

            // placement
            @RequestParam(defaultValue = "true") Boolean allowIntersections,
            @RequestParam(required = false) List<WordSearchDirection> directions) {

        WordFilterRequest filterRequest = new WordFilterRequest(
                minLength,
                maxLength,
                startsWith,
                endsWith,
                contains,
                notContains,
                includeCategories,
                excludeCategories,
                pattern,
                excludedWords);

        WordSortRequest sortRequest = new WordSortRequest(
                sort,
                order);

        WordPlacementOptions placementOptions = new WordPlacementOptions(
                allowIntersections,
                directions,
                100);

        ShapedWordSearchRequest request = new ShapedWordSearchRequest(
                rows,
                cols,
                blockedCells,
                wordsCount,
                allowIncomplete,
                letterCase,
                filterRequest,
                sortRequest,
                placementOptions);

        return shapedWordSearchService.generate(request);
    }
}