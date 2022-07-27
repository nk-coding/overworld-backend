package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.PlayerNPCStatisticMapper;
import de.unistuttgart.overworldbackend.repositories.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PlayerNPCStatisticService {

  private static final long GAINED_KNOWLEDGE_PER_NPC = 100;

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
   * Gets a list of all NPC statistics of a player of the given lecture
   * @param lectureId lecture which the statistics belong to
   * @param playerId player which the statistics belong to
   * @return List of playerNPCStatistiks of the player of the lecture
   */
  public List<PlayerNPCStatisticDTO> getAllStatisticsOfPlayer(final int lectureId, final String playerId) {
    List<PlayerNPCStatistic> statisticList = playerNPCStatisticRepository
      .findByLectureId(lectureId)
      .stream()
      .filter(playerNPCStatistic -> playerNPCStatistic.getPlayerStatistic().getUserId().equals(playerId))
      .toList();
    return playerNPCStatisticMapper.playerNPCStatisticsToPlayerNPCStatisticDTO(statisticList);
  }

  /**
   * Gets the NPC statistic of the player within the lecture with the statisticId
   * @param lectureId lecture which the statistic belongs to
   * @param playerId player which the statistic belongs to
   * @param statisticId id of the statistic, which is returned
   * @return playerNPCStatistic with the given statisticId
   */
  public PlayerNPCStatisticDTO getStatisticOfPlayer(
    final int lectureId,
    final String playerId,
    final UUID statisticId
  ) {
    return playerNPCStatisticMapper.playerNPCStatisticToPlayerNPCStatisticDTO(
      playerNPCStatisticRepository
        .findById(statisticId)
        .filter(statistic ->
          statistic.getLecture().getId() == lectureId && statistic.getPlayerStatistic().getUserId().equals(playerId)
        )
        .orElseThrow(() ->
          new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            String.format(
              "Statistic with the id %s of the player %s in the lecture %s not found.",
              statisticId,
              playerId,
              lectureId
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
    final Lecture lecture = npc.getLecture();
    final PlayerStatistic playerStatistic = playerstatisticRepository
      .findByLectureIdAndUserId(lecture.getId(), data.getUserId())
      .orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Player %s not found", data.getUserId()))
      );
    final Optional<PlayerNPCStatistic> playerNPCStatistic = playerNPCStatisticRepository.findByNpcIdAndLectureIdAndPlayerStatisticId(
      npc.getId(),
      lecture.getId(),
      playerStatistic.getId()
    );
    long gainedKnowledge = 0;
    final PlayerNPCStatistic currentPlayerNPCStatistic;
    if (playerNPCStatistic.isEmpty()) {
      gainedKnowledge = GAINED_KNOWLEDGE_PER_NPC;
      final PlayerNPCStatistic newPlayerNPCStatistic = new PlayerNPCStatistic();
      newPlayerNPCStatistic.setPlayerStatistic(playerStatistic);
      newPlayerNPCStatistic.setNpc(npc);
      newPlayerNPCStatistic.setLecture(lecture);
      newPlayerNPCStatistic.setCompleted(data.isCompleted());
      currentPlayerNPCStatistic = playerNPCStatisticRepository.save(newPlayerNPCStatistic);
    } else {
      currentPlayerNPCStatistic = playerNPCStatistic.get();
      if (!playerNPCStatistic.get().isCompleted() && data.isCompleted()) {
        gainedKnowledge = GAINED_KNOWLEDGE_PER_NPC;
        currentPlayerNPCStatistic.setCompleted(data.isCompleted());
      }
    }

    logData(lecture, currentPlayerNPCStatistic, gainedKnowledge);

    // TODO: calculate unlocked areas

    playerStatistic.addKnowledge(gainedKnowledge);
    playerstatisticRepository.save(playerStatistic);

    return playerNPCStatisticMapper.playerNPCStatisticToPlayerNPCStatisticDTO(
      playerNPCStatisticRepository.save(currentPlayerNPCStatistic)
    );
  }

  private void logData(
    final Lecture lecture,
    final PlayerNPCStatistic currentPlayerNPCStatistic,
    final long gainedKnowledge
  ) {
    final PlayerNPCActionLog actionLog = new PlayerNPCActionLog();
    actionLog.setPlayerNPCStatistic(currentPlayerNPCStatistic);
    actionLog.setLecture(lecture);
    actionLog.setGainedKnowledge(gainedKnowledge);
    playerNPCActionLogRepository.save(actionLog);
  }
}
