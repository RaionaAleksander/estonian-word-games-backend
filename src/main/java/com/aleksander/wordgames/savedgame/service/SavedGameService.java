package com.aleksander.wordgames.savedgame.service;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.aleksander.wordgames.common.enums.GameType;
import com.aleksander.wordgames.model.entity.SavedGame;
import com.aleksander.wordgames.savedgame.dto.model.SavedGameDto;
import com.aleksander.wordgames.savedgame.dto.request.SavedGamePageRequest;
import com.aleksander.wordgames.savedgame.dto.response.SavedGamePageResponse;
import com.aleksander.wordgames.savedgame.dto.response.SavedGameResponse;
import com.aleksander.wordgames.savedgame.enums.SavedGameSort;
import com.aleksander.wordgames.savedgame.exception.SavedGameNotFoundException;
import com.aleksander.wordgames.savedgame.exception.SavedGameParseException;
import com.aleksander.wordgames.savedgame.repository.SavedGameRepository;
import com.aleksander.wordgames.savedgame.repository.specification.SavedGameSpecification;
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

    public SavedGamePageResponse getSavedGames(
            SavedGamePageRequest request) {

        int page = resolvePage(request.getPage());
        int size = resolveSize(request.getSize());

        Pageable pageable = PageRequest.of(
                page,
                size,
                resolveSort(request.getSort()));

        Specification<SavedGame> spec = SavedGameSpecification.build(request.getGameType());

        Page<SavedGame> result = repository.findAll(spec, pageable);

        List<SavedGameDto> games = result.getContent()
                .stream()
                .map(this::toDto)
                .toList();

        return new SavedGamePageResponse(
                result.getTotalElements(),
                result.getTotalPages(),
                page,
                size,
                games.size(),
                games,
                Instant.now());
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

    @Transactional
    public void deleteAll() {
        repository.truncate();
    }

    // ---------------- helpers ----------------

    private int resolvePage(Integer page) {
        return (page != null && page >= 0)
                ? page
                : 0;
    }

    private int resolveSize(Integer size) {
        return (size != null && size > 0)
                ? size
                : 20;
    }

    private Sort resolveSort(SavedGameSort sort) {
        if (sort == SavedGameSort.OLDEST) {
            return Sort.by(Sort.Direction.ASC, "createdAt");
        }

        return Sort.by(Sort.Direction.DESC, "createdAt");
    }

    private SavedGameDto toDto(SavedGame entity) {

        return new SavedGameDto(
                entity.getId(),
                entity.getGameType(),
                entity.getCreatedAt());
    }
}
