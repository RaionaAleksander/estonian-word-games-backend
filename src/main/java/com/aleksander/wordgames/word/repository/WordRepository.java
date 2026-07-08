package com.aleksander.wordgames.word.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.aleksander.wordgames.model.entity.Word;

import jakarta.transaction.Transactional;

import java.util.Optional;

public interface WordRepository extends JpaRepository<Word, Long>, JpaSpecificationExecutor<Word> {
    boolean existsByLemma(String lemma);

    Optional<Word> findByLemma(String lemma);

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE words RESTART IDENTITY CASCADE", nativeQuery = true)
    void truncate();
}