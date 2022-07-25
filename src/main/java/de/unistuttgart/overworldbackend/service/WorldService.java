package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.World;
import de.unistuttgart.overworldbackend.data.WorldDTO;
import de.unistuttgart.overworldbackend.data.mapper.WorldMapper;
import de.unistuttgart.overworldbackend.repositories.WorldRepository;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WorldService {

  @Autowired
  private WorldRepository worldRepository;

  @Autowired
  private WorldMapper worldMapper;

  /**
   * Get a world of a lecture
   *
   * @throws ResponseStatusException (404) if world with its static name could not be found in the lecture
   * @param lectureId the id of the lecture the world is part of
   * @param staticName the static name of the world searching for
   * @return the found world object
   */
  public World getWorldByStaticNameFromLecture(final int lectureId, final String staticName) {
    return worldRepository
      .findByStaticNameAndLectureId(staticName, lectureId)
      .orElseThrow(() ->
        new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          String.format("There is no world with static name %s in lecture with id %s.", staticName, lectureId)
        )
      );
  }

  /**
   * Update a world by its id from a lecture.
   *
   * Only the topic name and active status is updatable.
   *
   * @throws ResponseStatusException (404) if lecture or world by its id do not exist
   * @param lectureId the id of the lecture the world is part of
   * @param staticName the static name of the world that should get updated
   * @param worldDTO the updated parameters
   * @return the updated world as DTO
   */
  public WorldDTO updateWorldFromLecture(final int lectureId, final String staticName, final WorldDTO worldDTO) {
    final World world = getWorldByStaticNameFromLecture(lectureId, staticName);
    world.setTopicName(worldDTO.getTopicName());
    world.setActive(worldDTO.isActive());
    final World updatedWorld = worldRepository.save(world);
    return worldMapper.worldToWorldDTO(updatedWorld);
  }
}
