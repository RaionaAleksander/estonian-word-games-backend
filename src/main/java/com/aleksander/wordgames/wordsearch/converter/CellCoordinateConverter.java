package com.aleksander.wordgames.wordsearch.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.aleksander.wordgames.wordsearch.dto.CellCoordinate;
import com.aleksander.wordgames.wordsearch.exception.WordSearchValidationException;

@Component
public class CellCoordinateConverter implements Converter<String, CellCoordinate> {

    @Override
    public CellCoordinate convert(String source) {
        String[] parts = source.split(",");

        if (parts.length != 2) {
            throw new WordSearchValidationException(
                    "Invalid cell coordinate: " + source);
        }

        try {
            int row = Integer.parseInt(parts[0].trim());
            int col = Integer.parseInt(parts[1].trim());

            return new CellCoordinate(row, col);

        } catch (NumberFormatException e) {
            throw new WordSearchValidationException(
                    "Invalid cell coordinate: " + source);
        }
    }
}