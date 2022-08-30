package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.Area;
import de.unistuttgart.overworldbackend.data.AreaLocationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AreaService {

  @Autowired
  private DungeonService dungeonService;

  @Autowired
  private WorldService worldService;

  public Area getAreaFromAreaLocationDTO(int courseId, AreaLocationDTO areaLocationDTO) {
    return areaLocationDTO.getDungeonIndex() != null
      ? dungeonService.getDungeonByIndexFromCourse(
        courseId,
        areaLocationDTO.getWorldIndex(),
        areaLocationDTO.getDungeonIndex()
      )
      : worldService.getWorldByIndexFromCourse(courseId, areaLocationDTO.getWorldIndex());
  }
}
