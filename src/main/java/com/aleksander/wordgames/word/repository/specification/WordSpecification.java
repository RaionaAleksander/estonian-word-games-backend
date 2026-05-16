package com.aleksander.wordgames.word.repository.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.aleksander.wordgames.model.entity.Word;
import com.aleksander.wordgames.word.dto.request.WordFilterRequest;

import jakarta.persistence.criteria.Predicate;

public class WordSpecification {

    public static Specification<Word> build(WordFilterRequest request) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // length
            if (request.getMinLength() != null) {
                predicates.add(cb.ge(root.get("length"), request.getMinLength()));
            }

            if (request.getMaxLength() != null) {
                predicates.add(cb.le(root.get("length"), request.getMaxLength()));
            }

            // startsWith
            if (request.getStartsWith() != null) {
                predicates.add(cb.like(
                        cb.lower(root.get("lemma")),
                        request.getStartsWith().toLowerCase() + "%"));
            }

            // endsWith
            if (request.getEndsWith() != null) {
                predicates.add(cb.like(
                        cb.lower(root.get("lemma")),
                        "%" + request.getEndsWith().toLowerCase()));
            }

            // contains
            if (request.getContains() != null && !request.getContains().isEmpty()) {
                for (String part : request.getContains()) {
                    predicates.add(cb.like(
                            cb.lower(root.get("lemma")),
                            "%" + part.toLowerCase() + "%"));
                }
            }

            // notContains
            if (request.getNotContains() != null && !request.getNotContains().isEmpty()) {
                for (String part : request.getNotContains()) {
                    predicates.add(cb.notLike(
                            cb.lower(root.get("lemma")),
                            "%" + part.toLowerCase() + "%"));
                }
            }

            // excludedWords
            if (request.getExcludedWords() != null && !request.getExcludedWords().isEmpty()) {
                predicates.add(cb.not(root.get("lemma")
                        .in(request.getExcludedWords())));
            }

            // pattern (underscore = any char)
            if (request.getPattern() != null && !request.getPattern().isBlank()) {

                String sqlPattern = request.getPattern()
                        .toLowerCase();

                predicates.add(cb.like(
                        cb.lower(root.get("lemma")),
                        sqlPattern));
            }

            // include categories
            if (request.getIncludeCategories() != null
                    && !request.getIncludeCategories().isEmpty()) {

                List<String> normalized = request.getIncludeCategories()
                        .stream()
                        .map(String::toUpperCase)
                        .toList();

                predicates.add(
                        cb.upper(root.get("category")).in(normalized));
            }

            // exclude categories
            if (request.getExcludeCategories() != null
                    && !request.getExcludeCategories().isEmpty()) {

                List<String> normalized = request.getExcludeCategories()
                        .stream()
                        .map(String::toUpperCase)
                        .toList();

                predicates.add(
                        cb.not(
                                cb.upper(root.get("category")).in(normalized)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}