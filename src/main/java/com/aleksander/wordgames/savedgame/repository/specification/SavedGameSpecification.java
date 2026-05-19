package com.aleksander.wordgames.savedgame.repository.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.aleksander.wordgames.common.enums.GameType;
import com.aleksander.wordgames.model.entity.SavedGame;

import jakarta.persistence.criteria.Predicate;

public class SavedGameSpecification {

    public static Specification<SavedGame> build(GameType gameType) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (gameType != null) {
                predicates.add(
                        cb.equal(root.get("gameType"), gameType));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}