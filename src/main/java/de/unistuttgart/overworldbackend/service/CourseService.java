package de.unistuttgart.overworldbackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.overworldbackend.client.ChickenshockClient;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.config.CourseConfig;
import de.unistuttgart.overworldbackend.data.config.DungeonConfig;
import de.unistuttgart.overworldbackend.data.config.WorldConfig;
import de.unistuttgart.overworldbackend.data.mapper.CourseMapper;
import de.unistuttgart.overworldbackend.data.minigames.ChickenshockConfiguration;
import de.unistuttgart.overworldbackend.repositories.CourseRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CourseService {

  CourseConfig configCourse;

  @Autowired
  private CourseRepository courseRepository;

  @Autowired
  private CourseMapper courseMapper;

  @Autowired
  private ChickenshockClient chickenshockClient;

  public CourseService() {
    configCourse = new CourseConfig();
    ObjectMapper mapper = new ObjectMapper();

    InputStream inputStream = TypeReference.class.getResourceAsStream("/config.json");
    try {
      configCourse = mapper.readValue(inputStream, CourseConfig.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * @throws ResponseStatusException (404) when course by its id could not be found
   * @param id the id of the course searching for
   * @return the found course
   */
  public Course getCourse(final int id) {
    return courseRepository
      .findById(id)
      .orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("There is no course with id %s.", id))
      );
  }

  /**
   * Update a course by its id.
   *
   * Only the course name and description is updatable.
   *
   * @throws ResponseStatusException (404) if course, world or dungeon by its id do not exist
   * @param courseId the id of the course that should get updated
   * @param courseDTO the updated parameters
   * @return the updated course as DTO
   */
  public CourseDTO updateCourse(final int courseId, final CourseDTO courseDTO) {
    Course course = getCourse(courseId);
    course.setCourseName(courseDTO.getCourseName());
    course.setDescription(courseDTO.getDescription());
    course.setActive(courseDTO.isActive());
    course.setSemester(courseDTO.getSemester());
    Course updatedCourse = courseRepository.save(course);
    return courseMapper.courseToCourseDTO(updatedCourse);
  }

  /**
   * Create a course with initial data.
   *
   * Creates a course with pre generated worlds, dungeons, minigame tasks and npcs.
   *
   * @param courseInit the initial data with the course should be created with
   * @return the created course as DTO with all its generated worlds, dungeons, minigame tasks and npcs
   */
  public CourseDTO createCourse(final CourseInitialData courseInit) {
    List<World> worlds = new ArrayList<>();
    AtomicInteger worldId = new AtomicInteger(1);
    configCourse.getWorlds().forEach(worldConfig -> configureWorld(worlds, worldId.getAndIncrement(), worldConfig));

    Course course = new Course(
      courseInit.getCourseName(),
      courseInit.getSemester(),
      courseInit.getDescription(),
      false,
      worlds
    );
    courseRepository.save(course);
    return courseMapper.courseToCourseDTO(course);
  }

  /**
   * Delete a course by its id
   *
   * @throws ResponseStatusException (404) when course with its id does not exist
   * @param id the course that should be deleted
   * @return the deleted course as DTO
   */
  public CourseDTO deleteCourse(final int id) {
    Course course = getCourse(id);
    courseRepository.delete(course);
    return courseMapper.courseToCourseDTO(course);
  }

  private void configureWorld(List<World> worlds, int worldId, WorldConfig worldConfig) {
    Set<MinigameTask> minigames = new HashSet<>();
    Set<NPC> npcs = new HashSet<>();
    List<Dungeon> dungeons = new ArrayList<>();
    AtomicInteger dungeonId = new AtomicInteger(1);
    worldConfig
      .getDungeons()
      .forEach(dungeonConfig -> dungeons.add(configureDungeon(dungeonId.getAndIncrement(), dungeonConfig)));
    for (int minigameIndex = 1; minigameIndex <= worldConfig.getNumberOfMinigames(); minigameIndex++) {
      MinigameTask minigame = new MinigameTask(null, null, minigameIndex);
      minigames.add(minigame);
    }
    for (int npcIndex = 1; npcIndex <= worldConfig.getNumberOfNPCs(); npcIndex++) {
      NPC npc = new NPC(new ArrayList<>(), npcIndex);
      npcs.add(npc);
    }
    World world = new World(worldConfig.getStaticName(), "", false, minigames, npcs, dungeons, worldId);
    worlds.add(world);
  }

  private Dungeon configureDungeon(int dungeonId, DungeonConfig dungeonConfig) {
    Set<MinigameTask> minigames = new HashSet<>();
    Set<NPC> npcs = new HashSet<>();
    for (int minigameIndex = 1; minigameIndex <= dungeonConfig.getNumberOfMinigames(); minigameIndex++) {
      MinigameTask minigame = new MinigameTask(null, null, minigameIndex);
      minigames.add(minigame);
    }
    for (int npcIndex = 1; npcIndex <= dungeonConfig.getNumberOfNPCs(); npcIndex++) {
      NPC npc = new NPC(new ArrayList<>(), npcIndex);
      npcs.add(npc);
    }
    return new Dungeon(dungeonConfig.getStaticName(), "", false, minigames, npcs, dungeonId);
  }

  public CourseDTO cloneCourse(int id, CourseInitialData courseInitialData) {
    Course course = courseRepository
      .findById(id)
      .orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("There is no course with id %s.", id))
      );
    Course cloneCourse = courseMapper.courseDTOToCourse(createCourse(courseInitialData));
    for (int i = 0; i < course.getWorlds().size(); i++) {
      cloneWorld(course.getWorlds().get(i), cloneCourse.getWorlds().get(i));
    }
    return courseMapper.courseToCourseDTO(cloneCourse);
  }

  private void cloneWorld(World oldWorld, World newWorld) {
    oldWorld
      .getMinigameTasks()
      .forEach(minigameTask ->
        newWorld
          .getMinigameTasks()
          .stream()
          .filter(minigameTask1 -> minigameTask1.getIndex() == minigameTask.getIndex())
          .forEach(minigameTask1 -> {
            minigameTask1 = cloneMinigameTask(minigameTask);
          })
      );
    oldWorld.getNpcs().forEach(npc -> newWorld.getNpcs().stream().filter(npc1 -> npc1.getIndex() == npc.getIndex()).forEach(npc1 -> {
      npc1.setText(npc.getText());
    }));
    for(int i = 0; i< newWorld.getDungeons().size();i++){
      cloneDungeon(newWorld.getDungeons().get(i), oldWorld.getDungeons().get(i));
    }
  }

  private void cloneDungeon(Dungeon oldDungeon, Dungeon newDungeon) {
    oldDungeon
            .getMinigameTasks()
            .forEach(minigameTask ->
                    newDungeon
                            .getMinigameTasks()
                            .stream()
                            .filter(minigameTask1 -> minigameTask1.getIndex() == minigameTask.getIndex())
                            .forEach(minigameTask1 -> {
                              minigameTask1 = cloneMinigameTask(minigameTask);
                            })
            );
    oldDungeon.getNpcs().forEach(npc -> newDungeon.getNpcs().stream().filter(npc1 -> npc1.getIndex() == npc.getIndex()).forEach(npc1 -> {
      npc1.setText(npc.getText());
    }));
  }

  private MinigameTask cloneMinigameTask(MinigameTask minigameTask) {
    if (minigameTask.getGame() == null) {
      return new MinigameTask(null, null, minigameTask.getIndex());
    }
    if (minigameTask.getGame().equals("CHICKENSHOCK")) {
      ChickenshockConfiguration config = chickenshockClient.getConfiguration(minigameTask.getConfigurationId());
      config.setId(null);
      config = chickenshockClient.postConfiguration(config);
      MinigameTask newMinigame = new MinigameTask("CHICKENSHOCK", config.getId(), minigameTask.getIndex());
      return newMinigame;
    }
    return minigameTask;
  }
}
