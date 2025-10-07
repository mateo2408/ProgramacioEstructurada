package com.example.restservice;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ScoreboardService {

    private final Map<String, Score> scores = new ConcurrentHashMap<>();

    @Cacheable("scoreboard")
    public List<Score> getTopScores(int limit) {
        return scores.values().stream()
                .sorted((s1, s2) -> Integer.compare(s2.getScore(), s1.getScore()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Cacheable("allScores")
    public List<Score> getAllScores() {
        return scores.values().stream()
                .sorted((s1, s2) -> Integer.compare(s2.getScore(), s1.getScore()))
                .collect(Collectors.toList());
    }

    @CacheEvict(value = {"scoreboard", "allScores"}, allEntries = true)
    public Score addScore(String playerName, int score) {
        Score newScore = new Score(playerName, score);
        scores.put(playerName + "_" + System.currentTimeMillis(), newScore);
        return newScore;
    }

    @CacheEvict(value = {"scoreboard", "allScores"}, allEntries = true)
    public void clearScores() {
        scores.clear();
    }

    public Score getPlayerBestScore(String playerName) {
        return scores.values().stream()
                .filter(score -> score.getPlayerName().equals(playerName))
                .max(Comparator.comparing(Score::getScore))
                .orElse(null);
    }
}
