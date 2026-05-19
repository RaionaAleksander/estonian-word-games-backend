package com.aleksander.wordgames.savedgame.dto.model;

import java.time.Instant;

import com.aleksander.wordgames.common.enums.GameType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedGameDto {

    private Long id;

    private GameType gameType;

    private Instant createdAt;
}