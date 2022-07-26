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
   * @throws ResponseStatusException when lecture with its id does not exist or a player with the playerId already has a playerstatistic
   * @param lectureId the id of the lecture where the playerstatistic will be created
   * @param player the player with its userId and username
   * @return the created lecture as DTO with all its generated worlds, dungeons, minigame tasks and npcs
   */
  public PlayerstatisticDTO createPlayerStatisticInLecture(final int lectureId, final Player player) {
    Optional<Playerstatistic> existingPlayerstatistic = playerstatisticRepository.findByLectureIdAndUserId(
      lectureId,
      player.getUserId()
    );
    if (existingPlayerstatistic.isPresent()) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        String.format("There is already a playerstatistic for userId %s in lecture %s", player.getUserId(), lectureId)
      );
    }
    Lecture lecture = lectureService.getLecture(lectureId);

    Playerstatistic playerstatistic = new Playerstatistic();
    playerstatistic.setLecture(lecture);
    playerstatistic.setCompletedDungeons(new ArrayList<>());
    playerstatistic.setUnlockedAreas(new ArrayList<>());
    playerstatistic.setUserId(player.getUserId());
    playerstatistic.setUsername(player.getUsername());

    AreaLocation areaLocation = new AreaLocation();
    areaLocation.setWorld(getFirstWorld(lectureId));
    playerstatistic.setCurrentAreaLocation(areaLocation);

    playerstatistic.setKnowledge(0);
    Playerstatistic savedPlayerstatistic = playerstatisticRepository.save(playerstatistic);
    return playerstatisticMapper.playerstatisticToPlayerstatisticDTO(savedPlayerstatistic);
  }

  private World getFirstWorld(int lectureId) {
    return worldService.getWorldByIndexFromLecture(lectureId, 1);
  }
}
