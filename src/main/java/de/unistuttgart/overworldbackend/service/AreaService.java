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
        return areaLocationDTO.getDungeonIndex() != null
            ? dungeonService.getDungeonByIndexFromCourse(
                courseId,
                areaLocationDTO.getWorldIndex(),
                areaLocationDTO.getDungeonIndex()
            )
            : worldService.getWorldByIndexFromCourse(courseId, areaLocationDTO.getWorldIndex());
    }

    public Area getAreaFromIndex(final int courseId, final int worldIndex, final Optional<Integer> dungeonIndex) {
        if (dungeonIndex.isEmpty()) {
            return worldService.getWorldByIndexFromCourse(courseId, worldIndex);
        } else {
            return dungeonService.getDungeonByIndexFromCourse(courseId, worldIndex, dungeonIndex.get());
        }
    }
}
