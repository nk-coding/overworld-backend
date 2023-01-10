package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.Area;
import de.unistuttgart.overworldbackend.data.AreaLocationDTO;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class AreaService {

    @Autowired
    private DungeonService dungeonService;

    @Autowired
    private WorldService worldService;

    public Area getAreaFromAreaLocationDTO(final int courseId, final AreaLocationDTO areaLocationDTO) {
        return areaLocationDTO.getDungeonIndex() != null && areaLocationDTO.getDungeonIndex() != 0
            ? dungeonService.getDungeonByIndexFromCourse(
                courseId,
                areaLocationDTO.getWorldIndex(),
                areaLocationDTO.getDungeonIndex()
            )
            : worldService.getWorldByIndexFromCourse(courseId, areaLocationDTO.getWorldIndex());
    }

    /**
     * Get an area of a course by its index.
     *
     * @throws ResponseStatusException (404) if area with the index was not found
     * @param courseId the id of the course the area is part of
     * @param worldIndex the index of the world or in case dungeonIndex is present, the worldIndex of the dungeon
     * @param dungeonIndex if area is a dungeon, the index of the dungeon
     * @return the found area object
     */
    public Area getAreaFromIndex(final int courseId, final int worldIndex, final Optional<Integer> dungeonIndex) {
        if (dungeonIndex.isEmpty()) {
            return worldService.getWorldByIndexFromCourse(courseId, worldIndex);
        } else {
            return dungeonService.getDungeonByIndexFromCourse(courseId, worldIndex, dungeonIndex.get());
        }
    }
}
