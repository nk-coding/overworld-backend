package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.PlayerTaskStatisticMapper;
import de.unistuttgart.overworldbackend.repositories.MinigameTaskRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerStatisticRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerTaskActionLogRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerTaskStatisticRepository;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PlayerTaskStatisticService {

  private static final long COMPLETED_SCORE = 50;
  private static final long MAX_KNOWLEDGE = 100;
  private static final double RETRY_KNOWLEDGE = 0.02;

  @Autowired
  PlayerTaskStatisticMapper playerTaskStatisticMapper;

  @Autowired
  PlayerTaskStatisticRepository playerTaskStatisticRepository;

  @Autowired
  MinigameTaskRepository minigameTaskRepository;

  @Autowired
  PlayerStatisticRepository playerstatisticRepository;

  @Autowired
  PlayerTaskActionLogRepository playerTaskActionLogRepository;

  /**
   * Gets a list of all playerTaskStatistics of a player of the given lecture
   *
   * @param lectureId lecture which the statistics belong to
   * @param playerId player which the statistics belong to
   * @return List of playerTaskStatisticDTOs of the player of the lecture
   */
  public List<PlayerTaskStatisticDTO> getAllStatisticsOfPlayer(final int lectureId, final String playerId) {
    final List<PlayerTaskStatistic> statisticList = playerTaskStatisticRepository
      .findByLectureId(lectureId)
      .parallelStream()
      .filter(playerTaskStatistic -> playerTaskStatistic.getPlayerStatistic().getUserId().equals(playerId))
      .toList();
    return playerTaskStatisticMapper.playerTaskStatisticsToPlayerTaskStatisticDTO(statisticList);
  }

  /**
   * Gets the playerStatistic of the lecture of the player of the statisticId
   * @param lectureId lecture which the statistic belongs to
   * @param playerId player which the statistic belongs to
   * @param statisticId id of the statistic, which is returned
   * @return playerTaskStatistic with the given statisticId
   */
  public PlayerTaskStatisticDTO getStatisticOfPlayer(
    final int lectureId,
    final String playerId,
    final UUID statisticId
  ) {
    return playerTaskStatisticMapper.playerTaskStatisticToPlayerTaskStatisticDTO(
      playerTaskStatisticRepository
        .findById(statisticId)
        .filter(statistic ->
          statistic.getLecture().getId() == lectureId && statistic.getPlayerStatistic().getUserId().equals(playerId)
        )
        .orElseThrow(() ->
          new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            String.format(
              "Statistic with the id %s of the lecture %s of the player %s not found",
              statisticId,
              lectureId,
              playerId
            )
          )
        )
    );
  }

  /**
   * update PlayerTaskStatistic with the given data
   * This method gets a data object with a Player, a Game, a Configuration and a score.
   * The given data gets logged as a PlayerTaskActionLog.
   * It calculates the progress of the player with the given score and updates the value in the correct PlayerStatistic object.
   * @param data Data of a game run
   * @return updated playerTaskStatistic
   */
  public PlayerTaskStatisticDTO submitData(final PlayerTaskStatisticData data) {
    final MinigameTask minigameTask = minigameTaskRepository
      .findByGameAndConfigurationId(data.getGame(), data.getConfigurationId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Minigame not found"));
    final Lecture lecture = minigameTask.getLecture();
    final PlayerStatistic playerStatistic = playerstatisticRepository
      .findByLectureIdAndUserId(lecture.getId(), data.getUserId())
      .orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Player %s not found", data.getUserId()))
      );
    final PlayerTaskStatistic playerTaskStatistic = playerTaskStatisticRepository
      .findByMinigameTaskIdAndLectureIdAndPlayerStatisticId(
        minigameTask.getId(),
        lecture.getId(),
        playerStatistic.getId()
      )
      .orElseGet(() -> {
        final PlayerTaskStatistic newPlayerTaskStatistic = new PlayerTaskStatistic();
        newPlayerTaskStatistic.setPlayerStatistic(playerStatistic);
        newPlayerTaskStatistic.setMinigameTask(minigameTask);
        newPlayerTaskStatistic.setLecture(lecture);
        return playerTaskStatisticRepository.save(newPlayerTaskStatistic);
      });

    long gainedKnowledge = calculateKnowledge(data.getScore(), playerTaskStatistic.getHighscore());

    playerTaskStatistic.setHighscore(Math.max(playerTaskStatistic.getHighscore(), data.getScore()));
    playerTaskStatistic.setCompleted(playerTaskStatistic.isCompleted() || checkCompleted(data.getScore()));

    logData(data, lecture, playerTaskStatistic, gainedKnowledge);

    calculateCompletedDungeons(playerStatistic);

    //TODO:calculate unlocked areas

    playerStatistic.addKnowledge(gainedKnowledge);
    playerstatisticRepository.save(playerStatistic);

    return playerTaskStatisticMapper.playerTaskStatisticToPlayerTaskStatisticDTO(
      playerTaskStatisticRepository.save(playerTaskStatistic)
    );
  }

  private void calculateCompletedDungeons(final PlayerStatistic playerstatistic) {
    final List<PlayerTaskStatistic> playerTaskStatistics = playerTaskStatisticRepository.findByPlayerStatisticId(
      playerstatistic.getId()
    );
    playerstatistic.setCompletedDungeons(
      playerstatistic
        .getLecture()
        .getWorlds()
        .parallelStream()
        .map(World::getDungeons)
        .flatMap(Collection::parallelStream)
        .filter(dungeon ->
          dungeon
            .getMinigameTasks()
            .parallelStream()
            .allMatch(minigameTask ->
              playerTaskStatistics
                .parallelStream()
                .filter(taskStatistic -> taskStatistic.getMinigameTask().equals(minigameTask))
                .anyMatch(PlayerTaskStatistic::isCompleted)
            )
        )
        .map(dungeon -> new AreaLocation(dungeon.getWorld(), dungeon))
        .collect(Collectors.toCollection(ArrayList::new))
    );
  }

  private void logData(
    final PlayerTaskStatisticData data,
    final Lecture lecture,
    final PlayerTaskStatistic currentPlayerTaskStatistic,
    final long gainedKnowledge
  ) {
    final PlayerTaskActionLog actionLog = new PlayerTaskActionLog();
    actionLog.setPlayerTaskStatistic(currentPlayerTaskStatistic);
    actionLog.setLecture(lecture);
    actionLog.setScore(data.getScore());
    actionLog.setCurrentHighscore(currentPlayerTaskStatistic.getHighscore());
    actionLog.setGainedKnowledge(gainedKnowledge);
    actionLog.setGame(data.getGame());
    actionLog.setConfigurationId(data.getConfigurationId());
    playerTaskActionLogRepository.save(actionLog);
  }

  private long calculateKnowledge(final long score, final long highscore) {
    return (long) (
      (double) MAX_KNOWLEDGE *
      Math.max(0, score - highscore) /
      100 +
      MAX_KNOWLEDGE *
      Math.max(0, score - Math.max(0, score - highscore)) *
      RETRY_KNOWLEDGE
    );
  }

  private boolean checkCompleted(final long score) {
    return score >= COMPLETED_SCORE;
  }
}
