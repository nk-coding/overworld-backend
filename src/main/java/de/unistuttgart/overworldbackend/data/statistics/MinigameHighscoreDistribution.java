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
public class MinigameHighscoreDistribution {

    /**
     * The start of the percentage of players that have a score between fromScore and toScore.
     */
    double fromPercentage;
    /**
     * The end of the percentage of players that have a score between fromScore and toScore.
     */
    double toPercentage;

    /**
     * The lower bound of players that has score on the minigame.
     */
    double fromScore;
    /**
     * The upper bound of players that has score on the minigame.
     */
    double toScore;

    /**
     * The amount of game results that took between fromTime and toTime to finish.
     */
    int count;

    public void addCount() {
        count++;
    }
}
