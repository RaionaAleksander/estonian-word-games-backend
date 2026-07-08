package com.aleksander.wordgames.config.loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.aleksander.wordgames.config.WordLoaderProperties;
import com.aleksander.wordgames.config.dto.WordJson;
import com.aleksander.wordgames.model.entity.Word;
import com.aleksander.wordgames.model.entity.WordDefinition;
import com.aleksander.wordgames.word.repository.WordDefinitionRepository;
import com.aleksander.wordgames.word.repository.WordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WordImportService {

    private final WordRepository wordRepository;
    private final WordDefinitionRepository definitionRepository;
    private final WordLoaderProperties properties;
    private final ObjectMapper objectMapper;

    @Transactional
    public void loadAll() throws IOException {

        for (WordLoaderProperties.FileConfig config : properties.getFiles()) {
            loadFromFile(config.getFile(), config.getCategory());
        }
    }

    private void loadFromFile(String fileName, String category) throws IOException {

        InputStream is = getClass()
                .getResourceAsStream("/data/" + fileName);

        if (is == null) {
            throw new IllegalArgumentException(
                    "Cannot find file: " + fileName);
        }

        List<WordJson> words = objectMapper.readValue(
                is,
                new TypeReference<>() {
                });

        for (WordJson json : words) {

            String lemma = json.getWord().trim().toLowerCase();

            Word word = Word.builder()
                    .lemma(lemma)
                    .length(lemma.length())
                    .category(category)
                    .build();

            wordRepository.save(word);

            for (String definitionText : json.getDefinitions()) {

                WordDefinition definition = new WordDefinition();
                definition.setWord(word);
                definition.setDefinition(definitionText);

                definitionRepository.save(definition);
            }
        }
    }
}