package com.aleksander.wordgames.savedgame.dto.response;

import java.time.Instant;
import java.util.List;

import com.aleksander.wordgames.savedgame.dto.model.SavedGameDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedGamePageResponse {

    private long totalElements;

    private int totalPages;

    private int currentPage;

    private int pageSize;

    private int count;

    private List<SavedGameDto> games;

    private Instant generatedAt;
}