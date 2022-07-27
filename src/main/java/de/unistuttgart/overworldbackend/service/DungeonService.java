package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.Dungeon;
import de.unistuttgart.overworldbackend.data.DungeonDTO;
import de.unistuttgart.overworldbackend.data.mapper.DungeonMapper;
import de.unistuttgart.overworldbackend.repositories.DungeonRepository;
import java.util.Set;
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
   * @param worldIndex the index of the world the dungeon is part of
   * @param dungeonIndex the index of the dungeon searching of
   * @return the found dungeon object
   */
  public Dungeon getDungeonByIndexFromLecture(final int lectureId, final int worldIndex, final int dungeonIndex) {
    return dungeonRepository
      .findAllByIndexAndLectureId(dungeonIndex, lectureId)
      .parallelStream()
      .filter(dungeon -> dungeon.getWorld().getIndex() == worldIndex)
      .findAny()
      .orElseThrow(() ->
        new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          String.format(
            "There is no dungeon with index %s in world with index %s in lecture with id %s.",
            dungeonIndex,
            worldIndex,
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
   * @param worldIndex the index of the world the dungeons are part of
   * @return the found dungeon object
   */
  public Set<DungeonDTO> getDungeonsFromWorld(final int lectureId, final int worldIndex) {
    return dungeonMapper.dungeonsToDungeonDTOs(
      dungeonRepository
        .findAllByLectureId(lectureId)
        .parallelStream()
        .filter(dungeon -> dungeon.getWorld().getIndex() == worldIndex)
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
   * @param worldIndex the index of the world where the dungeon should be listed
   * @param dungeonIndex the index of the dungeon that should get updated
   * @param dungeonDTO the updated parameters
   * @return the updated dungeon as DTO
   */
  public DungeonDTO updateDungeonFromLecture(
    final int lectureId,
    final int worldIndex,
    final int dungeonIndex,
    final DungeonDTO dungeonDTO
  ) {
    final Dungeon dungeon = getDungeonByIndexFromLecture(lectureId, worldIndex, dungeonIndex);
    dungeon.setTopicName(dungeonDTO.getTopicName());
    dungeon.setActive(dungeonDTO.isActive());
    final Dungeon updatedDungeon = dungeonRepository.save(dungeon);
    return dungeonMapper.dungeonToDungeonDTO(updatedDungeon);
  }
}
