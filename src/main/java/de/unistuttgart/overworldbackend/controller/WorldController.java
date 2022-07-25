package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.overworldbackend.data.WorldDTO;
import de.unistuttgart.overworldbackend.data.mapper.WorldMapper;
import de.unistuttgart.overworldbackend.repositories.WorldRepository;
import de.unistuttgart.overworldbackend.service.WorldService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Tag(name = "World", description = "Get and update worlds from a lecture")
@RestController
@Slf4j
@RequestMapping("/lectures/{lectureId}/worlds")
public class WorldController {

  @Autowired
  private WorldMapper worldMapper;

  @Autowired
  private WorldRepository worldRepository;

  @Autowired
  private WorldService worldService;

  @Operation(summary = "Get all worlds from a lecture by its id")
  @GetMapping("")
  public Set<WorldDTO> getWorlds(@PathVariable int lectureId) {
    log.debug("get worlds of lecture {}", lectureId);
    return worldMapper.worldsToWorldDTOs(worldRepository.findAllByLectureId(lectureId));
  }

  @Operation(summary = "Get a world by its static name from a lecture by its id")
  @GetMapping("/{worldIndex}")
  public WorldDTO getWorldByStaticName(@PathVariable int lectureId, @PathVariable int worldIndex) {
    log.debug("get world by index {} of lecture {}", worldIndex, lectureId);
    return worldMapper.worldToWorldDTO(worldService.getWorldByIndexFromLecture(lectureId, worldIndex));
  }

  @Operation(summary = "Update a world by static name id from a lecture by its id")
  @PutMapping("/{worldIndex}")
  public WorldDTO updateWorld(
    @PathVariable int lectureId,
    @PathVariable int worldIndex,
    @RequestBody WorldDTO worldDTO
  ) {
    log.debug("update world by index {} of lecture {}", worldIndex, lectureId);
    return worldService.updateWorldFromLecture(lectureId, worldIndex, worldDTO);
  }
}
