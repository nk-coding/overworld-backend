package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.data.WorldDTO;
import de.unistuttgart.overworldbackend.data.mapper.WorldMapper;
import de.unistuttgart.overworldbackend.repositories.WorldRepository;
import de.unistuttgart.overworldbackend.service.WorldService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "World", description = "Get and update worlds from a course")
@RestController
@Slf4j
@RequestMapping("/courses/{courseId}/worlds")
public class WorldController {

  @Autowired
  JWTValidatorService jwtValidatorService;

  @Autowired
  private WorldMapper worldMapper;

  @Autowired
  private WorldRepository worldRepository;

  @Autowired
  private WorldService worldService;

  @Operation(summary = "Get all worlds from a course by its id")
  @GetMapping("")
  public Set<WorldDTO> getWorlds(@PathVariable int courseId, @CookieValue("access_token") final String accessToken) {
    jwtValidatorService.validateTokenOrThrow(accessToken);
    log.debug("get worlds of course {}", courseId);
    return worldMapper.worldsToWorldDTOs(worldRepository.findAllByCourseId(courseId));
  }

  @Operation(summary = "Get a world by its index from a course")
  @GetMapping("/{worldIndex}")
  public WorldDTO getWorldByStaticName(
    @PathVariable int courseId,
    @PathVariable int worldIndex,
    @CookieValue("access_token") final String accessToken
  ) {
    jwtValidatorService.validateTokenOrThrow(accessToken);
    log.debug("get world by index {} of course {}", worldIndex, courseId);
    return worldMapper.worldToWorldDTO(worldService.getWorldByIndexFromCourse(courseId, worldIndex));
  }

  @Operation(summary = "Update a world by its index from a course")
  @PutMapping("/{worldIndex}")
  public WorldDTO updateWorld(
    @PathVariable int courseId,
    @PathVariable int worldIndex,
    @RequestBody WorldDTO worldDTO,
    @CookieValue("access_token") final String accessToken
  ) {
    jwtValidatorService.validateTokenOrThrow(accessToken);
    jwtValidatorService.hasRolesOrThrow(accessToken, List.of("lecturer"));
    log.debug("update world by index {} of course {} with {}", worldIndex, courseId, worldDTO);
    return worldService.updateWorldFromCourse(courseId, worldIndex, worldDTO);
  }
}
