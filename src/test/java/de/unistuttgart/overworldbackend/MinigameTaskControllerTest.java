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

  private String fullURL;
  private ObjectMapper objectMapper;

  private Lecture initialLecture;
  private World initialWorld;
  private WorldDTO initialWorldDTO;

  private MinigameTask initialTask1;
  private MinigameTaskDTO initialTaskDTO1;

  private MinigameTask initialTask2;
  private MinigameTaskDTO initialTaskDTO2;

  @BeforeEach
  public void createBasicData() {
    lectureRepository.deleteAll();

    final MinigameTask minigameTask1 = new MinigameTask();
    minigameTask1.setConfigurationId(UUID.randomUUID());
    minigameTask1.setGame("Bugfinder");
    minigameTask1.setLocation("w1-p1");

    final MinigameTask minigameTask2 = new MinigameTask();
    minigameTask2.setConfigurationId(UUID.randomUUID());
    minigameTask2.setGame("Moorhuhn");
    minigameTask2.setLocation("w1-p2");

    final World world = new World();
    world.setStaticName("Winter Wonderland");
    world.setTopicName("UML Winter");
    world.setActive(true);
    world.setMinigameTasks(Set.of(minigameTask1, minigameTask2));
    world.setNpcs(Set.of());
    world.setDungeons(Arrays.asList());

    final Lecture lecture = new Lecture("PSE", "Basic lecture of computer science students", Arrays.asList(world));
    initialLecture = lectureRepository.save(lecture);

    initialWorld = initialLecture.getWorlds().stream().findFirst().get();
    initialWorldDTO = worldMapper.worldToWorldDTO(initialWorld);

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

    assertNotNull(initialWorld.getId());
    assertNotNull(initialWorldDTO.getId());

    assertNotNull(initialTask1.getId());
    assertNotNull(initialTaskDTO1.getId());
    assertNotNull(initialTask2.getId());
    assertNotNull(initialTaskDTO2.getId());

    fullURL = "/lectures/" + initialLecture.getId() + "/worlds/" + initialWorld.getId() + "/minigame-tasks";

    objectMapper = new ObjectMapper();
  }

  @Test
  void getMinigameTasksFromWorld() throws Exception {
    final MvcResult result = mvc
      .perform(get(fullURL).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final Set<MinigameTaskDTO> minigameTasks = Set.of(
      objectMapper.readValue(result.getResponse().getContentAsString(), MinigameTaskDTO[].class)
    );

    assertSame(initialWorldDTO.getMinigameTasks().size(), minigameTasks.size());
    assertEquals(initialWorldDTO.getMinigameTasks(), minigameTasks);
  }

  @Test
  void getMinigameTaskFromWorld() throws Exception {
    final MvcResult result = mvc
      .perform(get(fullURL + "/" + initialTask1.getId()).contentType(MediaType.APPLICATION_JSON))
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
      .perform(get(fullURL + "/" + UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON))
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
      .perform(put(fullURL + "/" + initialTask1.getId()).content(bodyValue).contentType(MediaType.APPLICATION_JSON))
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
}
