package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.Dungeon;
import de.unistuttgart.overworldbackend.data.DungeonDTO;
import de.unistuttgart.overworldbackend.data.mapper.DungeonMapper;
import de.unistuttgart.overworldbackend.repositories.DungeonRepository;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DungeonService {

  @Autowired
  private DungeonRepository dungeonRepository;

  @Autowired
  private DungeonMapper dungeonMapper;

  /**
   * Get a dungeon of a lecture and a world
   *
   * @throws ResponseStatusException (404) if dungeon with its static name could not be found in the lecture
   * @param lectureId the id of the lecture the dungeon is part of
   * @param staticWorldName the static name of the world the dungeon is part of
   * @param staticDungeonName the static name of the dungeon searching of
   * @return the found dungeon object
   */
  public Dungeon getDungeonByStaticNameFromLecture(
    final int lectureId,
    final String staticWorldName,
    final String staticDungeonName
  ) {
    return dungeonRepository
      .findByStaticNameAndLectureId(staticDungeonName, lectureId)
      .filter(dungeon -> dungeon.getWorld().getStaticName().equals(staticWorldName))
      .orElseThrow(() ->
        new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          String.format(
            "There is no dungeon with static name %s in world with static name %s in lecture with id %s.",
            staticDungeonName,
            staticWorldName,
            lectureId
          )
        )
      );
  }

  /**
   * Get dungeons of a lecture and a world
   *
   * @throws ResponseStatusException (404) if world with its static name could not be found in the lecture
   * @param lectureId the id of the lecture the dungeons are part of
   * @param staticWorldName the static name of the world the dungeons are part of
   * @return the found dungeon object
   */
  public Set<DungeonDTO> getDungeonsNameFromLecture(final int lectureId, final String staticWorldName) {
    return dungeonMapper.dungeonsToDungeonDTOs(
      dungeonRepository
        .findAllByLectureId(lectureId)
        .stream()
        .filter(dungeon -> dungeon.getWorld().getStaticName().equals(staticWorldName))
        .collect(Collectors.toSet())
    );
  }

  /**
   * Update a dungeon by its id from a lecture and a world.
   *
   * Only the topic name and active status is updatable.
   *
   * @throws ResponseStatusException (404) if lecture, world or dungeon by its id do not exist
   * @param lectureId the id of the lecture the dungeon is part of
   * @param staticWorldName the static name of the world where the dungeon should be listed
   * @param staticDungeonName the static name of the dungeon that should get updated
   * @param dungeonDTO the updated parameters
   * @return the updated dungeon as DTO
   */
  public DungeonDTO updateDungeonFromLecture(
    final int lectureId,
    final String staticWorldName,
    final String staticDungeonName,
    final DungeonDTO dungeonDTO
  ) {
    final Dungeon dungeon = getDungeonByStaticNameFromLecture(lectureId, staticWorldName, staticDungeonName);
    dungeon.setTopicName(dungeonDTO.getTopicName());
    dungeon.setActive(dungeonDTO.isActive());
    final Dungeon updatedDungeon = dungeonRepository.save(dungeon);
    return dungeonMapper.dungeonToDungeonDTO(updatedDungeon);
  }
}
