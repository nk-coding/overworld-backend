package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.Dungeon;
import de.unistuttgart.overworldbackend.data.DungeonDTO;
import de.unistuttgart.overworldbackend.data.mapper.DungeonMapper;
import de.unistuttgart.overworldbackend.repositories.DungeonRepository;
import java.util.Set;
import java.util.UUID;
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
   * @throws ResponseStatusException if dungeon with its id could not be found in the lecture
   * @param lectureId the id of the lecture the dungeon is part of
   * @param dungeonId the id of the dungeon searching for
   * @return the found dungeon object
   */
  private Dungeon getDungeonFromLectureOrThrowNotFound(final int lectureId, final UUID worldId, final UUID dungeonId) {
    return dungeonRepository
      .findByIdAndLectureIdAndWorldId(dungeonId, lectureId, worldId)
      .orElseThrow(() ->
        new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          String.format("There is no dungeon with id %s in lecture with id %s.", dungeonId, lectureId)
        )
      );
  }

  /**
   * Get a list of dungeons of a lecture and a world
   *
   * @throws ResponseStatusException if lecture or world with its id do not exist
   * @param lectureId the id of the lecture the dungeons are part of
   * @param worldId the id of the world where the dungeons should be listed
   * @return a list of dungeons as DTO
   */
  public Set<DungeonDTO> getDungeonsFromLectureAndWorld(final int lectureId, final UUID worldId) {
    return dungeonMapper.dungeonsToDungeonDTOs(dungeonRepository.findAllByLectureIdAndWorldId(lectureId, worldId));
  }

  /**
   * Get a dungeon by its id from a lecture and a world
   *
   * @throws ResponseStatusException if lecture, world or dungeon by its id do not exist
   * @param lectureId the id of the lecture the dungeon is part of
   * @param worldId the id of the world where the dungeon should be listed
   * @param dungeonId the id of the dungeon searching for
   * @return the found dungeon as DTO
   */
  public DungeonDTO getDungeonFromLectureAndWorld(final int lectureId, final UUID worldId, final UUID dungeonId) {
    return dungeonMapper.dungeonToDungeonDTO(getDungeonFromLectureOrThrowNotFound(lectureId, worldId, dungeonId));
  }

  /**
   * Update a dungeon by its id from a lecture and a world.
   *
   * Only the topic name and active status is updatable.
   *
   * @throws ResponseStatusException if lecture, world or dungeon by its id do not exist
   * @param lectureId the id of the lecture the dungeon is part of
   * @param worldId the id of the world where the dungeon should be listed
   * @param dungeonId the id of the dungeon that should get updated
   * @param dungeonDTO the updated parameters
   * @return the updated dungeon as DTO
   */
  public DungeonDTO updateDungeonFromLecture(
    final int lectureId,
    final UUID worldId,
    final UUID dungeonId,
    final DungeonDTO dungeonDTO
  ) {
    final Dungeon dungeon = getDungeonFromLectureOrThrowNotFound(lectureId, worldId, dungeonId);
    dungeon.setTopicName(dungeonDTO.getTopicName());
    dungeon.setActive(dungeonDTO.isActive());
    final Dungeon updatedDungeon = dungeonRepository.save(dungeon);
    return dungeonMapper.dungeonToDungeonDTO(updatedDungeon);
  }
}
