package com.aleksander.wordgames.config.dto;

import java.util.List;

import lombok.Data;

@Data
public class WordJson {
    private String word;
    private List<String> definitions;
}