package com.aleksander.wordgames.savedgame.validator;

import java.time.Instant;

import com.aleksander.wordgames.common.enums.GameType;
import com.aleksander.wordgames.savedgame.exception.SavedGameValidationException;
import com.fasterxml.jackson.databind.JsonNode;

public class SavedGameValidator {

    public static GameType validate(JsonNode payload) {

        validateBase(payload);

        GameType type = parseGameType(payload);

        validateByType(payload, type);

        return type;
    }

    private static void validateBase(JsonNode payload) {

        require(payload, "gameType");
        require(payload, "generatedAt");

        try {
            Instant.parse(payload.get("generatedAt").asText());
        } catch (Exception e) {
            throw new SavedGameValidationException("Invalid generatedAt: " + payload.get("generatedAt").asText());
        }
    }

    public static void validateByType(JsonNode payload, GameType type) {

        switch (type) {

            case FIND_WORD -> validateFindWord(payload);

            case WORD_SEARCH -> validateWordSearch(payload);

            default -> throw new SavedGameValidationException(
                    "Unsupported gameType: " + type);
        }
    }

    private static void validateFindWord(JsonNode payload) {
        require(payload, "rows");
        require(payload, "cols");
        require(payload, "grid");
        require(payload, "mainWord");
        require(payload, "clues");
        require(payload, "filters");
    }

    private static void validateWordSearch(JsonNode payload) {
        require(payload, "rows");
        require(payload, "cols");
        require(payload, "grid");
        require(payload, "words");
        require(payload, "placements");
        require(payload, "filters");
    }

    private static GameType parseGameType(JsonNode payload) {

        try {
            return GameType.valueOf(payload.get("gameType").asText());
        } catch (Exception e) {
            throw new SavedGameValidationException("Invalid gameType: " + payload.get("gameType").asText());
        }
    }

    private static void require(JsonNode payload, String field) {
        if (!payload.hasNonNull(field)) {
            throw new SavedGameValidationException("Missing field: " + field);
        }
    }
}
