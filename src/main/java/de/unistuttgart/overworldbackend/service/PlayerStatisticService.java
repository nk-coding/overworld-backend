package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.PlayerStatisticMapper;
import de.unistuttgart.overworldbackend.repositories.PlayerStatisticRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerTaskStatisticRepository;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class PlayerStatisticService {

  @Autowired
  private CourseService courseService;

  @Autowired
  private PlayerStatisticRepository playerstatisticRepository;

  @Autowired
  private PlayerTaskStatisticRepository playerTaskStatisticRepository;

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
    course.addPlayerStatistic(playerstatistic);
    PlayerStatistic savedPlayerStatistic = getPlayerStatisticFromCourse(courseId, player.getUserId());
    return playerstatisticMapper.playerStatisticToPlayerstatisticDTO(savedPlayerStatistic);
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

  /**
   * Check if a new area is unlocked.
   * Adds next area to unlockedAreas if player fulfilled the requirements.
   *
   * @param currentArea the area to check where the tasks may be finished
   * @param playerStatistic player statistics of the current player
   */
  public void checkForUnlockedAreas(Area currentArea, PlayerStatistic playerStatistic) {
    final List<PlayerTaskStatistic> playerTaskStatistics = playerTaskStatisticRepository.findByPlayerStatisticId(
      playerStatistic.getId()
    );
    boolean areaCompleted = currentArea
      .getMinigameTasks()
      .parallelStream()
      .filter(minigameTask -> minigameTask.getGame() != null && !minigameTask.getGame().equals("NONE"))
      .allMatch(minigameTask ->
        playerTaskStatistics
          .parallelStream()
          .filter(playerTaskStatistic -> playerTaskStatistic.getMinigameTask().equals(minigameTask))
          .anyMatch(PlayerTaskStatistic::isCompleted)
      );

    if (areaCompleted) {
      if (currentArea instanceof World) {
        World currentWorld = ((World) currentArea);

        currentWorld.getDungeons().sort(Comparator.comparingInt(Area::getIndex));

        for (Dungeon dungeon : currentWorld.getDungeons()) {
          if (dungeon.isActive()) {
            playerStatistic.addUnlockedArea(dungeon);
            return;
          }
        }

        try {
          playerStatistic.addUnlockedArea(
            worldService.getWorldByIndexFromCourse(playerStatistic.getCourse().getId(), currentWorld.getIndex() + 1)
          );
        } catch (Exception e) {
          //ignore
        }
      } else if (currentArea instanceof Dungeon) {
        Dungeon currentDungeon = (Dungeon) currentArea;

        List<Dungeon> otherDungeons = currentDungeon
          .getWorld()
          .getDungeons()
          .stream()
          .filter(dungeon -> dungeon.getIndex() > currentDungeon.getIndex())
          .collect(Collectors.toList());
        otherDungeons.sort(Comparator.comparingInt(Dungeon::getIndex));

        for (Dungeon dungeon : otherDungeons) {
          if (dungeon.isActive()) {
            playerStatistic.addUnlockedArea(dungeon);
            return;
          }
        }

        try {
          playerStatistic.addUnlockedArea(
            worldService.getWorldByIndexFromCourse(
              playerStatistic.getCourse().getId(),
              currentDungeon.getWorld().getIndex() + 1
            )
          );
        } catch (Exception e) {
          //ignore
        }
      }
    }
  }

  private World getFirstWorld(final int courseId) {
    return worldService.getWorldByIndexFromCourse(courseId, 1);
  }
}
