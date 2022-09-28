package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.enums.Minigame;
import de.unistuttgart.overworldbackend.data.mapper.DungeonMapper;
import de.unistuttgart.overworldbackend.data.mapper.MinigameTaskMapper;
import de.unistuttgart.overworldbackend.data.mapper.WorldMapper;
import de.unistuttgart.overworldbackend.repositories.CourseRepository;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import de.unistuttgart.overworldbackend.repositories.DungeonRepository;
import de.unistuttgart.overworldbackend.repositories.MinigameTaskRepository;
import de.unistuttgart.overworldbackend.repositories.WorldRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@AutoConfigureMockMvc
@Transactional
@SpringBootTest
@Testcontainers
class MinigameTaskControllerTest {

  @Container
  public static PostgreSQLContainer postgresDB = new PostgreSQLContainer("postgres:14-alpine")
          .withDatabaseName("postgres")
          .withUsername("postgres")
          .withPassword("postgres");

  @DynamicPropertySource
  public static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgresDB::getJdbcUrl);
    registry.add("spring.datasource.username", postgresDB::getUsername);
    registry.add("spring.datasource.password", postgresDB::getPassword);
  }

  @Autowired
  private MockMvc mvc;

  @Autowired
  private CourseRepository courseRepository;

  @Autowired
  private WorldRepository worldRepository;

  @Autowired
  private DungeonRepository dungeonRepository;

  @Autowired
  private WorldMapper worldMapper;

  @Autowired
  private MinigameTaskMapper minigameTaskMapper;

  @Autowired
  private MinigameTaskRepository minigameTaskRepository;

  @Autowired
  private DungeonMapper dungeonMapper;

  private String fullURL;
  private ObjectMapper objectMapper;

  private Course initialCourse;
  private World initialWorld;
  private WorldDTO initialWorldDTO;
  private Dungeon initialDungeon;
  private DungeonDTO initialDungeonDTO;

  private MinigameTask initialTask1;
  private MinigameTaskDTO initialTaskDTO1;

  private MinigameTask initialTask2;
  private MinigameTaskDTO initialTaskDTO2;
  private MinigameTask initialTask3;
  private MinigameTaskDTO initialTaskDTO3;

  @BeforeEach
  public void createBasicData() {
    courseRepository.deleteAll();

    final MinigameTask minigameTask1 = new MinigameTask();
    minigameTask1.setConfigurationId(UUID.randomUUID());
    minigameTask1.setGame(Minigame.BUGFINDER);
    minigameTask1.setIndex(1);

    final MinigameTask minigameTask2 = new MinigameTask();
    minigameTask2.setConfigurationId(UUID.randomUUID());
    minigameTask2.setGame(Minigame.CHICKENSHOCK);
    minigameTask2.setIndex(2);

    final MinigameTask minigameTask3 = new MinigameTask();
    minigameTask3.setConfigurationId(UUID.randomUUID());
    minigameTask3.setGame(Minigame.CROSSWORDPUZZLE);
    minigameTask3.setIndex(3);

    Set<MinigameTask> dungeonMinigames = new HashSet<>();
    dungeonMinigames.add(minigameTask3);

    final Dungeon dungeon = new Dungeon();
    dungeon.setIndex(1);
    dungeon.setStaticName("Dungeon 1");
    dungeon.setTopicName("Testtopic");
    dungeon.setActive(true);
    dungeon.setMinigameTasks(dungeonMinigames);
    dungeon.setNpcs(Set.of());
    dungeon.setBooks(Set.of());
    dungeon.setConfigured(true);

    Set<MinigameTask> worldMinigames = new HashSet<>();
    worldMinigames.add(minigameTask1);
    worldMinigames.add(minigameTask2);

    final World world = new World();
    world.setIndex(1);
    world.setStaticName("Winter Wonderland");
    world.setTopicName("UML Winter");
    world.setActive(true);
    world.setMinigameTasks(worldMinigames);
    world.setNpcs(Set.of());
    world.setBooks(Set.of());
    world.setDungeons(Arrays.asList(dungeon));
    world.setConfigured(true);

    final Course course = new Course(
      "PSE",
      "SS-22",
      "Basic lecture of computer science students",
      true,
      Arrays.asList(world)
    );
    initialCourse = courseRepository.save(course);

    initialWorld = initialCourse.getWorlds().stream().findFirst().get();
    initialWorldDTO = worldMapper.worldToWorldDTO(initialWorld);

    initialDungeon = initialCourse.getWorlds().stream().findFirst().get().getDungeons().stream().findFirst().get();
    initialDungeonDTO = dungeonMapper.dungeonToDungeonDTO(initialDungeon);

    initialTask1 =
      initialWorld
        .getMinigameTasks()
        .stream()
        .filter(task -> task.getIndex() == 1)
        .findAny()
        .get();
    initialTaskDTO1 = minigameTaskMapper.minigameTaskToMinigameTaskDTO(initialTask1);

    initialTask2 =
      initialWorld
        .getMinigameTasks()
        .stream()
        .filter(task -> task.getIndex() == 2)
        .findAny()
        .get();
    initialTaskDTO2 = minigameTaskMapper.minigameTaskToMinigameTaskDTO(initialTask2);

    initialTask3 =
      initialDungeon
        .getMinigameTasks()
        .stream()
        .filter(task -> task.getIndex() == 3)
        .findAny()
        .get();
    initialTaskDTO3 = minigameTaskMapper.minigameTaskToMinigameTaskDTO(initialTask3);

    assertNotNull(initialWorld.getId());
    assertNotNull(initialWorldDTO.getId());

    assertNotNull(initialTask1.getId());
    assertNotNull(initialTaskDTO1.getId());
    assertNotNull(initialTask2.getId());
    assertNotNull(initialTaskDTO2.getId());

    fullURL = String.format("/courses/%d/worlds/%d", initialCourse.getId(), initialWorld.getIndex());

    objectMapper = new ObjectMapper();
  }

  @Test
  void getMinigameTasksFromWorld() throws Exception {
    final MvcResult result = mvc
      .perform(get(fullURL + "/minigame-tasks").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final Set<MinigameTaskDTO> minigameTasks = Set.of(
      objectMapper.readValue(result.getResponse().getContentAsString(), MinigameTaskDTO[].class)
    );

    assertSame(initialWorldDTO.getMinigameTasks().size(), minigameTasks.size());
    assertEquals(initialWorldDTO.getMinigameTasks(), minigameTasks);
  }

  @Test
  void getMinigameTasksFromDungeon() throws Exception {
    final MvcResult result = mvc
      .perform(
        get(fullURL + "/dungeons/" + initialDungeon.getIndex() + "/minigame-tasks")
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andReturn();

    final Set<MinigameTaskDTO> minigameTasks = Set.of(
      objectMapper.readValue(result.getResponse().getContentAsString(), MinigameTaskDTO[].class)
    );

    assertSame(initialDungeonDTO.getMinigameTasks().size(), minigameTasks.size());
    assertEquals(initialDungeonDTO.getMinigameTasks(), minigameTasks);
  }

  @Test
  void getMinigameTaskFromWorld() throws Exception {
    final MvcResult result = mvc
      .perform(get(fullURL + "/minigame-tasks/" + initialTask1.getIndex()).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final MinigameTaskDTO minigameTaskDTOResult = objectMapper.readValue(
      result.getResponse().getContentAsString(),
      MinigameTaskDTO.class
    );

    assertEquals(initialTaskDTO1, minigameTaskDTOResult);
  }

  @Test
  void getMinigameTaskFromWorld_DoesNotExist_ThrowsNotFound() throws Exception {
    mvc
      .perform(get(fullURL + "/minigame-tasks/" + Integer.MAX_VALUE).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  void getMinigameTaskFromDungeon() throws Exception {
    final MvcResult result = mvc
      .perform(
        get(fullURL + "/dungeons/" + initialDungeon.getIndex() + "/minigame-tasks/" + initialTask3.getIndex())
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andReturn();

    final MinigameTaskDTO minigameTaskDTOResult = objectMapper.readValue(
      result.getResponse().getContentAsString(),
      MinigameTaskDTO.class
    );

    assertEquals(initialTaskDTO3, minigameTaskDTOResult);
  }

  @Test
  void getMinigameTaskFromDungeon_DoesNotExist_ThrowsNotFound() throws Exception {
    mvc
      .perform(
        get(fullURL + "/dungeons/" + initialDungeon.getIndex() + "/minigame-tasks/" + Integer.MAX_VALUE)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  void updateMinigameTaskFromWorld() throws Exception {
    final Minigame newGame = Minigame.CROSSWORDPUZZLE;
    final String newDescription = "New Crosswordpuzzle game";
    final UUID newConfigurationId = UUID.randomUUID();
    final MinigameTaskDTO updateMinigameTaskDTO = minigameTaskMapper.minigameTaskToMinigameTaskDTO(initialTask1);
    updateMinigameTaskDTO.setGame(newGame);
    updateMinigameTaskDTO.setConfigurationId(newConfigurationId);
    updateMinigameTaskDTO.setDescription(newDescription);
    final String bodyValue = objectMapper.writeValueAsString(updateMinigameTaskDTO);

    final MvcResult result = mvc
      .perform(
        put(fullURL + "/minigame-tasks/" + initialTask1.getIndex())
          .content(bodyValue)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andReturn();

    final MinigameTaskDTO updatedMinigameTaskDTOResult = objectMapper.readValue(
      result.getResponse().getContentAsString(),
      MinigameTaskDTO.class
    );

    assertEquals(initialTaskDTO1.getId(), updatedMinigameTaskDTOResult.getId());
    assertEquals(Minigame.CROSSWORDPUZZLE, updatedMinigameTaskDTOResult.getGame());
    assertEquals(newConfigurationId, updatedMinigameTaskDTOResult.getConfigurationId());
    assertEquals(newDescription, updatedMinigameTaskDTOResult.getDescription());
    assertEquals(updateMinigameTaskDTO, updatedMinigameTaskDTOResult);
  }

  @Test
  void updateMinigameTaskFromDungeon() throws Exception {
    final Minigame newGame = Minigame.CHICKENSHOCK;
    final String newDescription = "New Chickenshock game";
    final UUID newConfigurationId = UUID.randomUUID();
    final MinigameTaskDTO updateMinigameTaskDTO = minigameTaskMapper.minigameTaskToMinigameTaskDTO(initialTask3);
    updateMinigameTaskDTO.setGame(newGame);
    updateMinigameTaskDTO.setConfigurationId(newConfigurationId);
    updateMinigameTaskDTO.setDescription(newDescription);
    final String bodyValue = objectMapper.writeValueAsString(updateMinigameTaskDTO);

    final MvcResult result = mvc
      .perform(
        put(fullURL + "/dungeons/" + initialDungeon.getIndex() + "/minigame-tasks/" + initialTask3.getIndex())
          .content(bodyValue)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andReturn();

    final MinigameTaskDTO updatedMinigameTaskDTOResult = objectMapper.readValue(
      result.getResponse().getContentAsString(),
      MinigameTaskDTO.class
    );

    assertEquals(initialTaskDTO3.getId(), updatedMinigameTaskDTOResult.getId());
    assertEquals(newGame, updatedMinigameTaskDTOResult.getGame());
    assertEquals(newConfigurationId, updatedMinigameTaskDTOResult.getConfigurationId());
    assertEquals(newDescription, updatedMinigameTaskDTOResult.getDescription());
  }

  @Test
  void removeMinigame_RemoveConfiguredFlag() throws Exception
  {
    final Minigame newGame = Minigame.NONE;
    final String newDescription = "";
    final UUID newConfigurationId = null;
    final MinigameTaskDTO updateMinigameTaskDTO = minigameTaskMapper.minigameTaskToMinigameTaskDTO(initialTask3);
    updateMinigameTaskDTO.setGame(newGame);
    updateMinigameTaskDTO.setConfigurationId(newConfigurationId);
    updateMinigameTaskDTO.setDescription(newDescription);
    final String bodyValue = objectMapper.writeValueAsString(updateMinigameTaskDTO);

    mvc.perform(
        put(fullURL + "/dungeons/" + initialDungeon.getIndex() + "/minigame-tasks/" + initialTask3.getIndex())
          .content(bodyValue)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk());

    Dungeon dungeon = dungeonRepository.findById(initialDungeon.getId()).get();
    assertEquals(false, dungeon.isConfigured());
  }

  @Test
  void removeMinigame_NotRemoveConfiguredFlag() throws Exception
  {
    final Minigame newGame = Minigame.NONE;
    final String newDescription = "";
    final UUID newConfigurationId = null;
    final MinigameTaskDTO updateMinigameTaskDTO = minigameTaskMapper.minigameTaskToMinigameTaskDTO(initialTask1);
    updateMinigameTaskDTO.setGame(newGame);
    updateMinigameTaskDTO.setConfigurationId(newConfigurationId);
    updateMinigameTaskDTO.setDescription(newDescription);
    final String bodyValue = objectMapper.writeValueAsString(updateMinigameTaskDTO);

    mvc.perform(
        put(fullURL + "/minigame-tasks/" + initialTask1.getIndex())
          .content(bodyValue)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk());

    World world = worldRepository.findById(initialWorld.getId()).get();
    assertEquals(true, world.isConfigured());
  }

  @Test
  void addMinigame_SetConfiguredFlag() throws Exception
  {
    initialDungeon.setConfigured(false);
    initialDungeon = dungeonRepository.save(initialDungeon);

    initialTask3.setGame(Minigame.NONE);
    initialTask3.setConfigurationId(null);
    initialTask3 = minigameTaskRepository.save(initialTask3);

    Minigame newGame = Minigame.CHICKENSHOCK;
    String newDescription = "New Chickenshock game";
    UUID newConfigurationId = UUID.randomUUID();
    MinigameTaskDTO updateMinigameTaskDTO = minigameTaskMapper.minigameTaskToMinigameTaskDTO(initialTask3);
    updateMinigameTaskDTO.setGame(newGame);
    updateMinigameTaskDTO.setConfigurationId(newConfigurationId);
    updateMinigameTaskDTO.setDescription(newDescription);
    String bodyValue = objectMapper.writeValueAsString(updateMinigameTaskDTO);

     mvc
      .perform(
        put(fullURL + "/dungeons/" + initialDungeon.getIndex() + "/minigame-tasks/" + initialTask3.getIndex())
          .content(bodyValue)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andReturn();

    Dungeon dungeon = dungeonRepository.findById(initialDungeon.getId()).get();
    assertEquals(true, dungeon.isConfigured());
  }
}
