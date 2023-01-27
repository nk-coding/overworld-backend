package de.unistuttgart.overworldbackend.data.statistics;

import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MinigameSuccessRateStatistic {

    /**
     * The x tries (key) which took amount of players (value) till success.
     */
    Map<Integer, Integer> successRateDistribution;

    /**
     * The x tries (key) which took amount of players (value) to not successfully complete the minigame task.
     */
    Map<Integer, Integer> failureRateDistribution;

    double successRate;
}
