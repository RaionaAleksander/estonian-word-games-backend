package com.aleksander.wordgames.wordsearch.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aleksander.wordgames.common.enums.LetterCase;
import com.aleksander.wordgames.word.dto.request.WordSortRequest;
import com.aleksander.wordgames.word.enums.SortOrder;
import com.aleksander.wordgames.word.enums.SortType;
import com.aleksander.wordgames.wordsearch.dto.CellCoordinate;
import com.aleksander.wordgames.wordsearch.dto.WordSearchResponse;
import com.aleksander.wordgames.wordsearch.dto.request.CustomShapedWordSearchRequest;
import com.aleksander.wordgames.wordsearch.engine.placer.WordPlacementOptions;
import com.aleksander.wordgames.wordsearch.enums.FillAlphabet;
import com.aleksander.wordgames.wordsearch.enums.WordSearchDirection;
import com.aleksander.wordgames.wordsearch.service.CustomShapedWordSearchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/games/custom-shaped-word-search")
public class CustomShapedWordSearchController {

    private final CustomShapedWordSearchService customShapedWordSearchService;

    @GetMapping("/generate")
    @Operation(summary = "Generate a custom shaped word search", description = "Generates a shaped word search using words provided by the user. "
            + "Blocked cells are provided as row,col coordinates.")
    public WordSearchResponse generate(
            @RequestParam(defaultValue = "10") int rows,
            @RequestParam(defaultValue = "10") int cols,
            @Parameter(description = "Blocked cells coordinates. Format: row,col. Example: 1,1", array = @ArraySchema(schema = @Schema(type = "string"))) @RequestParam List<CellCoordinate> blockedCells,
            @RequestParam List<String> words,
            @RequestParam(defaultValue = "LOWER") LetterCase letterCase,
            @RequestParam(defaultValue = "ESTONIAN") FillAlphabet alphabet,
            @RequestParam(defaultValue = "true") Boolean allowIntersections,
            @RequestParam(required = false) List<WordSearchDirection> directions,

            // sort
            @RequestParam(required = false) SortType sort,
            @RequestParam(required = false) SortOrder order) {

        WordSortRequest sortRequest = new WordSortRequest(
                sort,
                order);

        WordPlacementOptions placementOptions = new WordPlacementOptions(
                allowIntersections,
                directions,
                100);

        CustomShapedWordSearchRequest request = new CustomShapedWordSearchRequest(
                rows,
                cols,
                blockedCells,
                words,
                letterCase,
                alphabet,
                sortRequest,
                placementOptions);

        return customShapedWordSearchService.generate(request);
    }
}