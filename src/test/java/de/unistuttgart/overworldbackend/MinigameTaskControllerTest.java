package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.DungeonMapper;
import de.unistuttgart.overworldbackend.data.mapper.MinigameTaskMapper;
import de.unistuttgart.overworldbackend.data.mapper.WorldMapper;
import de.unistuttgart.overworldbackend.repositories.LectureRepository;
import de.unistuttgart.overworldbackend.repositories.WorldRepository;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
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
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MinigameTaskControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private LectureRepository lectureRepository;

  @Autowired
  private WorldMapper worldMapper;

  @Autowired
  private MinigameTaskMapper minigameTaskMapper;

  @Autowired
  private DungeonMapper dungeonMapper;

  private String fullURL;
  private ObjectMapper objectMapper;

  private Lecture initialLecture;
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
    lectureRepository.deleteAll();

    final MinigameTask minigameTask1 = new MinigameTask();
    minigameTask1.setConfigurationId(UUID.randomUUID());
    minigameTask1.setGame("Bugfinder");
    minigameTask1.setIndex(1);

    final MinigameTask minigameTask2 = new MinigameTask();
    minigameTask2.setConfigurationId(UUID.randomUUID());
    minigameTask2.setGame("Moorhuhn");
    minigameTask2.setIndex(2);

    final MinigameTask minigameTask3 = new MinigameTask();
    minigameTask2.setConfigurationId(UUID.randomUUID());
    minigameTask2.setGame("Crosswordpuzzle");
    minigameTask2.setIndex(3);

    final Dungeon dungeon = new Dungeon();
    dungeon.setStaticName("Dungeon 1");
    dungeon.setTopicName("Testtopic");
    dungeon.setActive(true);
    dungeon.setMinigameTasks(Set.of(minigameTask3));
    dungeon.setNpcs(Set.of());

    final World world = new World();
    world.setStaticName("Winter Wonderland");
    world.setTopicName("UML Winter");
    world.setActive(true);
    world.setMinigameTasks(Set.of(minigameTask1, minigameTask2));
    world.setNpcs(Set.of());
    world.setDungeons(Arrays.asList(dungeon));

    final Lecture lecture = new Lecture("PSE", "Basic lecture of computer science students", Arrays.asList(world));
    initialLecture = lectureRepository.save(lecture);

    initialWorld = initialLecture.getWorlds().stream().findFirst().get();
    initialWorldDTO = worldMapper.worldToWorldDTO(initialWorld);

    initialDungeon = initialLecture.getWorlds().stream().findFirst().get().getDungeons().stream().findFirst().get();
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

    fullURL = String.format("/lectures/%d/worlds/%d", initialLecture.getId(), initialWorld.getIndex());

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
    final String newGame = "Crosswordpuzzle";
    final UUID newConfigurationId = UUID.randomUUID();
    final MinigameTaskDTO updateMinigameTaskDTO = minigameTaskMapper.minigameTaskToMinigameTaskDTO(initialTask1);
    updateMinigameTaskDTO.setGame(newGame);
    updateMinigameTaskDTO.setConfigurationId(newConfigurationId);
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
    assertEquals(newGame, updatedMinigameTaskDTOResult.getGame());
    assertEquals(newConfigurationId, updatedMinigameTaskDTOResult.getConfigurationId());
    assertEquals(updatedMinigameTaskDTOResult, updatedMinigameTaskDTOResult);
  }

  @Test
  void updateMinigameTaskFromDungeon() throws Exception {
    final String newGame = "Moorhuhn";
    final UUID newConfigurationId = UUID.randomUUID();
    final MinigameTaskDTO updateMinigameTaskDTO = minigameTaskMapper.minigameTaskToMinigameTaskDTO(initialTask1);
    updateMinigameTaskDTO.setGame(newGame);
    updateMinigameTaskDTO.setConfigurationId(newConfigurationId);
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
    assertEquals(updatedMinigameTaskDTOResult, updatedMinigameTaskDTOResult);
  }
}
