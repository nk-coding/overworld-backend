package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.data.DungeonDTO;
import de.unistuttgart.overworldbackend.data.mapper.DungeonMapper;
import de.unistuttgart.overworldbackend.service.DungeonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Dungeon", description = "Get and update dungeons from worlds")
@RestController
@Slf4j
@RequestMapping("/courses/{courseId}/worlds/{worldIndex}/dungeons")
public class DungeonController {

  @Autowired
  JWTValidatorService jwtValidatorService;

  @Autowired
  private DungeonService dungeonService;

  @Autowired
  private DungeonMapper dungeonMapper;

  @Operation(summary = "Get all dungeons of a world")
  @GetMapping("")
  public Set<DungeonDTO> getDungeons(
    @PathVariable final int courseId,
    @PathVariable final int worldIndex,
    @CookieValue("access_token") final String accessToken
  ) {
    jwtValidatorService.validateTokenOrThrow(accessToken);
    log.debug("get dungeons of world {} of course {}", worldIndex, courseId);
    return dungeonService.getDungeonsFromWorld(courseId, worldIndex);
  }

  @Operation(summary = "Get a dungeon by its index in a world")
  @GetMapping("/{dungeonIndex}")
  public DungeonDTO getDungeon(
    @PathVariable final int courseId,
    @PathVariable final int worldIndex,
    @PathVariable final int dungeonIndex,
    @CookieValue("access_token") final String accessToken
  ) {
    jwtValidatorService.validateTokenOrThrow(accessToken);
    log.debug("get dungeon {} of world {} of course {}", dungeonIndex, worldIndex, courseId);
    return dungeonMapper.dungeonToDungeonDTO(
      dungeonService.getDungeonByIndexFromCourse(courseId, worldIndex, dungeonIndex)
    );
  }

  @Operation(summary = "Update a dungeon by its index in a world")
  @PutMapping("/{dungeonIndex}")
  public DungeonDTO updateDungeon(
    @PathVariable final int courseId,
    @PathVariable final int worldIndex,
    @PathVariable final int dungeonIndex,
    @RequestBody final DungeonDTO dungeonDTO,
    @CookieValue("access_token") final String accessToken
  ) {
    jwtValidatorService.validateTokenOrThrow(accessToken);
    jwtValidatorService.hasRolesOrThrow(accessToken, List.of("lecturer"));
    log.debug("update dungeon {} of world {} of course {} with {}", dungeonIndex, worldIndex, courseId, dungeonDTO);
    return dungeonService.updateDungeonFromCourse(courseId, worldIndex, dungeonIndex, dungeonDTO);
  }
}
