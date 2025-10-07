package com.example.restservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scoreboard")
public class ScoreboardController {

    @Autowired
    private ScoreboardService scoreboardService;

    @GetMapping("/top/{limit}")
    public List<Score> getTopScores(@PathVariable int limit) {
        return scoreboardService.getTopScores(limit);
    }

    @GetMapping("/all")
    public List<Score> getAllScores() {
        return scoreboardService.getAllScores();
    }

    @PostMapping("/add")
    public ResponseEntity<Score> addScore(@RequestBody ScoreRequest request) {
        Score score = scoreboardService.addScore(request.getPlayerName(), request.getScore());
        return ResponseEntity.ok(score);
    }

    @GetMapping("/player/{playerName}")
    public ResponseEntity<Score> getPlayerBestScore(@PathVariable String playerName) {
        Score score = scoreboardService.getPlayerBestScore(playerName);
        if (score != null) {
            return ResponseEntity.ok(score);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearScores() {
        scoreboardService.clearScores();
        return ResponseEntity.ok("Scoreboard cleared successfully");
    }

    // Inner class for request body
    public static class ScoreRequest {
        private String playerName;
        private int score;

        public String getPlayerName() {
            return playerName;
        }

        public void setPlayerName(String playerName) {
            this.playerName = playerName;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }
}
