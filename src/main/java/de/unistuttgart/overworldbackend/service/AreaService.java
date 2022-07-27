package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.Area;
import de.unistuttgart.overworldbackend.data.AreaLocationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AreaService {

  @Autowired
  private DungeonService dungeonService;

  @Autowired
  private WorldService worldService;

  public Area getAreaFromAreaLocationDTO(int lectureId, AreaLocationDTO areaLocationDTO) {
    return areaLocationDTO.getDungeonIndex() != null
      ? dungeonService.getDungeonByIndexFromLecture(
        lectureId,
        areaLocationDTO.getWorldIndex(),
        areaLocationDTO.getDungeonIndex()
      )
      : worldService.getWorldByIndexFromLecture(lectureId, areaLocationDTO.getWorldIndex());
  }
}
