package com.aleksander.wordgames.savedgame.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.aleksander.wordgames.model.entity.SavedGame;

@Repository
public interface SavedGameRepository extends JpaRepository<SavedGame, Long>, JpaSpecificationExecutor<SavedGame> {
}