package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.CourseMapper;
import de.unistuttgart.overworldbackend.data.mapper.PlayerStatisticMapper;
import de.unistuttgart.overworldbackend.data.mapper.TeleporterMapper;
import de.unistuttgart.overworldbackend.repositories.CourseRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerNPCActionLogRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerNPCStatisticRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerStatisticRepository;
import de.unistuttgart.overworldbackend.service.PlayerNPCStatisticService;
import java.util.*;
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
@SpringBootTest
@Testcontainers
class TeleporterInputControllerTest {

    @Container
    public static PostgreSQLContainer postgresDB = new PostgreSQLContainer("postgres:14-alpine")
        .withDatabaseName("postgres")
        .withUsername("postgres")
        .withPassword("postgres");

    @DynamicPropertySource
    public static void properties(final DynamicPropertyRegistry registry) {
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
    private PlayerStatisticRepository playerstatisticRepository;

    @Autowired
    private PlayerNPCActionLogRepository playerNPCActionLogRepository;

    @Autowired
    private PlayerNPCStatisticService playerNPCStatisticService;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private PlayerStatisticMapper playerstatisticMapper;

    @Autowired
    private TeleporterMapper teleporterMapper;

    @Autowired
    private PlayerNPCStatisticRepository playerNPCStatisticRepository;

    private String fullURL;
    private String npcURL;
    private ObjectMapper objectMapper;

    private Course initialCourse;
    private CourseDTO initialCourseDTO;

    private PlayerStatistic initialPlayerStatistic;
    private PlayerStatisticDTO initialPlayerStatisticDTO;

    private Teleporter initialTeleporter;
    private TeleporterDTO initialTeleporterDTO;

    @BeforeEach
    public void createBasicData() {
        courseRepository.deleteAll();

        final Dungeon dungeon = new Dungeon();
        dungeon.setStaticName("Dark Dungeon");
        dungeon.setTopicName("Dark UML");
        dungeon.setActive(true);
        dungeon.setMinigameTasks(Set.of());
        dungeon.setNpcs(Set.of());
        dungeon.setBooks(Set.of());
        final List<Dungeon> dungeons = new ArrayList<>();
        dungeons.add(dungeon);

        final Set<Teleporter> teleporters = new HashSet<>();
        teleporters.add(new Teleporter("example", new Position(1, 1), 1));

        final World world = new World();
        world.setStaticName("Winter Wonderland");
        world.setTopicName("UML Winter");
        world.setActive(true);
        world.setMinigameTasks(Set.of());
        world.setNpcs(Set.of());
        world.setBooks(Set.of());
        world.setTeleporters(teleporters);
        world.setDungeons(dungeons);
        final List<World> worlds = new ArrayList<>();
        worlds.add(world);

        final Course course = new Course("PSE", "SS-22", "Basic lecture of computer science students", true, worlds);

        initialCourse = courseRepository.save(course);
        initialCourseDTO = courseMapper.courseToCourseDTO(initialCourse);

        final World initialWorld = initialCourse.getWorlds().stream().findFirst().get();
        initialTeleporter = initialWorld.getTeleporters().stream().findFirst().get();
        initialTeleporterDTO = teleporterMapper.teleporterToTeleporterDTO(initialTeleporter);

        final PlayerStatistic playerstatistic = new PlayerStatistic();
        playerstatistic.setUserId("45h23o2j432");
        playerstatistic.setUsername("testUser");
        playerstatistic.setCourse(initialCourse);
        playerstatistic.setCurrentArea(initialCourse.getWorlds().stream().findFirst().get());
        playerstatistic.setKnowledge(new Random(10).nextLong());
        playerstatistic.setUnlockedAreas(new ArrayList<>());
        playerstatistic.setCompletedDungeons(new ArrayList<>());
        playerstatistic.setUnlockedTeleporters(new ArrayList<>());
        initialPlayerStatistic = playerstatisticRepository.save(playerstatistic);
        initialPlayerStatisticDTO = playerstatisticMapper.playerStatisticToPlayerstatisticDTO(initialPlayerStatistic);

        assertNotNull(initialCourse.getCourseName());

        assertEquals(initialCourse.getId(), initialTeleporter.getCourse().getId());
        assertEquals(initialCourse.getId(), initialPlayerStatistic.getCourse().getId());
        fullURL = "/internal";
        objectMapper = new ObjectMapper();

        doNothing().when(jwtValidatorService).validateTokenOrThrow("testToken");
        when(jwtValidatorService.extractUserId("testToken")).thenReturn("testUser");
    }

    @Test
    void submitTeleporterData() throws Exception {
        final PlayerTeleportData playerTeleportData = new PlayerTeleportData();
        playerTeleportData.setUserId(initialPlayerStatistic.getUserId());
        playerTeleportData.setTeleporterId(initialTeleporter.getId());
        playerTeleportData.setCompleted(true);

        final String bodyValue = objectMapper.writeValueAsString(playerTeleportData);

        final MvcResult result = mvc
            .perform(
                post(fullURL + "/submit-teleporter-pass")
                    .cookie(cookie)
                    .content(bodyValue)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andReturn();

        final PlayerStatisticDTO playerStatisticDTO = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            PlayerStatisticDTO.class
        );
        assertTrue(playerStatisticDTO.getUnlockedTeleporters().contains(initialTeleporterDTO));
    }

    @Test
    void submitTeleporterDataTwice() throws Exception {
        final PlayerTeleportData playerTeleportData = new PlayerTeleportData();
        playerTeleportData.setUserId(initialPlayerStatistic.getUserId());
        playerTeleportData.setTeleporterId(initialTeleporter.getId());
        playerTeleportData.setCompleted(false);

        final String bodyValue = objectMapper.writeValueAsString(playerTeleportData);

        final MvcResult result = mvc
            .perform(
                post(fullURL + "/submit-teleporter-pass")
                    .cookie(cookie)
                    .content(bodyValue)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andReturn();

        final PlayerStatisticDTO playerStatisticDTO = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            PlayerStatisticDTO.class
        );
        assertTrue(playerStatisticDTO.getUnlockedTeleporters().contains(initialTeleporterDTO));

        final PlayerTeleportData playerTeleportData2 = new PlayerTeleportData();
        playerTeleportData2.setUserId(initialPlayerStatistic.getUserId());
        playerTeleportData2.setTeleporterId(initialTeleporter.getId());
        playerTeleportData2.setCompleted(true);

        final String bodyValue2 = objectMapper.writeValueAsString(playerTeleportData2);

        mvc
            .perform(
                post(fullURL + "/submit-teleporter-pass")
                    .cookie(cookie)
                    .content(bodyValue2)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    void submitTeleporterData_PlayerDoesNotExist_ThrowNotFound() throws Exception {
        final PlayerTeleportData playerTeleportData = new PlayerTeleportData();
        playerTeleportData.setUserId(UUID.randomUUID().toString());
        playerTeleportData.setTeleporterId(initialTeleporter.getId());
        playerTeleportData.setCompleted(true);

        final String bodyValue = objectMapper.writeValueAsString(playerTeleportData);

        mvc
            .perform(
                post(fullURL + "/submit-teleporter-pass")
                    .cookie(cookie)
                    .content(bodyValue)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound());
    }

    @Test
    void submitTeleporterData_NPCDoesNotExist_ThrowNotFound() throws Exception {
        final PlayerTeleportData playerTeleportData = new PlayerTeleportData();
        playerTeleportData.setUserId(initialPlayerStatistic.getUserId());
        playerTeleportData.setTeleporterId(UUID.randomUUID());
        playerTeleportData.setCompleted(true);

        final String bodyValue = objectMapper.writeValueAsString(playerTeleportData);

        mvc
            .perform(
                post(fullURL + "/submit-teleporter-pass")
                    .cookie(cookie)
                    .content(bodyValue)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound());
    }
}
