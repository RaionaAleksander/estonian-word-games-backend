package com.aleksander.wordgames.config.loader;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.aleksander.wordgames.word.repository.WordRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final WordRepository wordRepository;
    private final WordImportService wordImportService;

    @Override
    public void run(String... args) throws Exception {

        if (wordRepository.count() > 0) {
            return;
        }

        wordImportService.loadAll();

        System.out.println("Words + definitions loaded!");
    }
}