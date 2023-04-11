package de.unistuttgart.overworldbackend.data.statistics;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
/**
 * Represents how many players hit a score in a minigame.
 */
public class MinigameScoreHit {

    /**
     * The score that was hit by the players.
     */
    double score;
    /**
     * The amount how many players hit the score.
     */
    double amount;

    public void addAmount() {
        amount++;
    }
}
