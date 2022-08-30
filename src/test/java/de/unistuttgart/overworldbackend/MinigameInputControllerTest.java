package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.*;
import de.unistuttgart.overworldbackend.repositories.CourseRepository;
import de.unistuttgart.overworldbackend.repositories.MinigameTaskRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerStatisticRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerTaskActionLogRepository;
import java.util.*;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@AutoConfigureMockMvc
@Transactional
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MinigameInputControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private CourseRepository courseRepository;

  @Autowired
  private PlayerStatisticRepository playerstatisticRepository;

  @Autowired
  private MinigameTaskRepository minigameTaskRepository;

  @Autowired
  private PlayerTaskActionLogRepository playerTaskActionLogRepository;

  @Autowired
  private CourseMapper courseMapper;

  @Autowired
  private PlayerStatisticMapper playerstatisticMapper;

  @Autowired
  private WorldMapper worldMapper;

  @Autowired
  private DungeonMapper dungeonMapper;

  @Autowired
  private MinigameTaskMapper minigameTaskMapper;

  private final String API_URL = "/api/v1/overworld";
  private String fullURL;
  private ObjectMapper objectMapper;

  private Course initialCourse;
  private CourseDTO initialCourseDTO;

  private World initialWorld;
  private WorldDTO initialWorldDTO;

  private Dungeon initialDungeon;
  private DungeonDTO initialDungeonDTO;

  private PlayerStatistic initialPlayerStatistic;
  private PlayerStatisticDTO initialPlayerStatisticDTO;

  private MinigameTask initialMinigameTask;
  private MinigameTaskDTO initialMinigameTaskDTO;

  @BeforeEach
  public void createBasicData() {
    courseRepository.deleteAll();

    final MinigameTask dungeonMinigameTask1 = new MinigameTask();
    dungeonMinigameTask1.setConfigurationId(UUID.randomUUID());
    dungeonMinigameTask1.setGame("Bugfinder");
    dungeonMinigameTask1.setIndex(1);

    final MinigameTask dungeonMinigameTask2 = new MinigameTask();
    dungeonMinigameTask2.setConfigurationId(UUID.randomUUID());
    dungeonMinigameTask2.setGame("Chickenshock");
    dungeonMinigameTask2.setIndex(2);

    Set<MinigameTask> dungeonMinigameTasks = new HashSet<>();
    dungeonMinigameTasks.add(dungeonMinigameTask1);
    dungeonMinigameTasks.add(dungeonMinigameTask2);

    final Dungeon dungeon = new Dungeon();
    dungeon.setStaticName("Dark Dungeon");
    dungeon.setTopicName("Dark UML");
    dungeon.setActive(true);
    dungeon.setMinigameTasks(dungeonMinigameTasks);
    dungeon.setNpcs(Set.of());
    final List<Dungeon> dungeons = new ArrayList<>();
    dungeons.add(dungeon);

    final MinigameTask minigameTask = new MinigameTask();
    minigameTask.setConfigurationId(UUID.randomUUID());
    minigameTask.setGame("Bugfinder");
    minigameTask.setIndex(1);

    final Set<MinigameTask> minigameTasks = new HashSet<>();
    minigameTasks.add(minigameTask);

    final World world = new World();
    world.setStaticName("Winter Wonderland");
    world.setTopicName("UML Winter");
    world.setActive(true);
    world.setMinigameTasks(minigameTasks);
    world.setNpcs(Set.of());
    world.setDungeons(dungeons);
    final List<World> worlds = new ArrayList<>();
    worlds.add(world);

    final Course course = new Course("PSE", "SS-22", "Basic lecture of computer science students", true, worlds);

    initialCourse = courseRepository.save(course);
    initialCourseDTO = courseMapper.courseToCourseDTO(initialCourse);

    initialWorld = initialCourse.getWorlds().stream().findFirst().get();
    initialWorldDTO = worldMapper.worldToWorldDTO(initialWorld);

    initialDungeon = initialWorld.getDungeons().stream().findFirst().get();
    initialDungeonDTO = dungeonMapper.dungeonToDungeonDTO(initialDungeon);

    initialMinigameTask = initialWorld.getMinigameTasks().stream().findFirst().get();
    initialMinigameTaskDTO = minigameTaskMapper.minigameTaskToMinigameTaskDTO(initialMinigameTask);

    final PlayerStatistic playerstatistic = new PlayerStatistic();
    playerstatistic.setUserId("45h23o2j432");
    playerstatistic.setUsername("testUser");
    playerstatistic.setCourse(initialCourse);
    playerstatistic.setCurrentArea(initialWorld);
    playerstatistic.setKnowledge(new Random(10).nextLong());
    List<Area> unlockedAreas = new ArrayList<>();
    unlockedAreas.add(initialWorld);
    playerstatistic.setUnlockedAreas(unlockedAreas);
    playerstatistic.setCompletedDungeons(new ArrayList<>());
    initialPlayerStatistic = playerstatisticRepository.save(playerstatistic);
    initialPlayerStatisticDTO = playerstatisticMapper.playerStatisticToPlayerstatisticDTO(initialPlayerStatistic);

    assertNotNull(initialCourse.getCourseName());

    assertEquals(initialCourse.getId(), initialMinigameTask.getCourse().getId());
    assertEquals(initialCourse.getId(), initialPlayerStatistic.getCourse().getId());

    fullURL = "/internal";

    objectMapper = new ObjectMapper();
  }

  @Test
  void submitGameData() throws Exception {
    final PlayerTaskStatisticData playerTaskStatisticData = new PlayerTaskStatisticData();
    playerTaskStatisticData.setUserId(initialPlayerStatisticDTO.getUserId());
    playerTaskStatisticData.setGame(initialMinigameTask.getGame());
    playerTaskStatisticData.setConfigurationId(initialMinigameTask.getConfigurationId());
    playerTaskStatisticData.setScore(80);

    final String bodyValue = objectMapper.writeValueAsString(playerTaskStatisticData);

    final MvcResult result = mvc
      .perform(post(fullURL + "/submit-game-pass").content(bodyValue).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final PlayerTaskStatisticDTO playerTaskStatisticDTO = objectMapper.readValue(
      result.getResponse().getContentAsString(),
      PlayerTaskStatisticDTO.class
    );
    assertEquals(playerTaskStatisticData.getScore(), playerTaskStatisticDTO.getHighscore());
    assertEquals(2, initialDungeon.getMinigameTasks().size());
    final PlayerStatistic playerstatistic = playerstatisticRepository.findById(initialPlayerStatisticDTO.getId()).get();
    assertSame(0, playerstatistic.getCompletedDungeons().size());

    // check that action log was created
    final PlayerTaskActionLog actionLog = playerTaskActionLogRepository
      .findAll()
      .stream()
      .filter(log -> log.getPlayerTaskStatistic().getPlayerStatistic().getId().equals(initialPlayerStatistic.getId()))
      .findAny()
      .get();
    assertNotNull(actionLog);
    assertEquals(playerTaskStatisticData.getGame(), actionLog.getGame());
    assertEquals(playerTaskStatisticData.getConfigurationId(), actionLog.getConfigurationId());
    assertEquals(playerTaskStatisticData.getScore(), actionLog.getScore());
    assertEquals(
      playerTaskStatisticData.getUserId(),
      actionLog.getPlayerTaskStatistic().getPlayerStatistic().getUserId()
    );
  }

  @Test
  void submitAllMinigames_CompletesDungeon() throws Exception {
    assertSame(0, initialPlayerStatistic.getCompletedDungeons().size());
    for (final MinigameTaskDTO minigameTask : initialDungeonDTO.getMinigameTasks()) {
      final PlayerTaskStatisticData playerTaskStatisticData = new PlayerTaskStatisticData();
      playerTaskStatisticData.setUserId(initialPlayerStatisticDTO.getUserId());
      playerTaskStatisticData.setGame(minigameTask.getGame());
      playerTaskStatisticData.setConfigurationId(minigameTask.getConfigurationId());
      playerTaskStatisticData.setScore(80);

      final String bodyValue = objectMapper.writeValueAsString(playerTaskStatisticData);

      mvc
        .perform(post(fullURL + "/submit-game-pass").content(bodyValue).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    }

    final PlayerStatistic playerstatistic = playerstatisticRepository.findById(initialPlayerStatisticDTO.getId()).get();
    assertSame(1, playerstatistic.getCompletedDungeons().size());
  }

  @Test
  void submitGameData_PlayerDoesNotExist_ThrowNotFound() throws Exception {
    final PlayerTaskStatisticData playerTaskStatisticData = new PlayerTaskStatisticData();
    playerTaskStatisticData.setUserId(UUID.randomUUID().toString());
    playerTaskStatisticData.setGame(initialMinigameTask.getGame());
    playerTaskStatisticData.setConfigurationId(initialMinigameTask.getConfigurationId());
    playerTaskStatisticData.setScore(80);

    final String bodyValue = objectMapper.writeValueAsString(playerTaskStatisticData);

    mvc
      .perform(post(fullURL + "/submit-game-pass").content(bodyValue).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound());
  }

  @Test
  void submitGameData_MinigameDoesNotExist_ThrowNotFound() throws Exception {
    final PlayerTaskStatisticData playerTaskStatisticData = new PlayerTaskStatisticData();
    playerTaskStatisticData.setUserId(initialPlayerStatisticDTO.getUserId());
    playerTaskStatisticData.setGame(UUID.randomUUID().toString());
    playerTaskStatisticData.setConfigurationId(initialMinigameTask.getConfigurationId());
    playerTaskStatisticData.setScore(80);

    final String bodyValue = objectMapper.writeValueAsString(playerTaskStatisticData);

    mvc
      .perform(post(fullURL + "/submit-game-pass").content(bodyValue).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound());
  }

  @Test
  void createLectureAndSubmitMinigameData() throws Exception {
    String currentUrl = "/courses";
    final CourseInitialData toCreateCourse = new CourseInitialData("testName", "SS-22", "testDescription");
    final String bodyValueCourse = objectMapper.writeValueAsString(toCreateCourse);

    final MvcResult resultCourse = mvc
      .perform(post(currentUrl).content(bodyValueCourse).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isCreated())
      .andReturn();

    final CourseDTO createdCourse = objectMapper.readValue(
      resultCourse.getResponse().getContentAsString(),
      CourseDTO.class
    );

    WorldDTO createdWorld = createdCourse.getWorlds().get(0);

    MinigameTaskDTO updateMinigameTaskDTO = createdWorld.getMinigameTasks().stream().findFirst().get();
    final String newGame = "Crosswordpuzzle";
    final UUID newConfigurationId = UUID.randomUUID();
    updateMinigameTaskDTO.setGame(newGame);
    updateMinigameTaskDTO.setConfigurationId(newConfigurationId);
    final String bodyValueTask = objectMapper.writeValueAsString(updateMinigameTaskDTO);

    final MvcResult resultTask = mvc
      .perform(
        put(
          currentUrl +
          "/" +
          createdCourse.getId() +
          "/worlds/" +
          createdWorld.getIndex() +
          "/minigame-tasks/" +
          updateMinigameTaskDTO.getIndex()
        )
          .content(bodyValueTask)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andReturn();

    final MinigameTaskDTO updatedMinigameTaskDTOResult = objectMapper.readValue(
      resultTask.getResponse().getContentAsString(),
      MinigameTaskDTO.class
    );

    final Player newPlayer = new Player("n423l34213", "newPlayer");
    final String bodyValue = objectMapper.writeValueAsString(newPlayer);

    final MvcResult resultStatistic = mvc
      .perform(
        post("/courses/" + createdCourse.getId() + "/playerstatistics")
          .content(bodyValue)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isCreated())
      .andReturn();

    final PlayerStatisticDTO createdPlayerStatisticDTOResult = objectMapper.readValue(
      resultStatistic.getResponse().getContentAsString(),
      PlayerStatisticDTO.class
    );

    final PlayerTaskStatisticData playerTaskStatisticData = new PlayerTaskStatisticData();
    playerTaskStatisticData.setUserId(createdPlayerStatisticDTOResult.getUserId());
    playerTaskStatisticData.setGame(updatedMinigameTaskDTOResult.getGame());
    playerTaskStatisticData.setConfigurationId(updatedMinigameTaskDTOResult.getConfigurationId());
    playerTaskStatisticData.setScore(80);

    final String bodyValueMinigame = objectMapper.writeValueAsString(playerTaskStatisticData);

    final MvcResult resultPlayerStatistic = mvc
      .perform(post(fullURL + "/submit-game-pass").content(bodyValueMinigame).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final PlayerTaskStatisticDTO playerTaskStatisticDTO = objectMapper.readValue(
      resultPlayerStatistic.getResponse().getContentAsString(),
      PlayerTaskStatisticDTO.class
    );
    assertEquals(playerTaskStatisticData.getScore(), playerTaskStatisticDTO.getHighscore());
  }
}
