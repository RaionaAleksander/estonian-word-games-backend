package com.aleksander.wordgames.word.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.aleksander.wordgames.model.entity.Word;
import com.aleksander.wordgames.model.entity.WordDefinition;
import com.aleksander.wordgames.word.dto.WordDefinitionsResponse;
import com.aleksander.wordgames.word.dto.WordDto;
import com.aleksander.wordgames.word.dto.WordExistsResponse;
import com.aleksander.wordgames.word.dto.WordFilterRequest;
import com.aleksander.wordgames.word.dto.WordPatternResponse;
import com.aleksander.wordgames.word.dto.WordResponse;
import com.aleksander.wordgames.word.enums.SortOrder;
import com.aleksander.wordgames.word.exception.InvalidSortException;
import com.aleksander.wordgames.word.exception.WordNotFoundException;
import com.aleksander.wordgames.word.repository.WordDefinitionRepository;
import com.aleksander.wordgames.word.repository.WordRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordRepository wordRepository;
    private final WordDefinitionRepository definitionRepository;

    public WordResponse getWordsResponse(WordFilterRequest request) {

        List<WordDto> result = findWords(request);

        return new WordResponse(
                result.size(),
                result,
                Instant.now());
    }

    public WordExistsResponse checkExists(String word) {

        Instant now = Instant.now();

        if (word == null || word.isBlank()) {
            return new WordExistsResponse(null, false, now);
        }

        String normalized = normalize(word);

        boolean exists = exists(normalized);

        return new WordExistsResponse(normalized, exists, now);
    }

    public WordDefinitionsResponse getDefinitionsResponse(
            String word,
            Integer limit,
            boolean random) {

        Instant now = Instant.now();

        if (word == null || word.isBlank()) {
            throw new WordNotFoundException(word);
        }

        String normalized = normalize(word);

        if (!exists(normalized)) {
            throw new WordNotFoundException(word);
        }

        List<String> definitions = getDefinitions(normalized, limit, random);

        return new WordDefinitionsResponse(
                normalized,
                definitions.size(),
                random,
                definitions,
                now);
    }

    public WordPatternResponse getPatternResponse(String word, Integer visibleLetters) {

        Instant now = Instant.now();

        if (word == null || word.isBlank()) {
            throw new WordNotFoundException(word);
        }

        String normalized = normalize(word);

        if (!exists(normalized)) {
            throw new WordNotFoundException(word);
        }

        String pattern;

        if (visibleLetters == null) {
            pattern = generatePatternSmart(normalized);
        } else {
            pattern = generatePattern(normalized, visibleLetters);
        }

        return new WordPatternResponse(
                normalized,
                pattern,
                visibleLetters,
                now);
    }

    // ---------------- helpers ----------------

    private boolean filterByLength(Word w, WordFilterRequest r) {
        if (r.getMinLength() == null && r.getMaxLength() == null)
            return true;

        int len = w.getLength();

        if (r.getMinLength() != null && len < r.getMinLength())
            return false;
        if (r.getMaxLength() != null && len > r.getMaxLength())
            return false;

        return true;
    }

    private boolean filterByStartsWith(Word w, WordFilterRequest r) {
        if (r.getStartsWith() == null)
            return true;
        return w.getLemma().startsWith(r.getStartsWith());
    }

    private boolean filterByEndsWith(Word w, WordFilterRequest r) {
        if (r.getEndsWith() == null)
            return true;
        return w.getLemma().endsWith(r.getEndsWith());
    }

    private boolean filterByContains(Word w, WordFilterRequest r) {
        if (r.getContains() == null || r.getContains().isEmpty())
            return true;

        for (String c : r.getContains()) {
            if (!w.getLemma().contains(c)) {
                return false;
            }
        }
        return true;
    }

    private boolean filterByNotContains(Word w, WordFilterRequest r) {
        if (r.getNotContains() == null)
            return true;

        for (String c : r.getNotContains()) {
            if (w.getLemma().contains(c)) {
                return false;
            }
        }
        return true;
    }

    private boolean filterByPattern(Word w, WordFilterRequest r) {
        if (r.getPattern() == null)
            return true;

        String word = w.getLemma();
        String pattern = r.getPattern();

        if (word.length() != pattern.length())
            return false;

        for (int i = 0; i < pattern.length(); i++) {
            char p = pattern.charAt(i);
            if (p != '_' && p != word.charAt(i)) {
                return false;
            }
        }

        return true;
    }

    private boolean filterByExcludedWords(Word w, WordFilterRequest r) {
        if (r.getExcludedWords() == null || r.getExcludedWords().isEmpty()) {
            return true;
        }
        return !r.getExcludedWords().contains(w.getLemma());
    }

    private WordDto toDto(Word w) {
        return new WordDto(w.getId(), w.getLemma(), w.getLength());
    }

    private List<WordDto> sort(List<WordDto> list, WordFilterRequest r) {
        if (r.getSort() == null) {
            return list;
        }

        Comparator<WordDto> comparator = switch (r.getSort()) {
            case LENGTH -> Comparator.comparing(WordDto::getLength);
            case ALPHABET -> Comparator.comparing(WordDto::getLemma);
            default -> throw new InvalidSortException("Unsupported sort type: " + r.getSort());
        };

        if (r.getOrder() == SortOrder.DESC) {
            comparator = comparator.reversed();
        }

        return list.stream()
                .sorted(comparator)
                .toList();
    }

    public String normalize(String word) {
        return word.trim().toLowerCase();
    }

    public boolean exists(String lemma) {
        return wordRepository.existsByLemma(lemma);
    }

    public List<String> getDefinitions(String lemma, Integer limit, boolean random) {

        Word entity = getWordOrThrow(lemma);

        List<String> definitions = definitionRepository
                .findByWordId(entity.getId())
                .stream()
                .map(WordDefinition::getDefinition)
                .collect(Collectors.toList());

        if (definitions.isEmpty()) {
            return List.of();
        }

        if (random) {
            Collections.shuffle(definitions);
        }

        if (limit != null && limit > 0 && limit < definitions.size()) {
            return definitions.subList(0, limit);
        }

        return definitions;
    }

    private Word getWordOrThrow(String lemma) {
        return wordRepository.findByLemma(lemma)
                .orElseThrow(() -> new WordNotFoundException(lemma));
    }

    public List<Word> filterWords(WordFilterRequest request) {
        List<Word> words = wordRepository.findAll();

        return words.stream()
                .filter(w -> filterByLength(w, request))
                .filter(w -> filterByStartsWith(w, request))
                .filter(w -> filterByEndsWith(w, request))
                .filter(w -> filterByContains(w, request))
                .filter(w -> filterByNotContains(w, request))
                .filter(w -> filterByPattern(w, request))
                .filter(w -> filterByExcludedWords(w, request))
                .collect(Collectors.toList());
    }

    public List<WordDto> processWords(List<Word> words, WordFilterRequest request) {
        List<WordDto> result = words.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        // random + limit + sort logic
        if (Boolean.TRUE.equals(request.getRandom())) {

            Collections.shuffle(result);

            if (request.getLimit() != null && request.getLimit() < result.size()) {
                result = result.subList(0, request.getLimit());
            }

            result = sort(result, request);

        } else {

            result = sort(result, request);

            if (request.getLimit() != null && request.getLimit() < result.size()) {
                result = result.subList(0, request.getLimit());
            }
        }

        return result;
    }

    public List<WordDto> findWords(WordFilterRequest request) {
        List<Word> filtered = filterWords(request);
        return processWords(filtered, request);
    }

    public String generatePattern(String word, int visibleLetters) {

        int length = word.length();

        visibleLetters = Math.max(visibleLetters, 1);

        if (visibleLetters >= length) {
            return word;
        }

        char[] result = new char[length];
        Arrays.fill(result, '_');

        List<Integer> indexes = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            indexes.add(i);
        }

        Collections.shuffle(indexes);

        for (int i = 0; i < visibleLetters; i++) {
            int index = indexes.get(i);
            result[index] = word.charAt(index);
        }

        return new String(result);
    }

    public String resolveClue(String word) {

        List<String> definitions = getDefinitions(word, 1, true);

        if (!definitions.isEmpty()) {
            return definitions.get(0);
        }

        return generatePatternSmart(word);
    }

    private String generatePatternSmart(String word) {

        int visibleLetters = calculateVisibleLetters(word.length());

        return generatePattern(word, visibleLetters);
    }

    private int calculateVisibleLetters(int length) {

        if (length <= 3)
            return 1;
        if (length <= 5)
            return 2;
        if (length <= 7)
            return 3;

        return (int) Math.round(length * 0.5);
    }
}