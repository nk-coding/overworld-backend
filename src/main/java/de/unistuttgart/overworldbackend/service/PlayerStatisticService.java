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
  private CourseService courseService;

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
   * get statistics from a player course
   *
   * @throws ResponseStatusException (404) when playerstatistic with courseId and userId could not be found
   * @param courseId the id of the course
   * @param userId the playerId of the player searching for
   * @return the found playerstatistic
   */
  public PlayerStatistic getPlayerStatisticFromCourse(final int courseId, final String userId) {
    return playerstatisticRepository
      .findByCourseIdAndUserId(courseId, userId)
      .orElseThrow(() ->
        new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          String.format(
            "There is no playerstatistic from player with userId %s in course with id %s.",
            userId,
            courseId
          )
        )
      );
  }

  /**
   * Create a playerstatistic with initial data in a course.
   *
   * @throws ResponseStatusException (404) when course with its id does not exist
   *                                 (400) when a player with the playerId already has a playerstatistic
   * @param courseId the id of the course where the playerstatistic will be created
   * @param player the player with its userId and username
   * @return the created playerstatistic as DTO
   */
  public PlayerStatisticDTO createPlayerStatisticInCourse(final int courseId, final Player player) {
    final Optional<PlayerStatistic> existingPlayerstatistic = playerstatisticRepository.findByCourseIdAndUserId(
      courseId,
      player.getUserId()
    );
    if (existingPlayerstatistic.isPresent()) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        String.format("There is already a playerstatistic for userId %s in course %s", player.getUserId(), courseId)
      );
    }
    final Course course = courseService.getCourse(courseId);
    final World firstWorld = getFirstWorld(courseId);

    final PlayerStatistic playerstatistic = new PlayerStatistic();
    playerstatistic.setCourse(course);
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
   * @throws ResponseStatusException (404) when playerstatistic with its playerId in the course does not exist
   *                                 (400) when combination of world and dungeon in currentLocation does not exist
   * @param courseId the id of the course where the playerstatistic will be created
   * @param playerId the playerId of the player
   * @param playerstatisticDTO the updated parameters
   * @return the updated playerstatistic
   */
  public PlayerStatisticDTO updatePlayerStatisticInCourse(
    final int courseId,
    final String playerId,
    final PlayerStatisticDTO playerstatisticDTO
  ) {
    final PlayerStatistic playerstatistic = getPlayerStatisticFromCourse(courseId, playerId);

    if (playerstatisticDTO.getCurrentArea() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current area location is not specified");
    }

    try {
      playerstatistic.setCurrentArea(
        areaService.getAreaFromAreaLocationDTO(courseId, playerstatisticDTO.getCurrentArea())
      );
    } catch (ResponseStatusException exception) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Specified area does not exist");
    }
    return playerstatisticMapper.playerStatisticToPlayerstatisticDTO((playerstatisticRepository.save(playerstatistic)));
  }

  private World getFirstWorld(final int courseId) {
    return worldService.getWorldByIndexFromCourse(courseId, 1);
  }
}
