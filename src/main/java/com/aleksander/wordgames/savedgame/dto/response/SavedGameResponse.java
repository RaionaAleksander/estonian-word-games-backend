package com.aleksander.wordgames.savedgame.dto.response;

import java.time.Instant;

import com.aleksander.wordgames.common.enums.GameType;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedGameResponse {

    private Long id;

    private GameType gameType;

    private Instant createdAt;
}