package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.Dungeon;
import de.unistuttgart.overworldbackend.data.MinigameTask;
import de.unistuttgart.overworldbackend.data.MinigameTaskDTO;
import de.unistuttgart.overworldbackend.data.World;
import de.unistuttgart.overworldbackend.data.WorldDTO;
import de.unistuttgart.overworldbackend.data.mapper.MinigameTaskMapper;
import de.unistuttgart.overworldbackend.repositories.MinigameTaskRepository;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
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
   * @throws ResponseStatusException (404) if area or task with its id could not be found in the course
   * @param courseId the id of the course the minigame task is part of
   * @param worldIndex the index of the world the task is part of
   * @param dungeonIndex the index of the dungeon the task is part of
   * @param taskIndex the index of the task searching for
   * @return the found task object
   */
  public MinigameTask getMinigameTaskFromAreaOrThrowNotFound(
    final int courseId,
    final int worldIndex,
    final Optional<Integer> dungeonIndex,
    final int taskIndex
  ) {
    if (dungeonIndex.isEmpty()) {
      return worldService
        .getWorldByIndexFromCourse(courseId, worldIndex)
        .getMinigameTasks()
        .parallelStream()
        .filter(minigameTask -> minigameTask.getIndex() == taskIndex)
        .findAny()
        .orElseThrow(() ->
          new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            String.format("Task not found with index %s in course %s in world %s.", taskIndex, courseId, worldIndex)
          )
        );
    } else {
      return dungeonService
        .getDungeonByIndexFromCourse(courseId, worldIndex, dungeonIndex.get())
        .getMinigameTasks()
        .parallelStream()
        .filter(minigameTask -> minigameTask.getIndex() == taskIndex)
        .findAny()
        .orElseThrow(() ->
          new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            String.format(
              "Task not found with index %s in course %s in world %s in dungeon %s.",
              taskIndex,
              courseId,
              worldIndex,
              dungeonIndex.get()
            )
          )
        );
    }
  }

  /**
   * Get a list of minigame tasks of a course and an area
   *
   * @throws ResponseStatusException (404) if course or area with its id do not exist
   * @param courseId the id of the course the minigame tasks should be part of
   * @param worldIndex the index of the world where the minigame tasks should be part of
   * @return a list of minigame tasks as DTO
   */
  public Set<MinigameTaskDTO> getMinigameTasksFromArea(final int courseId, final int worldIndex) {
    return minigameTaskMapper.minigameTasksToMinigameTaskDTOs(
      worldService.getWorldByIndexFromCourse(courseId, worldIndex).getMinigameTasks()
    );
  }

  /**
   * Get a list of minigame tasks of a course and a dungeon
   *
   * @throws ResponseStatusException (404) if course or area with its id do not exist
   * @param courseId the id of the course the minigame tasks should be part of
   * @param worldIndex the index of the world where the minigame tasks should be part of
   * @param dungeonIndex the index of the dungeon where the minigame tasks should be part of
   * @return a list of minigame tasks as DTO
   */
  public Set<MinigameTaskDTO> getMinigameTasksFromArea(
    final int courseId,
    final int worldIndex,
    final int dungeonIndex
  ) {
    final Dungeon dungeon = dungeonService.getDungeonByIndexFromCourse(courseId, worldIndex, dungeonIndex);
    return minigameTaskMapper.minigameTasksToMinigameTaskDTOs(dungeon.getMinigameTasks());
  }

  /**
   * Get a minigame task by its index from a course and an area
   *
   * @throws ResponseStatusException (404) if course, area or task by its id do not exist
   * @param courseId the id of the course the minigame task should be part of
   * @param worldIndex the index of the world where the minigame task should be part of
   * @param dungeonIndex the index of the dungen where the minigame task should be part of
   * @param taskIndex the index of the minigame task searching for
   * @return the found minigame task as DTO
   */
  public MinigameTaskDTO getMinigameTaskFromArea(
    final int courseId,
    final int worldIndex,
    final Optional<Integer> dungeonIndex,
    final int taskIndex
  ) {
    final MinigameTask minigameTask = getMinigameTaskFromAreaOrThrowNotFound(
      courseId,
      worldIndex,
      dungeonIndex,
      taskIndex
    );
    return minigameTaskMapper.minigameTaskToMinigameTaskDTO(minigameTask);
  }

  /**
   * Update a minigame task by index id from a course and an area.
   *
   * Only the game and configuration id is updatable.
   *
   * @throws ResponseStatusException (404) if course, world or dungeon by its id do not exist
   * @param courseId the id of the course the minigame task should be part of
   * @param worldIndex the index of the world where the minigame task should be part of
   * @param dungeonIndex the index of the dungeon where the minigame task should be part of
   * @param taskIndex the index of the minigame task that should get updated
   * @param taskDTO the updated parameters
   * @return the updated area as DTO
   */
  public MinigameTaskDTO updateMinigameTaskFromArea(
    final int courseId,
    final int worldIndex,
    final Optional<Integer> dungeonIndex,
    final int taskIndex,
    final MinigameTaskDTO taskDTO
  ) {
    final MinigameTask minigameTask = getMinigameTaskFromAreaOrThrowNotFound(
      courseId,
      worldIndex,
      dungeonIndex,
      taskIndex
    );
    boolean configuredBefore = isMinigameConfigured(minigameTask.taskDTO.getConfigurationId());
    boolean configuredAfter = isMinigameConfigured(taskDTO.getConfigurationId());
    minigameTask.setGame(taskDTO.getGame());
    minigameTask.setConfigurationId(taskDTO.getConfigurationId());
    minigameTask.setDescription(taskDTO.getDescription());
    final MinigameTask updatedMinigameTask = minigameTaskRepository.save(minigameTask);
    if(configuredAfter && !configuredBefore)
    {
      minigameAdded(courseId, worldIndex, dungeonIndex);
    }
    else if(!configuredAfter && configuredBefore)
    {
      minigameRemoved(courseId, worldIndex, dungeonIndex);
    }
    return minigameTaskMapper.minigameTaskToMinigameTaskDTO(updatedMinigameTask);
  }

  /**
   * Checks if a minigame is configured or not
   * @param configurationId the configurationId of the minigame
   * @return true if minigame is configured, false otherwise
   */
  private boolean isMinigameConfigured(final String configurationId)
  {
    return !((configurationId == null) || (configurationId == "NONE"));
  }

  /**
   * If a minigame is added, the configured flag of the area is set
   * @param courseId the if of the course the minigame is in
   * @param worldIndex the index of the world the minigame is in
   * @param dungeonIndex the index of the dungeon the minigame is in
   */
  private void minigameAdded(final int courseId, final int worldIndex, final Optional<Integer> dungeonIndex)
  {
    if(dungeonIndex.isEmpty())
    {
      World world = worldService.getWorldByIndexFromCourse(courseId, worldIndex);
      if(!world.isConfigured())
      {
        WorldDTO worldDTO = new WorldDTO(world);
        worldDTO.setConfigured(true);
        worldService.updateWorldFromCourse(courseId, worldIndex, worldDTO);
      }
    }
    else
    {
      Dungeon dungeon = dungeonService.getDungeonByIndexFromCourse(courseId, worldIndex, dungeonIndex);
      if(!dungeon.isConfigured())
      {
        DungeonDTO dungeonDTO = new DungeonDTO(dungeon);
        dungeonDTO.setConfigured(true);
        dungeonService.updateDungeonFromCourse(courseId, worldIndex, dungeonIndex, dungeonDTO);
      }
    }
  }

  /**
   * If a minigame is removed, it is checked whether the configured flag of the area needs to be changed
   * @param courseId the if of the course the minigame is in
   * @param worldIndex the index of the world the minigame is in
   * @param dungeonIndex the index of the dungeon the minigame is in
   */
  private void minigameRemoved(final int courseId, final int worldIndex, final Optional<Integer> dungeonIndex)
  {
    Set<MinigameTask> minigames;
    if(dungeonIndex.isEmpty())
    {
      World world = worldService.getWorldByIndexFromCourse(courseId, worldIndex);
      minigames = world.getMinigameTasks();
      int amountOfConfiguredMinigames = getAmountOfConfiguredMinigames(minigames);
      if(amountOfConfiguredMinigames <= 0)
      {
        world.setConfigured(false);
        WorldDTO worldDTO = new WorldDTO(world);
        worldService.updateWorldFromCourse(courseId, worldIndex, worldDTO);
      }
    }
    else
    {
      Dungeon dungeon = dungeonService.getDungeonByIndexFromCourse(courseId, worldIndex, dungeonIndex);
      minigames = dungeon.getMinigameTasks();
      int amountOfConfiguredMinigames = getAmountOfConfiguredMinigames(minigames);
      if(amountOfConfiguredMinigames <= 0)
      {
        dungeon.setConfigured(false);
        DungeonDTO dungeonDTO = new DungeonDTO(dungeon);
        dungeonService.updateDungeonFromCourse(courseId, worldIndex, dungeonIndex, dungeonDTO);
      }
    }
  }

  /**
   * Counts the amount of configured minigames in the given set
   * @param minigames the minigames to check
   * @return the amount of configured minigames
   */
  private int getAmountOfConfiguredMinigames(Set<MinigameTask> minigames)
  {
    int amountOfConfiguredMinigames = 0;
    for(MinigameTask minigame : minigames)
    {
      if(isMinigameConfigured(minigame.getConfigurationId))
      {
        amountOfConfiguredMinigames++;
      }
    }
    return amountOfConfiguredMinigames;
  }
}
