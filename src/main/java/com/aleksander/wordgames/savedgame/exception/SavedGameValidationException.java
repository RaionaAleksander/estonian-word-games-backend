package com.aleksander.wordgames.savedgame.exception;

import org.springframework.http.HttpStatus;

public class SavedGameValidationException extends SavedGameException {
    public SavedGameValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}