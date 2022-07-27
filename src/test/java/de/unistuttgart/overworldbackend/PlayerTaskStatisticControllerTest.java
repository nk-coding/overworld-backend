package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.LectureMapper;
import de.unistuttgart.overworldbackend.data.mapper.MinigameTaskMapper;
import de.unistuttgart.overworldbackend.data.mapper.NPCMapper;
import de.unistuttgart.overworldbackend.data.mapper.PlayerstatisticMapper;
import de.unistuttgart.overworldbackend.repositories.LectureRepository;
import de.unistuttgart.overworldbackend.repositories.MinigameTaskRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerstatisticRepository;
import de.unistuttgart.overworldbackend.service.PlayerTaskStatisticService;
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
class PlayerTaskStatisticControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private LectureRepository lectureRepository;

  @Autowired
  private PlayerstatisticRepository playerstatisticRepository;

  @Autowired
  private PlayerTaskStatisticService playerTaskStatisticService;

  @Autowired
  private LectureMapper lectureMapper;

  @Autowired
  private PlayerstatisticMapper playerstatisticMapper;

  @Autowired
  private MinigameTaskMapper minigameTaskMapper;

  private String fullURL;
  private ObjectMapper objectMapper;

  private Lecture initialLecture;
  private LectureDTO initialLectureDTO;

  private Playerstatistic initialPlayerstatistic;
  private PlayerstatisticDTO initialPlayerstatisticDTO;

  private MinigameTask initialMinigameTask;

  private MinigameTaskDTO initialMinigameTaskDTO;

  @Autowired
  private MinigameTaskRepository minigameTaskRepository;

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
    dungeon.setStaticName("Dark Dungeon");
    dungeon.setTopicName("Dark UML");
    dungeon.setActive(true);
    dungeon.setMinigameTasks(Set.of(minigameTask3));
    dungeon.setNpcs(Set.of());
    final List<Dungeon> dungeons = new ArrayList<>();

    final World world = new World();
    world.setStaticName("Winter Wonderland");
    world.setTopicName("UML Winter");
    world.setActive(true);
    world.setMinigameTasks(Set.of(minigameTask1, minigameTask2));
    world.setNpcs(Set.of());
    world.setDungeons(dungeons);
    List<World> worlds = new ArrayList<>();
    worlds.add(world);

    final Lecture lecture = new Lecture("PSE", "Basic lecture of computer science students", worlds);

    initialLecture = lectureRepository.save(lecture);
    initialLectureDTO = lectureMapper.lectureToLectureDTO(initialLecture);

    initialMinigameTask =
      initialLecture.getWorlds().stream().findFirst().get().getMinigameTasks().stream().findFirst().get();
    initialMinigameTaskDTO = minigameTaskMapper.minigameTaskToMinigameTaskDTO(initialMinigameTask);

    final Playerstatistic playerstatistic = new Playerstatistic();
    playerstatistic.setUserId("45h23o2j432");
    playerstatistic.setUsername("testUser");
    playerstatistic.setLecture(initialLecture);
    AreaLocation areaLocation = new AreaLocation();
    areaLocation.setWorld(initialLecture.getWorlds().stream().findFirst().get());
    playerstatistic.setCurrentAreaLocation(areaLocation);
    playerstatistic.setKnowledge(new Random(10).nextLong());
    playerstatistic.setUnlockedAreas(new ArrayList<>());
    playerstatistic.setCompletedDungeons(new ArrayList<>());
    initialPlayerstatistic = playerstatisticRepository.save(playerstatistic);
    initialPlayerstatisticDTO = playerstatisticMapper.playerstatisticToPlayerstatisticDTO(initialPlayerstatistic);

    assertNotNull(initialLecture.getLectureName());
    assertNotNull(initialLectureDTO.getId());

    assertEquals(initialLecture.getId(), initialMinigameTask.getLecture().getId());
    assertEquals(initialLecture.getId(), initialPlayerstatistic.getLecture().getId());
    fullURL =
      String.format(
        "/lectures/%d/playerstatistics/" + initialPlayerstatistic.getUserId() + "/player-task-statistics",
        initialLecture.getId()
      );

    objectMapper = new ObjectMapper();
  }

  @Test
  void getTaskStatistics() throws Exception {
    PlayerTaskStatisticDTO statistic = playerTaskStatisticService.submitData(
      new PlayerTaskStatisticData(
        initialMinigameTask.getGame(),
        initialMinigameTask.getConfigurationId(),
        80,
        initialPlayerstatisticDTO.getUserId()
      )
    );

    final MvcResult result = mvc
      .perform(get(fullURL).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final List<PlayerTaskStatisticDTO> playerTaskStatisticDTOs = Arrays.asList(
      objectMapper.readValue(result.getResponse().getContentAsString(), PlayerTaskStatisticDTO[].class)
    );
    assertEquals(playerTaskStatisticDTOs.get(0), statistic);
  }

  @Test
  void getTaskStatistic() throws Exception {
    PlayerTaskStatisticDTO statistic = playerTaskStatisticService.submitData(
      new PlayerTaskStatisticData(
        initialMinigameTask.getGame(),
        initialMinigameTask.getConfigurationId(),
        80,
        initialPlayerstatisticDTO.getUserId()
      )
    );

    final MvcResult result = mvc
      .perform(get(fullURL + "/" + statistic.getId()).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final PlayerTaskStatisticDTO playerTaskStatisticDTO = objectMapper.readValue(
      result.getResponse().getContentAsString(),
      PlayerTaskStatisticDTO.class
    );
    assertEquals(statistic, playerTaskStatisticDTO);
    assertEquals(initialMinigameTaskDTO, playerTaskStatisticDTO.getMinigameTask());
    assertNotNull(playerTaskStatisticDTO.getMinigameTask().getAreaLocation());
    assertEquals(initialMinigameTaskDTO.getAreaLocation(), playerTaskStatisticDTO.getMinigameTask().getAreaLocation());
  }

  @Test
  void getTaskStatistic_DoesNotExist_ThrowsNotFound() throws Exception {
    final MvcResult result = mvc
      .perform(get(fullURL + "/" + UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound())
      .andReturn();
  }
}
