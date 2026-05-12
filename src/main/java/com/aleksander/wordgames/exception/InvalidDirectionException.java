package com.aleksander.wordgames.exception;

import org.springframework.http.HttpStatus;

public class InvalidDirectionException extends ApiException {
    public InvalidDirectionException(String dir) {
        super("Unexpected direction: " + dir, HttpStatus.BAD_REQUEST);
    }
}