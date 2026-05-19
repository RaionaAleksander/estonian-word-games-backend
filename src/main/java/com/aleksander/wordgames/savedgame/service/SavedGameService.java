package com.aleksander.wordgames.savedgame.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.aleksander.wordgames.common.enums.GameType;
import com.aleksander.wordgames.model.entity.SavedGame;
import com.aleksander.wordgames.savedgame.dto.SavedGameResponse;
import com.aleksander.wordgames.savedgame.exception.SavedGameNotFoundException;
import com.aleksander.wordgames.savedgame.exception.SavedGameParseException;
import com.aleksander.wordgames.savedgame.repository.SavedGameRepository;
import com.aleksander.wordgames.savedgame.validator.SavedGameValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SavedGameService {

    private final SavedGameRepository repository;
    private final ObjectMapper objectMapper;

    public JsonNode getById(Long id) {

        SavedGame game = repository.findById(id)
                .orElseThrow(() -> new SavedGameNotFoundException(id));

        try {
            return objectMapper.readTree(game.getPayload());

        } catch (JsonProcessingException e) {
            throw new SavedGameParseException(
                    "Failed to parse saved game payload");
        }
    }

    public SavedGameResponse save(JsonNode payload) {
        GameType type = SavedGameValidator.validate(payload);

        SavedGame entity = new SavedGame();
        entity.setGameType(type);
        entity.setPayload(payload.toString());
        entity.setCreatedAt(Instant.now());

        repository.save(entity);

        return new SavedGameResponse(
                entity.getId(),
                entity.getGameType(),
                entity.getCreatedAt());
    }

    @Transactional
    public void delete(Long id) {
        SavedGame game = repository.findById(id)
                .orElseThrow(() -> new SavedGameNotFoundException(id));

        repository.delete(game);
    }
}
