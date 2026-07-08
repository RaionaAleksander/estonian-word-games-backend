package com.aleksander.wordgames.savedgame.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.aleksander.wordgames.common.enums.GameType;
import com.aleksander.wordgames.savedgame.dto.request.SavedGamePageRequest;
import com.aleksander.wordgames.savedgame.dto.response.SavedGamePageResponse;
import com.aleksander.wordgames.savedgame.dto.response.SavedGameResponse;
import com.aleksander.wordgames.savedgame.enums.SavedGameSort;
import com.aleksander.wordgames.savedgame.service.SavedGameService;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/saved-games")
public class SavedGameController {

    private final SavedGameService savedGameService;

    @GetMapping("/{id}")
    public JsonNode getById(@PathVariable Long id) {
        return savedGameService.getById(id);
    }

    @GetMapping
    public SavedGamePageResponse getSavedGames(
            @RequestParam(required = false) GameType gameType,
            @RequestParam(required = false) SavedGameSort sort,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        SavedGamePageRequest request = new SavedGamePageRequest(
                gameType,
                sort,
                page,
                size);

        return savedGameService.getSavedGames(request);
    }

    @PostMapping
    public SavedGameResponse save(@RequestBody JsonNode payload) {
        return savedGameService.save(payload);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        savedGameService.delete(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll() {
        savedGameService.deleteAll();
    }
}