package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.enums.Minigame;
import de.unistuttgart.overworldbackend.data.mapper.CourseMapper;
import de.unistuttgart.overworldbackend.repositories.CourseRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerStatisticRepository;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.servlet.http.Cookie;
import javax.transaction.Transactional;
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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@AutoConfigureMockMvc
@Transactional
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CourseControllerTest {

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
  private PlayerStatisticRepository playerStatisticRepository;

  @Autowired
  private CourseMapper courseMapper;

  private String fullURL;
  private ObjectMapper objectMapper;

  private Course initialCourse;
  private CourseDTO initialCourseDTO;
  private MinigameTask initialMinigameTask;
  private NPC initialNPC;

  @BeforeEach
  public void createBasicData() {
    courseRepository.deleteAll();

    final Dungeon dungeon = new Dungeon();
    dungeon.setIndex(1);
    dungeon.setStaticName("Dark Dungeon");
    dungeon.setTopicName("Dark UML");
    dungeon.setActive(true);
    dungeon.setMinigameTasks(Set.of());
    dungeon.setNpcs(Set.of());
    final List<Dungeon> dungeons = new ArrayList<>();
    final Set<MinigameTask> minigameTasks = new HashSet<>();

    final MinigameTask minigameTask = new MinigameTask();
    minigameTask.setConfigurationId(UUID.randomUUID());
    minigameTask.setGame(Minigame.BUGFINDER);
    minigameTask.setIndex(1);
    minigameTasks.add(minigameTask);

    final List<String> npcText = new ArrayList<>();
    npcText.add("NPCText");
    final NPC npc = new NPC();
    npc.setText(npcText);
    npc.setIndex(1);
    final Set<NPC> npcs = new HashSet<>();
    npcs.add(npc);

    final World world = new World();
    world.setIndex(1);
    world.setStaticName("Winter Wonderland");
    world.setTopicName("UML Winter");
    world.setActive(true);
    world.setMinigameTasks(minigameTasks);
    world.setNpcs(npcs);
    world.setDungeons(dungeons);
    final List<World> worlds = new ArrayList<>();
    worlds.add(world);

    final Course course = new Course("PSE", "SS-22", "Basic lecture of computer science students", true, worlds);
    initialCourse = courseRepository.save(course);
    initialCourseDTO = courseMapper.courseToCourseDTO(initialCourse);

    initialMinigameTask = course.getWorlds().stream().findFirst().get().getMinigameTasks().stream().findFirst().get();
    initialNPC = course.getWorlds().stream().findFirst().get().getNpcs().stream().findFirst().get();

    assertNotNull(initialCourse.getCourseName());

    fullURL = "/courses";

    objectMapper = new ObjectMapper();

    doNothing().when(jwtValidatorService).validateTokenOrThrow("testToken");
    when(jwtValidatorService.extractUserId("testToken")).thenReturn("testUser");
  }

  @Test
  void getCourse() throws Exception {
    final MvcResult result = mvc
      .perform(get(fullURL + "/" + initialCourseDTO.getId()).cookie(cookie).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final CourseDTO courseDTOResult = objectMapper.readValue(
      result.getResponse().getContentAsString(),
      CourseDTO.class
    );

    assertEquals(initialCourseDTO, courseDTOResult);
    assertEquals(initialCourseDTO.getId(), courseDTOResult.getId());
  }

  @Test
  void getCourse_DoesNotExist_ThrowsNotFound() throws Exception {
    mvc
      .perform(get(fullURL + "/1").cookie(cookie).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  void getCourses() throws Exception {
    final MvcResult result = mvc.perform(get(fullURL).cookie(cookie)).andExpect(status().isOk()).andReturn();

    final Set<CourseDTO> courseDTOResult = Set.of(
      objectMapper.readValue(result.getResponse().getContentAsString(), CourseDTO[].class)
    );

    final CourseDTO courseDTO = courseDTOResult.stream().findFirst().get();
    assertSame(1, courseDTOResult.size());
    assertEquals(initialCourseDTO.getId(), courseDTO.getId());
    assertEquals(initialCourseDTO, courseDTO);
  }

  @Test
  void updateCourse() throws Exception {
    final CourseDTO courseToUpdate = new CourseDTO();
    courseToUpdate.setCourseName("Software-engineering");
    courseToUpdate.setDescription("Basic lecture of software engineering students");

    final String bodyValue = objectMapper.writeValueAsString(courseToUpdate);

    final MvcResult result = mvc
      .perform(
        put(fullURL + "/" + initialCourseDTO.getId())
          .cookie(cookie)
          .content(bodyValue)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andReturn();

    final CourseDTO updatedCourse = objectMapper.readValue(result.getResponse().getContentAsString(), CourseDTO.class);

    assertEquals(courseToUpdate.getCourseName(), updatedCourse.getCourseName());
    assertEquals(courseToUpdate.getDescription(), updatedCourse.getDescription());
    assertEquals(
      courseToUpdate.getCourseName(),
      courseRepository.getReferenceById(updatedCourse.getId()).getCourseName()
    );
    assertEquals(
      courseToUpdate.getDescription(),
      courseRepository.getReferenceById(updatedCourse.getId()).getDescription()
    );
  }

  @Test
  void deleteCourse() throws Exception {
    final MvcResult result = mvc
      .perform(delete(fullURL + "/" + initialCourseDTO.getId()).cookie(cookie).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final CourseDTO courseDTOResult = objectMapper.readValue(
      result.getResponse().getContentAsString(),
      CourseDTO.class
    );

    assertEquals(initialCourseDTO, courseDTOResult);
    assertEquals(initialCourseDTO.getId(), courseDTOResult.getId());
    assertTrue(courseRepository.findAll().isEmpty());
  }

  @Test
  void createCourse() throws Exception {
    final CourseInitialData toCreateCourse = new CourseInitialData("testName", "SS-22", "testDescription");
    final String bodyValue = objectMapper.writeValueAsString(toCreateCourse);

    final MvcResult result = mvc
      .perform(post(fullURL).cookie(cookie).content(bodyValue).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isCreated())
      .andReturn();

    final CourseDTO createdCourse = objectMapper.readValue(result.getResponse().getContentAsString(), CourseDTO.class);

    assertEquals(toCreateCourse.getCourseName(), createdCourse.getCourseName());
    assertEquals(toCreateCourse.getDescription(), createdCourse.getDescription());
    assertEquals(
      createdCourse.getCourseName(),
      courseRepository.getReferenceById(createdCourse.getId()).getCourseName()
    );
    assertEquals(
      createdCourse.getDescription(),
      courseRepository.getReferenceById(createdCourse.getId()).getDescription()
    );
  }

  @Test
  void deleteCourseWithPlayerStatistics() throws Exception {
    // create playerstatstic
    final Player newPlayer = new Player("n423l34213", "newPlayer");
    mvc
      .perform(
        post(fullURL + "/" + initialCourseDTO.getId() + "/playerstatistics")
          .cookie(cookie)
          .content(objectMapper.writeValueAsString(newPlayer))
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isCreated())
      .andReturn();

    // submit a playertaskstatstic
    final PlayerTaskStatisticData playerTaskStatisticData = new PlayerTaskStatisticData();
    playerTaskStatisticData.setUserId(newPlayer.getUserId());
    playerTaskStatisticData.setGame(initialMinigameTask.getGame());
    playerTaskStatisticData.setConfigurationId(initialMinigameTask.getConfigurationId());
    playerTaskStatisticData.setScore(80);
    mvc
      .perform(
        post("/internal/submit-game-pass")
          .cookie(cookie)
          .content(objectMapper.writeValueAsString(playerTaskStatisticData))
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andReturn();

    // submit a npc statistic
    PlayerNPCStatisticData playerNPCStatisticData = new PlayerNPCStatisticData();
    playerNPCStatisticData.setUserId(newPlayer.getUserId());
    playerNPCStatisticData.setNpcId(initialNPC.getId());
    playerNPCStatisticData.setCompleted(true);

    Optional<PlayerStatistic> playerStatistic = playerStatisticRepository.findByCourseIdAndUserId(
      initialCourse.getId(),
      newPlayer.getUserId()
    );

    mvc
      .perform(
        post("/internal/submit-npc-pass")
          .cookie(cookie)
          .content(objectMapper.writeValueAsString(playerNPCStatisticData))
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andReturn();

    assertTrue(playerStatistic.isPresent());
    assertFalse(playerStatistic.get().getPlayerTaskStatistics().isEmpty());
    assertFalse(playerStatistic.get().getPlayerNPCStatistics().isEmpty());

    final MvcResult result = mvc
      .perform(delete(fullURL + "/" + initialCourseDTO.getId()).cookie(cookie).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final CourseDTO courseDTOResult = objectMapper.readValue(
      result.getResponse().getContentAsString(),
      CourseDTO.class
    );

    assertEquals(initialCourseDTO, courseDTOResult);
    assertEquals(initialCourseDTO.getId(), courseDTOResult.getId());
    assertTrue(
      playerStatisticRepository.findByCourseIdAndUserId(initialCourse.getId(), newPlayer.getUserId()).isEmpty()
    );
    assertTrue(courseRepository.findAll().isEmpty());
  }

  @Test
  void createCourse_InvalidSemesterRegex_ThrowsException() throws Exception {
    final CourseInitialData toCreateCourse = new CourseInitialData("testName2", "Sommer 2021", "testDescription");
    final String bodyValue = objectMapper.writeValueAsString(toCreateCourse);

    mvc
      .perform(post(fullURL).content(bodyValue).cookie(cookie).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest());
  }

  @Test
  void updateCourse_InvalidSemesterRegex_ThrowsException() throws Exception {
    final CourseDTO courseToUpdate = new CourseDTO();
    courseToUpdate.setCourseName("Software-engineering");
    courseToUpdate.setDescription("Basic lecture of software engineering students");
    courseToUpdate.setSemester("Sommer 2022");

    final String bodyValue = objectMapper.writeValueAsString(courseToUpdate);

    mvc
      .perform(
        put(fullURL + "/" + initialCourseDTO.getId())
          .cookie(cookie)
          .content(bodyValue)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isBadRequest());
  }
}
