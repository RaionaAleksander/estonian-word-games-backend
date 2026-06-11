package com.aleksander.wordgames.config.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.aleksander.wordgames.word.repository.WordRepository;

@Component
public class DictionaryHealthIndicator implements HealthIndicator {

    private final WordRepository wordRepository;

    public DictionaryHealthIndicator(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    @Override
    public Health health() {

        long count = wordRepository.count();

        if (count == 0) {
            return Health.down()
                    .withDetail("reason", "Dictionary is empty")
                    .build();
        }

        return Health.up()
                .withDetail("wordCount", count)
                .build();
    }
}