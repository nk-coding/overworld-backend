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

  private long gainedKnowledgePerNPC = 100;

  @Autowired
  PlayerNPCStatisticMapper playerNPCStatisticMapper;

  @Autowired
  PlayerNPCStatisticRepository playerNPCStatisticRepository;

  @Autowired
  NPCRepository npcRepository;

  @Autowired
  PlayerstatisticRepository playerstatisticRepository;

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
      .findPlayerNPCStatisticByLectureId(lectureId)
      .stream()
      .filter(playerNPCStatistic -> playerNPCStatistic.getPlayerstatistic().getUserId().equals(playerId))
      .collect(Collectors.toList());
    return playerNPCStatisticMapper.playerNPCStatisticsToPlayerNPCStatisticDTO(statisticList);
  }

  /**
   * Gets the playerStatistic of the lecture of the player of the statisticId
   * @param lectureId lecture which the statistic belongs to
   * @param playerId player which the statistic belongs to
   * @param statisticId id of the statistic, which is returned
   * @return playerTaskStatistic with the given statisticId
   */
  public PlayerNPCStatisticDTO getStatisticOfPlayer(
    final int lectureId,
    final String playerId,
    final UUID statisticId
  ) {
    final Optional<PlayerNPCStatistic> statistic = playerNPCStatisticRepository.findById(statisticId);
    if (
      statistic.isEmpty() ||
      statistic.get().getLecture().getId() != lectureId ||
      !playerId.equals(statistic.get().getPlayerstatistic().getUserId())
    ) {
      throw new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        String.format(
          "Statistic with the id %s of the player %s in the lecture %s not found.",
          statisticId,
          playerId,
          lectureId
        )
      );
    }
    return playerNPCStatisticMapper.playerNPCStatisticToPlayerNPCStatisticDTO(statistic.get());
  }

  /**
   * Returns the update PlayerTaskStatistic with the given data
   * This method gets a data object with a Player, a Game, a Configuration and a score.
   * The given data gets logged as a PlayerTaskActionLog.
   * It calculates the progess of the player with the given score and updates the value in the correct Playerstatistic object.
   * @param data Data of a game run
   * @return updated playerTaskStatistic
   */
  public PlayerNPCStatisticDTO submitData(final PlayerNPCStatisticData data) {
    final Optional<NPC> npc = npcRepository.findById(data.getNpcId());
    if (npc.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("NPC %s not found", data.getNpcId()));
    }
    final Lecture lecture = npc.get().getLecture();
    final Optional<Playerstatistic> playerstatistic = playerstatisticRepository.findByLectureIdAndUserId(
      lecture.getId(),
      data.getUserId()
    );
    if (playerstatistic.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Player %s not found", data.getUserId()));
    }
    final Optional<PlayerNPCStatistic> playerNPCStatistic = playerNPCStatisticRepository.findPlayerNPCStatisticByNpcIdAndLectureIdAndPlayerstatisticId(
      npc.get().getId(),
      lecture.getId(),
      playerstatistic.get().getId()
    );
    long gainedKnowledge = 0;
    final PlayerNPCStatistic currentPlayerNPCStatistic;
    if (playerNPCStatistic.isEmpty()) {
      gainedKnowledge = gainedKnowledgePerNPC;
      final PlayerNPCStatistic newPlayerNPCStatistic = new PlayerNPCStatistic();
      newPlayerNPCStatistic.setPlayerstatistic(playerstatistic.get());
      newPlayerNPCStatistic.setNpc(npc.get());
      newPlayerNPCStatistic.setLecture(lecture);
      newPlayerNPCStatistic.setCompleted(data.isCompleted());
      currentPlayerNPCStatistic = playerNPCStatisticRepository.save(newPlayerNPCStatistic);
    } else {
      currentPlayerNPCStatistic = playerNPCStatistic.get();
      if (!playerNPCStatistic.get().isCompleted() && data.isCompleted()) {
        gainedKnowledge = gainedKnowledgePerNPC;
        currentPlayerNPCStatistic.setCompleted(data.isCompleted());
      }
    }

    logData(lecture, currentPlayerNPCStatistic, gainedKnowledge);

    // TODO: calculate completed dungeons and unlocked areas

    playerstatistic.get().addKnowledge(gainedKnowledge);
    playerstatisticRepository.save(playerstatistic.get());

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
    actionLog.setDate(new Date());
    actionLog.setGainedKnowledge(gainedKnowledge);
    playerNPCActionLogRepository.save(actionLog);
  }
}
