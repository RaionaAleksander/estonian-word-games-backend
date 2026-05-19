package com.aleksander.wordgames.savedgame.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aleksander.wordgames.savedgame.dto.SavedGameResponse;
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

    @PostMapping
    public SavedGameResponse save(@RequestBody JsonNode payload) {
        return savedGameService.save(payload);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        savedGameService.delete(id);
    }
}