package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.Dungeon;
import de.unistuttgart.overworldbackend.data.MinigameTask;
import de.unistuttgart.overworldbackend.data.MinigameTaskDTO;
import de.unistuttgart.overworldbackend.data.mapper.MinigameTaskMapper;
import de.unistuttgart.overworldbackend.repositories.MinigameTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class MinigameTaskService {

  @Autowired
  private MinigameTaskRepository minigameTaskRepository;

  @Autowired
  private WorldService worldService;

  @Autowired
  private DungeonService dungeonService;

  @Autowired
  private MinigameTaskMapper minigameTaskMapper;

  /**
   * Get a minigame task of an area
   *
   * @throws ResponseStatusException (404) if area or task with its id could not be found in the lecture
   * @param lectureId the id of the lecture the minigame task is part of
   * @param worldIndex the index of the world the task is part of
   * @param dungeonIndex the index of the dungeon the task is part of
   * @param taskId the id of the task searching for
   * @return the found task object
   */
  public MinigameTask getMinigameTaskFromAreaOrThrowNotFound(
    final int lectureId,
    final int worldIndex,
    final Optional<Integer> dungeonIndex,
    final UUID taskId
  ) {
    if (dungeonIndex.isEmpty()) {
      return worldService
        .getWorldByIndexFromLecture(lectureId, worldIndex)
        .getMinigameTasks()
        .stream()
        .filter(minigameTask -> minigameTask.getId().equals(taskId))
        .findAny()
        .orElseThrow(() ->
          new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            String.format("Task not found in lecture %s in world %s.", lectureId, worldIndex)
          )
        );
    } else {
      return dungeonService
        .getDungeonByIndexFromLecture(lectureId, worldIndex, dungeonIndex.get())
        .getMinigameTasks()
        .stream()
        .filter(minigameTask -> minigameTask.getId().equals(taskId))
        .findAny()
        .orElseThrow(() ->
          new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            String.format(
              "Task not found in lecture %s in world %s in dungeon %s.",
              lectureId,
              worldIndex,
              dungeonIndex.get()
            )
          )
        );
    }
  }

  /**
   * Get a list of minigame tasks of a lecture and an area
   *
   * @throws ResponseStatusException (404) if lecture or area with its id do not exist
   * @param lectureId the id of the lecture the minigame tasks should be part of
   * @param worldIndex the index of the world where the minigame tasks should be part of
   * @return a list of minigame tasks as DTO
   */
  public Set<MinigameTaskDTO> getMinigameTasksFromArea(
    final int lectureId,
    final int worldIndex,
    final Optional<Integer> dungeonIndex
  ) {
    if (dungeonIndex.isEmpty()) {
      return minigameTaskMapper.minigameTasksToMinigameTaskDTOs(
        worldService.getWorldByIndexFromLecture(lectureId, worldIndex).getMinigameTasks()
      );
    } else {
      return minigameTaskMapper.minigameTasksToMinigameTaskDTOs(
        dungeonService.getDungeonByIndexFromLecture(lectureId, worldIndex, dungeonIndex.get()).getMinigameTasks()
      );
    }
  }

  /**
   * Get a list of minigame tasks of a lecture and a dungeon
   *
   * @throws ResponseStatusException (404) if lecture or area with its id do not exist
   * @param lectureId the id of the lecture the minigame tasks should be part of
   * @param worldIndex the index of the world where the minigame tasks should be part of
   * @param dungeonIndex the index of the dungeon where the minigame tasks should be part of
   * @return a list of minigame tasks as DTO
   */
  public Set<MinigameTaskDTO> getMinigameTasksFromArea(
    final int lectureId,
    final int worldIndex,
    final int dungeonIndex
  ) {
    final Dungeon dungeon = dungeonService.getDungeonByIndexFromLecture(lectureId, worldIndex, dungeonIndex);
    return minigameTaskMapper.minigameTasksToMinigameTaskDTOs(dungeon.getMinigameTasks());
  }

  /**
   * Get a minigame task by its id from a lecture and an area
   *
   * @throws ResponseStatusException (404) if lecture, area or task by its id do not exist
   * @param lectureId the id of the lecture the minigame task should be part of
   * @param worldIndex the index of the world where the minigame task should be part of
   * @param dungeonIndex the index of the dungen where the minigame task should be part of
   * @param taskId the id of the minigame task searching for
   * @return the found minigame task as DTO
   */
  public MinigameTaskDTO getMinigameTaskFromArea(
    final int lectureId,
    final int worldIndex,
    final Optional<Integer> dungeonIndex,
    final UUID taskId
  ) {
    final MinigameTask minigameTask = getMinigameTaskFromAreaOrThrowNotFound(
      lectureId,
      worldIndex,
      dungeonIndex,
      taskId
    );
    return minigameTaskMapper.minigameTaskToMinigameTaskDTO(minigameTask);
  }

  /**
   * Update a minigame task by its id from a lecture and an area.
   *
   * Only the game and configuration id is updatable.
   *
   * @throws ResponseStatusException (404) if lecture, world or dungeon by its id do not exist
   * @param lectureId the id of the lecture the minigame task should be part of
   * @param worldIndex the index of the world where the minigame task should be part of
   * @param dungeonIndex the index of the dungeon where the minigame task should be part of
   * @param taskId the id of the minigame task that should get updated
   * @param taskDTO the updated parameters
   * @return the updated area as DTO
   */
  public MinigameTaskDTO updateMinigameTaskFromArea(
    final int lectureId,
    final int worldIndex,
    final Optional<Integer> dungeonIndex,
    final UUID taskId,
    final MinigameTaskDTO taskDTO
  ) {
    final MinigameTask minigameTask = getMinigameTaskFromAreaOrThrowNotFound(
      lectureId,
      worldIndex,
      dungeonIndex,
      taskId
    );
    minigameTask.setGame(taskDTO.getGame());
    minigameTask.setConfigurationId(taskDTO.getConfigurationId());
    final MinigameTask updatedMinigameTask = minigameTaskRepository.save(minigameTask);
    return minigameTaskMapper.minigameTaskToMinigameTaskDTO(updatedMinigameTask);
  }
}
