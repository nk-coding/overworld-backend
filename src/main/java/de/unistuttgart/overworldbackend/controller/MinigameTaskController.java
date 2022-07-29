package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.overworldbackend.data.MinigameTaskDTO;
import de.unistuttgart.overworldbackend.service.MinigameTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Minigame Task", description = "Get and update minigame tasks from areas (world or dungeons)")
@RestController
@Slf4j
@RequestMapping("/courses/{courseId}/worlds/{worldIndex}")
public class MinigameTaskController {

  @Autowired
  private MinigameTaskService minigameTaskService;

  @Operation(summary = "Get all task from a world")
  @GetMapping("/minigame-tasks")
  public Set<MinigameTaskDTO> getMinigameTasksFromWorld(@PathVariable int courseId, @PathVariable int worldIndex) {
    log.debug("get tasks of world {} of course {}", worldIndex, courseId);
    return minigameTaskService.getMinigameTasksFromArea(courseId, worldIndex);
  }

  @Operation(summary = "Get all tasks from a dungeon")
  @GetMapping("/dungeons/{dungeonIndex}/minigame-tasks")
  public Set<MinigameTaskDTO> getMinigameTasksFromDungeon(
    @PathVariable int courseId,
    @PathVariable int worldIndex,
    @PathVariable int dungeonIndex
  ) {
    log.debug("get tasks of dungeon {} from world {} of course {}", dungeonIndex, worldIndex, courseId);
    return minigameTaskService.getMinigameTasksFromArea(courseId, worldIndex, dungeonIndex);
  }

  @Operation(summary = "Get a task by its index from a world")
  @GetMapping("/minigame-tasks/{taskIndex}")
  public MinigameTaskDTO getMinigameTaskFromWorld(
    @PathVariable int courseId,
    @PathVariable int worldIndex,
    @PathVariable int taskIndex
  ) {
    log.debug("get task {} of world {} of course {}", taskIndex, worldIndex, courseId);
    return minigameTaskService.getMinigameTaskFromArea(courseId, worldIndex, Optional.empty(), taskIndex);
  }

  @Operation(summary = "Get a task by its index from a dungeon")
  @GetMapping("/dungeons/{dungoenIndex}/minigame-tasks/{taskIndex}")
  public MinigameTaskDTO getMinigameTaskFromDungeon(
    @PathVariable int courseId,
    @PathVariable int worldIndex,
    @PathVariable int dungoenIndex,
    @PathVariable int taskIndex
  ) {
    log.debug("get task {} of dungeon {} from world {} of course {}", taskIndex, dungoenIndex, worldIndex, courseId);
    return minigameTaskService.getMinigameTaskFromArea(courseId, worldIndex, Optional.of(dungoenIndex), taskIndex);
  }

  @Operation(summary = "Update a task by its index from a world")
  @PutMapping("/minigame-tasks/{taskIndex}")
  public MinigameTaskDTO updateMinigameTasksFromWorld(
    @PathVariable int courseId,
    @PathVariable int worldIndex,
    @PathVariable int taskIndex,
    @RequestBody MinigameTaskDTO minigameTaskDTO
  ) {
    log.debug("update task {} of world {} of course {} with {}", taskIndex, worldIndex, courseId, minigameTaskDTO);
    return minigameTaskService.updateMinigameTaskFromArea(
      courseId,
      worldIndex,
      Optional.empty(),
      taskIndex,
      minigameTaskDTO
    );
  }

  @Operation(summary = "Update a task by index id from a dungeon")
  @PutMapping("/dungeons/{dungeonIndex}/minigame-tasks/{taskIndex}")
  public MinigameTaskDTO updateMinigameTasksFromDungeon(
    @PathVariable int courseId,
    @PathVariable int worldIndex,
    @PathVariable int dungeonIndex,
    @PathVariable int taskIndex,
    @RequestBody MinigameTaskDTO minigameTaskDTO
  ) {
    log.debug(
      "update task {} of dungeon {} from world {} of course {} with {}",
      taskIndex,
      dungeonIndex,
      worldIndex,
      courseId,
      minigameTaskDTO
    );
    return minigameTaskService.updateMinigameTaskFromArea(
      courseId,
      worldIndex,
      Optional.of(dungeonIndex),
      taskIndex,
      minigameTaskDTO
    );
  }
}
