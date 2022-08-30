package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.PlayerNPCStatisticMapper;
import de.unistuttgart.overworldbackend.repositories.NPCRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerNPCActionLogRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerNPCStatisticRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerStatisticRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class PlayerNPCStatisticService {

  private static final long GAINED_KNOWLEDGE_PER_NPC = 100;

  @Autowired
  PlayerStatisticService playerStatisticService;

  @Autowired
  PlayerNPCStatisticMapper playerNPCStatisticMapper;

  @Autowired
  PlayerNPCStatisticRepository playerNPCStatisticRepository;

  @Autowired
  NPCRepository npcRepository;

  @Autowired
  PlayerStatisticRepository playerstatisticRepository;

  @Autowired
  PlayerNPCActionLogRepository playerNPCActionLogRepository;

  /**
   * Gets a list of all NPC statistics of a player of the given course
   * @param courseId course which the statistics belong to
   * @param playerId player which the statistics belong to
   * @return List of playerNPCStatistics of the player of the course
   */
  public List<PlayerNPCStatisticDTO> getAllStatisticsOfPlayer(final int courseId, final String playerId) {
    List<PlayerNPCStatistic> statisticList = playerNPCStatisticRepository
      .findByCourseId(courseId)
      .stream()
      .filter(playerNPCStatistic -> playerNPCStatistic.getPlayerStatistic().getUserId().equals(playerId))
      .toList();
    return playerNPCStatisticMapper.playerNPCStatisticsToPlayerNPCStatisticDTO(statisticList);
  }

  /**
   * Gets the NPC statistic of the player within the course with the statisticId
   * @param courseId course which the statistic belongs to
   * @param playerId player which the statistic belongs to
   * @param statisticId id of the statistic, which is returned
   * @return playerNPCStatistic with the given statisticId
   */
  public PlayerNPCStatisticDTO getStatisticOfPlayer(final int courseId, final String playerId, final UUID statisticId) {
    return playerNPCStatisticMapper.playerNPCStatisticToPlayerNPCStatisticDTO(
      playerNPCStatisticRepository
        .findById(statisticId)
        .filter(statistic ->
          statistic.getCourse().getId() == courseId && statistic.getPlayerStatistic().getUserId().equals(playerId)
        )
        .orElseThrow(() ->
          new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            String.format(
              "Statistic with the id %s of the player %s in the course %s not found.",
              statisticId,
              playerId,
              courseId
            )
          )
        )
    );
  }

  /**
   * Returns the update PlayerTaskStatistic with the given data object.
   * @param data Data of a NPC pass
   * @return updated playerNPCStatistic
   */
  public PlayerNPCStatisticDTO submitData(final PlayerNPCStatisticData data) {
    final NPC npc = npcRepository
      .findById(data.getNpcId())
      .orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("NPC %s not found", data.getNpcId()))
      );
    final Course course = npc.getCourse();
    final PlayerStatistic playerStatistic = playerstatisticRepository
      .findByCourseIdAndUserId(course.getId(), data.getUserId())
      .orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Player %s not found", data.getUserId()))
      );
    final Optional<PlayerNPCStatistic> playerNPCStatistic = playerNPCStatisticRepository.findByNpcIdAndCourseIdAndPlayerStatisticId(
      npc.getId(),
      course.getId(),
      playerStatistic.getId()
    );
    long gainedKnowledge = 0;
    final PlayerNPCStatistic currentPlayerNPCStatistic;
    if (playerNPCStatistic.isEmpty()) {
      gainedKnowledge = GAINED_KNOWLEDGE_PER_NPC;
      final PlayerNPCStatistic newPlayerNPCStatistic = new PlayerNPCStatistic();
      newPlayerNPCStatistic.setPlayerStatistic(playerStatistic);
      newPlayerNPCStatistic.setNpc(npc);
      newPlayerNPCStatistic.setCourse(course);
      newPlayerNPCStatistic.setCompleted(data.isCompleted());
      playerStatistic.addPlayerNPCStatistic(newPlayerNPCStatistic);
      currentPlayerNPCStatistic =
        playerNPCStatisticRepository
          .findByNpcIdAndCourseIdAndPlayerStatisticId(npc.getId(), course.getId(), playerStatistic.getId())
          .get();
    } else {
      currentPlayerNPCStatistic = playerNPCStatistic.get();
      if (!playerNPCStatistic.get().isCompleted() && data.isCompleted()) {
        gainedKnowledge = GAINED_KNOWLEDGE_PER_NPC;
        currentPlayerNPCStatistic.setCompleted(data.isCompleted());
      }
    }

    logData(course, currentPlayerNPCStatistic, gainedKnowledge);

    playerStatisticService.checkForUnlockedAreas(npc.getArea(), playerStatistic);

    playerStatistic.addKnowledge(gainedKnowledge);
    playerstatisticRepository.save(playerStatistic);

    return playerNPCStatisticMapper.playerNPCStatisticToPlayerNPCStatisticDTO(
      playerNPCStatisticRepository.save(currentPlayerNPCStatistic)
    );
  }

  private void logData(
    final Course course,
    final PlayerNPCStatistic currentPlayerNPCStatistic,
    final long gainedKnowledge
  ) {
    final PlayerNPCActionLog actionLog = new PlayerNPCActionLog();
    actionLog.setPlayerNPCStatistic(currentPlayerNPCStatistic);
    actionLog.setCourse(course);
    actionLog.setGainedKnowledge(gainedKnowledge);
    currentPlayerNPCStatistic.addActionLog(actionLog);
  }
}
