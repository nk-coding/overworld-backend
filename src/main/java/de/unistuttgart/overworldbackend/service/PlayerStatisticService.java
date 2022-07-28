package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.PlayerStatisticMapper;
import de.unistuttgart.overworldbackend.repositories.PlayerStatisticRepository;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PlayerStatisticService {

  @Autowired
  private LectureService lectureService;

  @Autowired
  private PlayerStatisticRepository playerstatisticRepository;

  @Autowired
  private PlayerStatisticMapper playerstatisticMapper;

  @Autowired
  private AreaService areaService;

  @Autowired
  private WorldService worldService;

  @Autowired
  private DungeonService dungeonService;

  /**
   * get statistics from a player lecture
   *
   * @throws ResponseStatusException (404) when playerstatistic with lectureId and userId could not be found
   * @param lectureId the id of the lecture
   * @param userId the playerId of the player searching for
   * @return the found playerstatistic
   */
  public PlayerStatistic getPlayerStatisticFromLecture(final int lectureId, final String userId) {
    return playerstatisticRepository
      .findByLectureIdAndUserId(lectureId, userId)
      .orElseThrow(() ->
        new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          String.format(
            "There is no playerstatistic from player with userId %s in lecture with id %s.",
            userId,
            lectureId
          )
        )
      );
  }

  /**
   * Create a playerstatistic with initial data in a lecture.
   *
   * @throws ResponseStatusException (404) when lecture with its id does not exist
   *                                 (400) when a player with the playerId already has a playerstatistic
   * @param lectureId the id of the lecture where the playerstatistic will be created
   * @param player the player with its userId and username
   * @return the created playerstatistic as DTO
   */
  public PlayerStatisticDTO createPlayerStatisticInLecture(final int lectureId, final Player player) {
    final Optional<PlayerStatistic> existingPlayerstatistic = playerstatisticRepository.findByLectureIdAndUserId(
      lectureId,
      player.getUserId()
    );
    if (existingPlayerstatistic.isPresent()) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        String.format("There is already a playerstatistic for userId %s in lecture %s", player.getUserId(), lectureId)
      );
    }
    final Lecture lecture = lectureService.getLecture(lectureId);
    final World firstWorld = getFirstWorld(lectureId);

    final PlayerStatistic playerstatistic = new PlayerStatistic();
    playerstatistic.setLecture(lecture);
    playerstatistic.setCompletedDungeons(new ArrayList<>());
    List<Area> unlockedAreas = new ArrayList<>();
    unlockedAreas.add(firstWorld);

    playerstatistic.setUnlockedAreas(unlockedAreas);
    playerstatistic.setUserId(player.getUserId());
    playerstatistic.setUsername(player.getUsername());
    playerstatistic.setCurrentArea(firstWorld);
    playerstatistic.setKnowledge(0);
    return playerstatisticMapper.playerStatisticToPlayerstatisticDTO(playerstatisticRepository.save(playerstatistic));
  }

  /**
   * Update a playerstatistic.
   *
   * Only the currentArea is updatable.
   *
   * @throws ResponseStatusException (404) when playerstatistic with its playerId in the lecture does not exist
   *                                 (400) when combination of world and dungeon in currentLocation does not exist
   * @param lectureId the id of the lecture where the playerstatistic will be created
   * @param playerId the playerId of the player
   * @param playerstatisticDTO the updated parameters
   * @return the updated playerstatistic
   */
  public PlayerStatisticDTO updatePlayerStatisticInLecture(
    final int lectureId,
    final String playerId,
    final PlayerStatisticDTO playerstatisticDTO
  ) {
    final PlayerStatistic playerstatistic = getPlayerStatisticFromLecture(lectureId, playerId);

    if (playerstatisticDTO.getCurrentArea() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current area location is not specified");
    }

    try {
      playerstatistic.setCurrentArea(
        areaService.getAreaFromAreaLocationDTO(lectureId, playerstatisticDTO.getCurrentArea())
      );
    } catch (ResponseStatusException exception) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Specified area does not exist");
    }
    return playerstatisticMapper.playerStatisticToPlayerstatisticDTO((playerstatisticRepository.save(playerstatistic)));
  }

  private World getFirstWorld(final int lectureId) {
    return worldService.getWorldByIndexFromLecture(lectureId, 1);
  }
}