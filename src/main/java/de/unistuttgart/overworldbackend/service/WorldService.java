package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.World;
import de.unistuttgart.overworldbackend.data.WorldDTO;
import de.unistuttgart.overworldbackend.data.mapper.WorldMapper;
import de.unistuttgart.overworldbackend.repositories.WorldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class WorldService {

    @Autowired
    private WorldRepository worldRepository;

    @Autowired
    private WorldMapper worldMapper;

    /**
     * Get a world of a course
     *
     * @throws ResponseStatusException (404) if world with its static name could not be found in the course
     * @param courseId the id of the course the world is part of
     * @param worldIndex the index of the world searching for
     * @return the found world object
     */
    public World getWorldByIndexFromCourse(final int courseId, final int worldIndex) {
        return worldRepository
            .findByIndexAndCourseId(worldIndex, courseId)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format(
                        "There is no world with static name %s in the course with ID %s.",
                        worldIndex,
                        courseId
                    )
                )
            );
    }

    /**
     * Update a world by its id from a course.
     *
     * Only the topic name and active status is updatable.
     *
     * @throws ResponseStatusException (404) if course or world by its id do not exist
     * @param courseId the id of the course the world is part of
     * @param worldIndex the index of the world that should get updated
     * @param worldDTO the updated parameters
     * @return the updated world as DTO
     */
    public WorldDTO updateWorldFromCourse(final int courseId, final int worldIndex, final WorldDTO worldDTO) {
        final World world = getWorldByIndexFromCourse(courseId, worldIndex);
        world.setTopicName(worldDTO.getTopicName());
        world.setActive(worldDTO.isActive());
        final World updatedWorld = worldRepository.save(world);
        return worldMapper.worldToWorldDTO(updatedWorld);
    }
}
