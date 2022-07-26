package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.LectureMapper;
import de.unistuttgart.overworldbackend.data.mapper.PlayerstatisticMapper;
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
  private PlayerstatisticMapper playerstatisticMapper;

  private String fullURL;
  private ObjectMapper objectMapper;

  private Lecture initialLecture;
  private LectureDTO initialLectureDTO;

  private Playerstatistic initialPlayerstatistic;
  private PlayerstatisticDTO initialPlayerstatisticDTO;

  @BeforeEach
  public void createBasicData() {
    lectureRepository.deleteAll();

    final World world = new World();
    world.setStaticName("Winter Wonderland");
    world.setTopicName("UML Winter");
    world.setActive(true);
    world.setMinigameTasks(Set.of());
    world.setNpcs(Arrays.asList());
    world.setDungeons(new ArrayList<>());
    List<World> worlds = new ArrayList<>();
    worlds.add(world);

    final Lecture lecture = new Lecture("PSE", "Basic lecture of computer science students", worlds);
    initialLecture = lectureRepository.save(lecture);
    initialLectureDTO = lectureMapper.lectureToLectureDTO(initialLecture);

    final Playerstatistic playerstatistic = new Playerstatistic();
    playerstatistic.setUserId("45h23o2j432");
    playerstatistic.setUsername("testUser");
    playerstatistic.setLecture(initialLecture);
    playerstatistic.setCurrentArea(initialLecture.getWorlds().stream().findFirst().get());
    playerstatistic.setKnowledge(new Random(10).nextLong());
    playerstatistic.setUnlockedAreas(Arrays.asList(initialLecture.getWorlds().stream().findFirst().get()));
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
    assertEquals(initialLectureDTO, createdPlayerstatisticDTOResult.getLecture());
  }
}
