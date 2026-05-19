package com.aleksander.wordgames.savedgame.dto.request;

import com.aleksander.wordgames.common.enums.GameType;
import com.aleksander.wordgames.savedgame.enums.SavedGameSort;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedGamePageRequest {

    private GameType gameType;

    private SavedGameSort sort;

    private Integer page;

    private Integer size;
}