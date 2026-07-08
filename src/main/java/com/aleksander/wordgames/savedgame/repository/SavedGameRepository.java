package com.aleksander.wordgames.savedgame.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aleksander.wordgames.model.entity.SavedGame;

import jakarta.transaction.Transactional;

@Repository
public interface SavedGameRepository extends JpaRepository<SavedGame, Long>, JpaSpecificationExecutor<SavedGame> {
    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE saved_games RESTART IDENTITY CASCADE", nativeQuery = true)
    void truncate();
}