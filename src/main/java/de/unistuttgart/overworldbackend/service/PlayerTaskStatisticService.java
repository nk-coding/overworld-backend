package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.PlayerTaskStatisticMapper;
import de.unistuttgart.overworldbackend.repositories.MinigameTaskRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerTaskActionLogRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerTaskStatisticRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerstatisticRepository;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PlayerTaskStatisticService {

  private final long completedScore = 50;
  private final long maxKnowledge = 100;
  private final double retryKnowledge = 0.02;

  @Autowired
  PlayerTaskStatisticMapper playerTaskStatisticMapper;

  @Autowired
  PlayerTaskStatisticRepository playerTaskStatisticRepository;

  @Autowired
  MinigameTaskRepository minigameTaskRepository;

  @Autowired
  PlayerstatisticRepository playerstatisticRepository;

  @Autowired
  PlayerTaskActionLogRepository playerTaskActionLogRepository;

  /**
   * Gets a list of all playerTaskStatistics of a player of the given lecture
   * @param lectureId lecture which the statistics belong to
   * @param playerId player which the statistics belong to
   * @return List of playerTaskStatistiks of the player of the lecture
   */
  public List<PlayerTaskStatisticDTO> getAllStatisticsOfPlayer(int lectureId, String playerId) {
    List<PlayerTaskStatistic> statisticList = playerTaskStatisticRepository
      .findPlayerTaskStatisticByLectureId(lectureId)
      .stream()
      .filter(playerTaskStatistic -> playerTaskStatistic.getPlayerstatistic().getUserId().equals(playerId))
      .collect(Collectors.toList());
    return playerTaskStatisticMapper.playerTaskStatisticsToPlayerTaskStatisticDTO(statisticList);
  }

  /**
   * Gets the playerStatistic of the lecture of the player of the statisticId
   * @param lectureId lecture which the statistic belongs to
   * @param playerId player which the statistic belongs to
   * @param statisticId id of the statistic, which is returned
   * @return playerTaskStatistic with the given statisticId
   */
  public PlayerTaskStatisticDTO getStatisticOfPlayer(int lectureId, String playerId, UUID statisticId) {
    Optional<PlayerTaskStatistic> statistic = playerTaskStatisticRepository.findById(statisticId);
    if (
      statistic.isEmpty() ||
      statistic.get().getLecture().getId() != lectureId ||
      statistic.get().getPlayerstatistic().getUserId().equals(playerId)
    ) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Statistic not found");
    }
    return playerTaskStatisticMapper.playerTaskStatisticToPlayerTaskStatisticDTO(statistic.get());
  }

  /**
   * Returns the update PlayerTaskStatistic with the given data
   * This method gets a data object with a Player, a Game, a Configuration and a score.
   * The given data gets logged as a PlayerTaskActionLog.
   * It calculates the progess of the player with the given score and updates the value in the correct Playerstatistic object.
   * @param data Data of a game run
   * @return updated playerTaskStatistic
   */
  public PlayerTaskStatisticDTO submitData(PlayerTaskStatisticData data) {
    Optional<MinigameTask> minigameTask = minigameTaskRepository.findByGameAndConfigurationId(
      data.getGame(),
      data.getConfigurationId()
    );
    if (minigameTask.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Minigame not found");
    }
    Lecture lecture = minigameTask.get().getLecture();
    Optional<Playerstatistic> playerstatistic = playerstatisticRepository.findByLectureIdAndUserId(
      lecture.getId(),
      data.getUserId()
    );
    if (playerstatistic.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found");
    }
    Optional<PlayerTaskStatistic> playerTaskStatistic = playerTaskStatisticRepository.findPlayerTaskStatisticByMinigameTaskIdAndLectureIdAndPlayerstatisticId(
      minigameTask.get().getId(),
      lecture.getId(),
      playerstatistic.get().getId()
    );
    PlayerTaskStatistic currentPlayerTaskStatistic;
    if (playerTaskStatistic.isEmpty()) {
      PlayerTaskStatistic newPlayerTaskStatistic = new PlayerTaskStatistic();
      newPlayerTaskStatistic.setPlayerstatistic(playerstatistic.get());
      newPlayerTaskStatistic.setMinigameTask(minigameTask.get());
      newPlayerTaskStatistic.setLecture(lecture);
      currentPlayerTaskStatistic = playerTaskStatisticRepository.save(newPlayerTaskStatistic);
    } else {
      currentPlayerTaskStatistic = playerTaskStatistic.get();
    }

    long gainedKnowledge = calculateKnowledge(data.getScore(), currentPlayerTaskStatistic.getHighscore());

    if (data.getScore() > currentPlayerTaskStatistic.getHighscore()) {
      currentPlayerTaskStatistic.setHighscore(data.getScore());
    }
    if (!currentPlayerTaskStatistic.isCompleted()) {
      currentPlayerTaskStatistic.setCompleted(checkCompleted(data.getScore()));
    }

    logData(data, lecture, currentPlayerTaskStatistic, gainedKnowledge);

    calculateCompletedDungeons(playerstatistic.get());

    //TODO:calculate completed dungeons and unlocked areas

    playerstatistic.get().addKnowledge(gainedKnowledge);
    playerstatisticRepository.save(playerstatistic.get());

    return playerTaskStatisticMapper.playerTaskStatisticToPlayerTaskStatisticDTO(
      playerTaskStatisticRepository.save(currentPlayerTaskStatistic)
    );
  }

  private void calculateCompletedDungeons(Playerstatistic playerstatistic) {
    List<PlayerTaskStatistic> playerTaskStatistics = playerTaskStatisticRepository.findPlayerTaskStatisticByPlayerstatisticId(
      playerstatistic.getId()
    );
    List<World> worlds = playerstatistic.getLecture().getWorlds();
    List<Dungeon> completedDungeons = new ArrayList<>();
    for (World world : worlds) {
      for (Dungeon dungeon : world.getDungeons()) {
        boolean completed = true;
        for (MinigameTask minigameTask : dungeon.getMinigameTasks()) {
          Optional<PlayerTaskStatistic> currentStatistic = playerTaskStatistics
            .stream()
            .filter(currentMinigameTask -> currentMinigameTask.equals(minigameTask))
            .findAny();
          if (currentStatistic.isEmpty() || !currentStatistic.get().isCompleted()) {
            completed = false;
          }
        }
        if (completed) {
          completedDungeons.add(dungeon);
        }
      }
    }
    List<AreaLocation> completedDungeonLocations = new ArrayList<>();
    for (Dungeon dungeon : completedDungeons) {
      completedDungeonLocations.add(new AreaLocation(dungeon.getWorld(), dungeon));
    }
    playerstatistic.setCompletedDungeons(completedDungeonLocations);
  }

  private void logData(
    PlayerTaskStatisticData data,
    Lecture lecture,
    PlayerTaskStatistic currentPlayerTaskStatistic,
    long gainedKnowledge
  ) {
    PlayerTaskActionLog actionLog = new PlayerTaskActionLog();
    actionLog.setPlayerTaskStatistic(currentPlayerTaskStatistic);
    actionLog.setLecture(lecture);
    actionLog.setDate(new Date());
    actionLog.setScore(data.getScore());
    actionLog.setCurrentHighscore(currentPlayerTaskStatistic.getHighscore());
    actionLog.setGainedKnowledge(gainedKnowledge);
    actionLog.setGame(data.getGame());
    actionLog.setConfigurationId(data.getConfigurationId());
    playerTaskActionLogRepository.save(actionLog);
  }

  private long calculateKnowledge(long score, long highscore) {
    return (long) (
      maxKnowledge *
      Math.max(0, score - highscore) /
      100 +
      maxKnowledge *
      Math.max(0, score - Math.max(0, score - highscore)) *
      retryKnowledge
    );
  }

  private boolean checkCompleted(long score) {
    return score >= completedScore;
  }
}
