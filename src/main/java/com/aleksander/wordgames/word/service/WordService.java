package com.aleksander.wordgames.word.service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.aleksander.wordgames.model.entity.Word;
import com.aleksander.wordgames.model.entity.WordDefinition;
import com.aleksander.wordgames.word.dto.meta.FilterMetaDto;
import com.aleksander.wordgames.word.dto.meta.SortMetaDto;
import com.aleksander.wordgames.word.dto.meta.WordRequestMetaDto;
import com.aleksander.wordgames.word.dto.model.WordDto;
import com.aleksander.wordgames.word.dto.request.WordFilterRequest;
import com.aleksander.wordgames.word.dto.request.WordListRequest;
import com.aleksander.wordgames.word.dto.request.WordPageRequest;
import com.aleksander.wordgames.word.dto.request.WordSortRequest;
import com.aleksander.wordgames.word.dto.response.WordDefinitionsResponse;
import com.aleksander.wordgames.word.dto.response.WordExistsResponse;
import com.aleksander.wordgames.word.dto.response.WordPageResponse;
import com.aleksander.wordgames.word.dto.response.WordPatternResponse;
import com.aleksander.wordgames.word.dto.response.WordResponse;
import com.aleksander.wordgames.word.enums.SortOrder;
import com.aleksander.wordgames.word.exception.InvalidSortException;
import com.aleksander.wordgames.word.exception.WordNotFoundException;
import com.aleksander.wordgames.word.repository.WordDefinitionRepository;
import com.aleksander.wordgames.word.repository.WordRepository;
import com.aleksander.wordgames.word.repository.specification.WordSpecification;

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

    public WordResponse getWordsResponse(WordListRequest request) {

        List<WordDto> result = findWords(request);

        WordRequestMetaDto meta = buildWordRequestMeta(
                request.getFilter(),
                request.getSort(),
                request.getRandom());

        return new WordResponse(
                result.size(),
                result,
                meta,
                Instant.now());
    }

    public WordPageResponse getWordsPageResponse(WordPageRequest request) {

        List<WordDto> result = findWordsPage(request);

        int page = resolvePage(request.getPage());
        int size = resolveSize(request.getSize());

        long totalElements = filterWords(request.getFilter()).size();

        int totalPages = (int) Math.ceil((double) totalElements / size);

        WordRequestMetaDto meta = buildWordRequestMeta(
                request.getFilter(),
                request.getSort(),
                false);

        return new WordPageResponse(
                totalElements,
                totalPages,
                page,
                size,
                result.size(),
                result,
                meta,
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

    private WordDto toDto(Word w) {
        return new WordDto(w.getId(), w.getLemma(), w.getLength(), w.getCategory());
    }

    private List<WordDto> sort(List<WordDto> list, WordSortRequest request) {
        if (request == null || request.getSort() == null) {
            return list;
        }

        Comparator<WordDto> comparator = switch (request.getSort()) {
            case LENGTH -> Comparator.comparing(WordDto::getLength);
            case ALPHABET -> Comparator.comparing(WordDto::getLemma);
            default -> throw new InvalidSortException("Unsupported sort type: " + request.getSort());
        };

        if (request.getOrder() == SortOrder.DESC) {
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
        Specification<Word> specification = WordSpecification.build(request);

        return wordRepository.findAll(specification);
    }

    public List<WordDto> processWords(List<Word> words, WordListRequest request) {
        List<WordDto> result = words.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        // random + limit + sort logic
        if (Boolean.TRUE.equals(request.getRandom())) {

            Collections.shuffle(result);

            if (request.getLimit() != null && request.getLimit() < result.size()) {
                result = result.subList(0, request.getLimit());
            }

            result = sort(result, request.getSort());

        } else {

            result = sort(result, request.getSort());

            if (request.getLimit() != null && request.getLimit() < result.size()) {
                result = result.subList(0, request.getLimit());
            }
        }

        return result;
    }

    public List<WordDto> findWords(WordListRequest request) {
        List<Word> filtered = filterWords(request.getFilter());
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

    public List<WordDto> findWordsPage(WordPageRequest request) {

        List<Word> filtered = filterWords(request.getFilter());

        List<WordDto> result = filtered.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        result = sort(result, request.getSort());

        int page = resolvePage(request.getPage());
        int size = resolveSize(request.getSize());

        int fromIndex = page * size;

        if (fromIndex >= result.size()) {
            return List.of();
        }

        int toIndex = Math.min(fromIndex + size, result.size());

        return result.subList(fromIndex, toIndex);
    }

    private int resolvePage(Integer page) {
        return (page != null && page > 0)
                ? page
                : 0;
    }

    private int resolveSize(Integer size) {
        return (size != null && size > 0)
                ? size
                : 20;
    }

    /* Meta */

    public FilterMetaDto buildFilterMeta(WordFilterRequest request) {
        FilterMetaDto meta = new FilterMetaDto();

        if (request == null) {
            return meta;
        }

        if (request.getMinLength() != null) {
            meta.setMinLength(request.getMinLength());
        }

        if (request.getMaxLength() != null) {
            meta.setMaxLength(request.getMaxLength());
        }

        if (request.getStartsWith() != null) {
            meta.setStartsWith(request.getStartsWith());
        }

        if (request.getEndsWith() != null) {
            meta.setEndsWith(request.getEndsWith());
        }

        if (request.getContains() != null && !request.getContains().isEmpty()) {
            meta.setContains(request.getContains());
        }

        if (request.getNotContains() != null && !request.getNotContains().isEmpty()) {
            meta.setNotContains(request.getNotContains());
        }

        if (request.getIncludeCategories() != null && !request.getIncludeCategories().isEmpty()) {
            meta.setIncludeCategories(request.getIncludeCategories());
        }

        if (request.getExcludeCategories() != null && !request.getExcludeCategories().isEmpty()) {
            meta.setExcludeCategories(request.getExcludeCategories());
        }

        if (request.getPattern() != null) {
            meta.setPattern(request.getPattern());
        }

        if (request.getExcludedWords() != null && !request.getExcludedWords().isEmpty()) {
            meta.setExcludedWords(request.getExcludedWords());
        }

        return meta;
    }

    public SortMetaDto buildSortMeta(WordSortRequest request) {

        if (request == null || request.getSort() == null) {
            return null;
        }

        return new SortMetaDto(
                request.getSort(),
                request.getOrder());
    }

    public WordRequestMetaDto buildWordRequestMeta(
            WordFilterRequest filter,
            WordSortRequest sort,
            Boolean random) {

        return new WordRequestMetaDto(
                buildFilterMeta(filter),
                buildSortMeta(sort),
                random);
    }
}