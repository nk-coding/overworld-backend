package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.PlayerstatisticMapper;
import de.unistuttgart.overworldbackend.repositories.PlayerstatisticRepository;
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
  private PlayerstatisticRepository playerstatisticRepository;

  @Autowired
  private PlayerstatisticMapper playerstatisticMapper;

  @Autowired
  private WorldService worldService;

  @Autowired
  private DungeonService dungeonService;

  /**
   * @throws ResponseStatusException when playerstatistic with lectureId and userId could not be found
   * @param lectureId the id of the lecture
   * @param userId the playerId of the player searching for
   * @return the found playerstatistic
   */
  public Playerstatistic getPlayerStatisticFromLecture(final int lectureId, final String userId) {
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
  public PlayerstatisticDTO createPlayerStatisticInLecture(final int lectureId, final Player player) {
    final Optional<Playerstatistic> existingPlayerstatistic = playerstatisticRepository.findByLectureIdAndUserId(
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

    final Playerstatistic playerstatistic = new Playerstatistic();
    playerstatistic.setLecture(lecture);
    playerstatistic.setCompletedDungeons(new ArrayList<>());
    playerstatistic.setUnlockedAreas(new ArrayList<>());
    playerstatistic.setUserId(player.getUserId());
    playerstatistic.setUsername(player.getUsername());

    final AreaLocation areaLocation = new AreaLocation(getFirstWorld(lectureId));
    playerstatistic.setCurrentAreaLocation(areaLocation);

    playerstatistic.setKnowledge(0);
    return playerstatisticMapper.playerstatisticToPlayerstatisticDTO(playerstatisticRepository.save(playerstatistic));
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
  public PlayerstatisticDTO updatePlayerStatisticInLecture(
    final int lectureId,
    final String playerId,
    final PlayerstatisticDTO playerstatisticDTO
  ) {
    final Playerstatistic playerstatistic = getPlayerStatisticFromLecture(lectureId, playerId);

    if (playerstatisticDTO.getCurrentAreaLocation() == null) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        String.format("Current area location is not specified")
      );
    }

    final AreaLocation areaLocation = areaLocationDTOToAreaLocation(
      lectureId,
      playerstatisticDTO.getCurrentAreaLocation()
    );
    playerstatistic.setCurrentAreaLocation(areaLocation);
    return playerstatisticMapper.playerstatisticToPlayerstatisticDTO((playerstatisticRepository.save(playerstatistic)));
  }

  private World getFirstWorld(final int lectureId) {
    return worldService.getWorldByIndexFromLecture(lectureId, 1);
  }

  /**
   * @throws ResponseStatusException (404) if world or dungeon with its indexes does not exist
   * @param lectureId the id of the lecture
   * @param areaLocationDTO the updated parameters
   * @return the area location object
   */
  private AreaLocation areaLocationDTOToAreaLocation(final int lectureId, final AreaLocationDTO areaLocationDTO) {
    try {
      final World world = worldService.getWorldByIndexFromLecture(lectureId, areaLocationDTO.getWorldIndex());
      Dungeon dungeon = null;
      if (areaLocationDTO.getDungeonIndex() != null) {
        dungeon =
          dungeonService.getDungeonByIndexFromLecture(lectureId, world.getIndex(), areaLocationDTO.getDungeonIndex());
      }
      return new AreaLocation(world, dungeon);
    } catch (final ResponseStatusException exception) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Combination of world and dungeon does not exist");
    }
  }
}
