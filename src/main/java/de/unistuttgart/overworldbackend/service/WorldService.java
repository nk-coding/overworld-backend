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
   * @throws ResponseStatusException (404) if world with its id could not be found in the lecture
   * @param lectureId the id of the lecture the world is part of
   * @param worldId the id of the world searching for
   * @return the found world object
   */
  private World getWorldFromLectureOrThrowNotFound(final int lectureId, final UUID worldId) {
    return worldRepository
      .findByIdAndLectureId(worldId, lectureId)
      .orElseThrow(() ->
        new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          String.format("There is no world with id %s in lecture with id %s.", worldId, lectureId)
        )
      );
  }

  /**
   * Get a list of worlds of a lecture
   *
   * @throws ResponseStatusException (404) if lecture with its id does not exist
   * @param lectureId the id of the lecture the worlds are part of
   * @return a list of worlds as DTO
   */
  public Set<WorldDTO> getWorldsFromLecture(final int lectureId) {
    return worldMapper.worldsToWorldDTOs(worldRepository.findAllByLectureId(lectureId));
  }

  /**
   * Get a world by its id from a lecture
   *
   * @throws ResponseStatusException (404) if lecture or world by its id do not exist
   * @param lectureId the id of the lecture the world is part of
   * @param worldId the id of the world searching for
   * @return the found world as DTO
   */
  public WorldDTO getWorldFromLecture(final int lectureId, final UUID worldId) {
    return worldMapper.worldToWorldDTO(getWorldFromLectureOrThrowNotFound(lectureId, worldId));
  }

  /**
   * Update a world by its id from a lecture.
   *
   * Only the topic name and active status is updatable.
   *
   * @throws ResponseStatusException (404) if lecture or world by its id do not exist
   * @param lectureId the id of the lecture the world is part of
   * @param worldId the id of the world that should get updated
   * @param worldDTO the updated parameters
   * @return the updated world as DTO
   */
  public WorldDTO updateWorldFromLecture(final int lectureId, final UUID worldId, final WorldDTO worldDTO) {
    final World world = getWorldFromLectureOrThrowNotFound(lectureId, worldId);
    world.setTopicName(worldDTO.getTopicName());
    world.setActive(worldDTO.isActive());
    final World updatedWorld = worldRepository.save(world);
    return worldMapper.worldToWorldDTO(updatedWorld);
  }
}
