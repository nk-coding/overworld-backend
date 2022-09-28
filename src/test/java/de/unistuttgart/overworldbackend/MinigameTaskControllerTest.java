package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.enums.Minigame;
import de.unistuttgart.overworldbackend.data.mapper.DungeonMapper;
import de.unistuttgart.overworldbackend.data.mapper.MinigameTaskMapper;
import de.unistuttgart.overworldbackend.data.mapper.WorldMapper;
import de.unistuttgart.overworldbackend.repositories.CourseRepository;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import javax.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

  @MockBean
  JWTValidatorService jwtValidatorService;

  final Cookie cookie = new Cookie("access_token", "testToken");

  @Autowired
  private CourseRepository courseRepository;

  @Autowired
  private WorldMapper worldMapper;

  @Autowired
  private MinigameTaskMapper minigameTaskMapper;

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

    final Dungeon dungeon = new Dungeon();
    dungeon.setIndex(1);
    dungeon.setStaticName("Dungeon 1");
    dungeon.setTopicName("Testtopic");
    dungeon.setActive(true);
    dungeon.setMinigameTasks(Set.of(minigameTask3));
    dungeon.setNpcs(Set.of());
    dungeon.setBooks(Set.of());

    final World world = new World();
    world.setIndex(1);
    world.setStaticName("Winter Wonderland");
    world.setTopicName("UML Winter");
    world.setActive(true);
    world.setMinigameTasks(Set.of(minigameTask1, minigameTask2));
    world.setNpcs(Set.of());
    world.setBooks(Set.of());
    world.setDungeons(Arrays.asList(dungeon));

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
        .filter(task -> task.getId().equals(minigameTask1.getId()))
        .findAny()
        .get();
    initialTaskDTO1 = minigameTaskMapper.minigameTaskToMinigameTaskDTO(initialTask1);

    initialTask2 =
      initialWorld
        .getMinigameTasks()
        .stream()
        .filter(task -> task.getId().equals(minigameTask2.getId()))
        .findAny()
        .get();
    initialTaskDTO2 = minigameTaskMapper.minigameTaskToMinigameTaskDTO(initialTask2);

    initialTask3 =
      initialDungeon
        .getMinigameTasks()
        .stream()
        .filter(task -> task.getId().equals(minigameTask3.getId()))
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

    doNothing().when(jwtValidatorService).validateTokenOrThrow("testToken");
    when(jwtValidatorService.extractUserId("testToken")).thenReturn("testUser");
  }

  @Test
  void getMinigameTasksFromWorld() throws Exception {
    final MvcResult result = mvc
      .perform(get(fullURL + "/minigame-tasks").cookie(cookie).contentType(MediaType.APPLICATION_JSON))
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
          .cookie(cookie)
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
      .perform(
        get(fullURL + "/minigame-tasks/" + initialTask1.getIndex())
          .cookie(cookie)
          .contentType(MediaType.APPLICATION_JSON)
      )
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
      .perform(
        get(fullURL + "/minigame-tasks/" + Integer.MAX_VALUE).cookie(cookie).contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  void getMinigameTaskFromDungeon() throws Exception {
    final MvcResult result = mvc
      .perform(
        get(fullURL + "/dungeons/" + initialDungeon.getIndex() + "/minigame-tasks/" + initialTask3.getIndex())
          .cookie(cookie)
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
          .cookie(cookie)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  void updateMinigameTaskFromWorld() throws Exception {
    final String newGame = "Crosswordpuzzle";
    final String newDescription = "New Crosswordpuzzle game";
    final UUID newConfigurationId = UUID.randomUUID();
    final MinigameTaskDTO updateMinigameTaskDTO = minigameTaskMapper.minigameTaskToMinigameTaskDTO(initialTask1);
    updateMinigameTaskDTO.setGame(Minigame.CROSSWORDPUZZLE);
    updateMinigameTaskDTO.setConfigurationId(newConfigurationId);
    updateMinigameTaskDTO.setDescription(newDescription);
    final String bodyValue = objectMapper.writeValueAsString(updateMinigameTaskDTO);

    final MvcResult result = mvc
      .perform(
        put(fullURL + "/minigame-tasks/" + initialTask1.getIndex())
          .cookie(cookie)
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
          .cookie(cookie)
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
}
