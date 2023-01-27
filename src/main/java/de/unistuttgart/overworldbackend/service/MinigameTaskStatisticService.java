package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.PlayerStatistic;
import de.unistuttgart.overworldbackend.data.PlayerTaskActionLog;
import de.unistuttgart.overworldbackend.data.PlayerTaskStatistic;
import de.unistuttgart.overworldbackend.data.statistics.MinigameHighscoreDistribution;
import de.unistuttgart.overworldbackend.data.statistics.MinigameSuccessRateStatistic;
import de.unistuttgart.overworldbackend.repositories.MinigameTaskRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerTaskStatisticRepository;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MinigameTaskStatisticService {

    @Autowired
    private MinigameTaskRepository minigameTaskRepository;

    @Autowired
    private PlayerTaskStatisticRepository playerTaskStatisticRepository;

    static final List TIME_SPENT_DISTRIBUTION_PERCENTAGES = Arrays.asList(0, 25, 50, 75, 100);

    /**
     * Returns the success rate and amount of tries of a player for a minigame task till success
     * @param minigameTaskId id of the minigame task to get the statistic for
     * @returns a the minigame success rate statistic for the given minigame task
     */
    public MinigameSuccessRateStatistic getSuccessRateOfMinigame(UUID minigameTaskId) {
        // get success rate of all PlayerTaskStatistics by minigameTaskId
        List<PlayerTaskStatistic> playerTaskStatistics = playerTaskStatisticRepository.findByMinigameTaskId(
            minigameTaskId
        );
        List<PlayerTaskStatistic> successfulPlayerTaskStatistics = playerTaskStatistics
            .stream()
            .filter(playerTaskStatistic -> playerTaskStatistic.isCompleted())
            .toList();
        List<PlayerTaskStatistic> failedPlayerTaskStatistics = playerTaskStatistics
            .stream()
            .filter(playerTaskStatistic -> !playerTaskStatistic.isCompleted())
            .toList();

        // collect tries of playerstatistic till success by counting up action logs till success
        Map<PlayerTaskStatistic, Integer> successfulTries = successfulPlayerTaskStatistics
            .stream()
            .collect(
                Collectors.toMap(
                    Function.identity(),
                    taskStatistic ->
                        (int) getOrderedPlayerTaskActionLogByDate(taskStatistic)
                            .stream()
                            .takeWhile(log -> !this.wasTaskActionLogSuccessful(log))
                            .count() +
                        1
                )
            );
        Map<PlayerTaskStatistic, Integer> failedTries = failedPlayerTaskStatistics
            .stream()
            .collect(Collectors.toMap(Function.identity(), statistic -> statistic.getPlayerTaskActionLogs().size()));
        double successRate = (double) successfulPlayerTaskStatistics.size() / playerTaskStatistics.size();
        Map<Integer, Integer> successRateDistribution = new HashMap<>();
        successfulTries.forEach((playerTaskStatistic, tries) ->
            successRateDistribution.put(tries, successRateDistribution.getOrDefault(tries, 0) + 1)
        );
        Map<Integer, Integer> failRateDistribution = new HashMap<>();
        failedTries.forEach((playerTaskStatistic, tries) ->
            failRateDistribution.put(tries, failRateDistribution.getOrDefault(tries, 0) + 1)
        );
        return new MinigameSuccessRateStatistic(successRateDistribution, failRateDistribution, successRate);
    }

    public List<MinigameHighscoreDistribution> getPlayerHighscoreDistributions(
        final UUID minigameTaskId,
        Optional<List<Integer>> timeSpentDistributionPercentages
    ) {
        final List<Integer> timeSpentDistributionPercentagesToUse;
        if (timeSpentDistributionPercentages.isEmpty()) {
            timeSpentDistributionPercentagesToUse = TIME_SPENT_DISTRIBUTION_PERCENTAGES;
        } else {
            timeSpentDistributionPercentagesToUse = timeSpentDistributionPercentages.get();
        }
        if (timeSpentDistributionPercentagesToUse.size() < 2) {
            throw new IllegalArgumentException("TIME_SPENT_DISTRIBUTION_PERCENTAGES must have at least 2 elements");
        }
        if (timeSpentDistributionPercentagesToUse.get(0) != 0) {
            throw new IllegalArgumentException("TIME_SPENT_DISTRIBUTION_PERCENTAGES must start with 0");
        }
        if (timeSpentDistributionPercentagesToUse.get(timeSpentDistributionPercentagesToUse.size() - 1) != 100) {
            throw new IllegalArgumentException("TIME_SPENT_DISTRIBUTION_PERCENTAGES must end with 100");
        }
        final List<PlayerTaskStatistic> playerTaskStatistics = playerTaskStatisticRepository.findByMinigameTaskIdOrderByHighscore(
            minigameTaskId
        );
        final List<MinigameHighscoreDistribution> highscoreDistributions = new ArrayList<>();
        for (int i = 0; i < timeSpentDistributionPercentagesToUse.size() - 1; i++) {
            MinigameHighscoreDistribution highscoreDistribution = new MinigameHighscoreDistribution();
            highscoreDistribution.setFromPercentage(timeSpentDistributionPercentagesToUse.get(i));
            highscoreDistribution.setToPercentage(timeSpentDistributionPercentagesToUse.get(i + 1));
            highscoreDistributions.add(highscoreDistribution);
        }

        // calculate score time borders to time spent distribution percentage
        int currentStatisticIndex = 0;
        for (MinigameHighscoreDistribution highscoreDistribution : highscoreDistributions) {
            int endIndex = (int) (
                (highscoreDistribution.getToPercentage() / 100.0) * (playerTaskStatistics.size() - 1)
            );
            PlayerTaskStatistic currentStatistic = playerTaskStatistics.get(currentStatisticIndex);
            highscoreDistribution.setFromScore(currentStatistic.getHighscore());
            while (currentStatisticIndex <= endIndex) {
                highscoreDistribution.addCount();
                currentStatisticIndex++;
            }
            highscoreDistribution.setToScore(currentStatistic.getHighscore());
        }
        return highscoreDistributions;
    }

    /**
     * Gets the ordered player task action logs by date
     * @param successfulPlayerTaskStatistic the player task statistic to get the logs for
     * @return the ordered player task action logs by date
     */
    private List<PlayerTaskActionLog> getOrderedPlayerTaskActionLogByDate(
        final PlayerTaskStatistic successfulPlayerTaskStatistic
    ) {
        return successfulPlayerTaskStatistic
            .getPlayerTaskActionLogs()
            .stream()
            .sorted(Comparator.comparing(PlayerTaskActionLog::getDate))
            .toList();
    }

    /**
     * @param log the action log of the game run
     * @return wether the game run of the action run was successful or not
     */
    private boolean wasTaskActionLogSuccessful(PlayerTaskActionLog log) {
        return log.getScore() >= PlayerTaskStatisticService.COMPLETED_SCORE;
    }
}
