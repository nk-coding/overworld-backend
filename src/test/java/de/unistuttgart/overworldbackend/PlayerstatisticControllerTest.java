package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.DungeonMapper;
import de.unistuttgart.overworldbackend.data.mapper.LectureMapper;
import de.unistuttgart.overworldbackend.data.mapper.PlayerstatisticMapper;
import de.unistuttgart.overworldbackend.data.mapper.WorldMapper;
import de.unistuttgart.overworldbackend.repositories.LectureRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerstatisticRepository;
import de.unistuttgart.overworldbackend.service.PlayerStatisticService;
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
class PlayerstatisticControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private LectureRepository lectureRepository;

  @Autowired
  private PlayerstatisticRepository playerstatisticRepository;

  @Autowired
  private LectureMapper lectureMapper;

  @Autowired
  private WorldMapper worldMapper;

  @Autowired
  private DungeonMapper dungeonMapper;

  @Autowired
  private PlayerstatisticMapper playerstatisticMapper;

  private String fullURL;
  private ObjectMapper objectMapper;

  private Lecture initialLecture;
  private LectureDTO initialLectureDTO;
  private World initialWorld;
  private WorldDTO initialWorldDTO;

  private Dungeon initialDungeon;
  private DungeonDTO initialDungeonDTO;

  private Playerstatistic initialPlayerstatistic;
  private PlayerstatisticDTO initialPlayerstatisticDTO;

  @BeforeEach
  public void createBasicData() {
    playerstatisticRepository.deleteAll();
    lectureRepository.deleteAll();

    final Dungeon dungeon = new Dungeon();
    dungeon.setIndex(1);
    dungeon.setStaticName("Dark Dungeon");
    dungeon.setTopicName("Dark UML");
    dungeon.setActive(true);
    dungeon.setMinigameTasks(Set.of());
    dungeon.setNpcs(Set.of());

    final World world = new World();
    world.setIndex(1);
    world.setStaticName("Winter Wonderland");
    world.setTopicName("UML Winter");
    world.setActive(true);
    world.setMinigameTasks(Set.of());
    world.setNpcs(Set.of());
    world.setDungeons(Arrays.asList(dungeon));
    List<World> worlds = new ArrayList<>();
    worlds.add(world);

    final Lecture lecture = new Lecture("PSE", "Basic lecture of computer science students", worlds);
    initialLecture = lectureRepository.save(lecture);
    initialLectureDTO = lectureMapper.lectureToLectureDTO(initialLecture);

    initialWorld = initialLecture.getWorlds().stream().findFirst().get();
    initialWorldDTO = worldMapper.worldToWorldDTO(initialWorld);

    initialDungeon = initialWorld.getDungeons().stream().findFirst().get();
    initialDungeonDTO = dungeonMapper.dungeonToDungeonDTO(initialDungeon);

    final AreaLocation areaLocation = new AreaLocation(initialWorld);
    final Playerstatistic playerstatistic = new Playerstatistic();

    playerstatistic.setUserId("45h23o2j432");
    playerstatistic.setUsername("testUser");
    playerstatistic.setLecture(initialLecture);
    playerstatistic.setCurrentAreaLocation(areaLocation);
    playerstatistic.setKnowledge(new Random(10).nextLong());
    final ArrayList<AreaLocation> unlockedAreas = new ArrayList<>();
    unlockedAreas.add(areaLocation);
    playerstatistic.setUnlockedAreas(unlockedAreas);
    playerstatistic.setCompletedDungeons(new ArrayList<>());
    initialPlayerstatistic = playerstatisticRepository.save(playerstatistic);
    initialPlayerstatisticDTO = playerstatisticMapper.playerstatisticToPlayerstatisticDTO(initialPlayerstatistic);

    assertNotNull(initialLecture.getLectureName());
    assertNotNull(initialLectureDTO.getId());

    assertNotNull(initialPlayerstatistic.getId());
    assertNotNull(initialPlayerstatisticDTO.getId());

    fullURL = "/lectures/" + initialLectureDTO.getId() + "/playerstatistics";

    objectMapper = new ObjectMapper();
  }

  @Test
  void getPlayerstatistic() throws Exception {
    final MvcResult result = mvc
      .perform(get(fullURL + "/" + initialPlayerstatisticDTO.getUserId()).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final PlayerstatisticDTO playerstatisticDTOResult = objectMapper.readValue(
      result.getResponse().getContentAsString(),
      PlayerstatisticDTO.class
    );

    assertEquals(initialPlayerstatisticDTO, playerstatisticDTOResult);
    assertEquals(initialPlayerstatisticDTO.getId(), playerstatisticDTOResult.getId());
  }

  @Test
  void getPlayerstatistic_DoesNotExist_ThrowsNotFound() throws Exception {
    mvc
      .perform(get(fullURL + "/" + UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  void createPlayerstatistic() throws Exception {
    final Player newPlayer = new Player("n423l34213", "newPlayer");
    final String bodyValue = objectMapper.writeValueAsString(newPlayer);

    final MvcResult result = mvc
      .perform(post(fullURL).content(bodyValue).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isCreated())
      .andReturn();

    final PlayerstatisticDTO createdPlayerstatisticDTOResult = objectMapper.readValue(
      result.getResponse().getContentAsString(),
      PlayerstatisticDTO.class
    );

    assertEquals(0, createdPlayerstatisticDTOResult.getKnowledge());
    assertEquals(newPlayer.getUserId(), createdPlayerstatisticDTOResult.getUserId());
    assertEquals(newPlayer.getUsername(), createdPlayerstatisticDTOResult.getUsername());
    assertEquals(new AreaLocationDTO(1, null), createdPlayerstatisticDTOResult.getCurrentAreaLocation());
    assertEquals(Arrays.asList(new AreaLocationDTO(1, null)), createdPlayerstatisticDTOResult.getUnlockedAreas());
  }

  @Test
  void updatePlayerstatistic() throws Exception {
    final PlayerstatisticDTO updatedPlayerstatistic = playerstatisticMapper.playerstatisticToPlayerstatisticDTO(
      initialPlayerstatistic
    );
    final AreaLocationDTO newAreaLocation = new AreaLocationDTO(initialWorld.getIndex(), initialDungeon.getIndex());
    updatedPlayerstatistic.setCurrentAreaLocation(newAreaLocation);

    final String bodyValue = objectMapper.writeValueAsString(updatedPlayerstatistic);

    final MvcResult result = mvc
      .perform(
        put(fullURL + "/" + initialPlayerstatisticDTO.getUserId())
          .content(bodyValue)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andReturn();

    final PlayerstatisticDTO updatedPlayerstatisticDTOResult = objectMapper.readValue(
      result.getResponse().getContentAsString(),
      PlayerstatisticDTO.class
    );

    assertEquals(newAreaLocation, updatedPlayerstatisticDTOResult.getCurrentAreaLocation());
    assertEquals(initialPlayerstatistic.getId(), updatedPlayerstatisticDTOResult.getId());
    assertEquals(initialPlayerstatistic.getUserId(), updatedPlayerstatisticDTOResult.getUserId());
    assertEquals(initialPlayerstatistic.getUsername(), updatedPlayerstatisticDTOResult.getUsername());
  }

  @Test
  void updatePlayerstatistic_AreaLocationDoesNotExist_ThrowsBadRequest() throws Exception {
    final PlayerstatisticDTO updatedPlayerstatistic = playerstatisticMapper.playerstatisticToPlayerstatisticDTO(
      initialPlayerstatistic
    );
    final AreaLocationDTO newAreaLocation = new AreaLocationDTO(Integer.MAX_VALUE, Integer.MAX_VALUE);
    updatedPlayerstatistic.setCurrentAreaLocation(newAreaLocation);

    final String bodyValue = objectMapper.writeValueAsString(updatedPlayerstatistic);

    mvc
      .perform(
        put(fullURL + "/" + initialPlayerstatisticDTO.getUserId())
          .content(bodyValue)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isBadRequest());
  }
}
