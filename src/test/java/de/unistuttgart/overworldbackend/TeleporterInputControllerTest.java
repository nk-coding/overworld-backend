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
import de.unistuttgart.overworldbackend.repositories.PlayerStatisticRepository;
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
    private CourseMapper courseMapper;

    @Autowired
    private PlayerStatisticMapper playerstatisticMapper;

    @Autowired
    private TeleporterMapper teleporterMapper;

    private String fullURL;
    private String npcURL;
    private ObjectMapper objectMapper;

    private Course initialCourse;
    private CourseDTO initialCourseDTO;

    private PlayerStatistic initialPlayerStatistic;
    private PlayerStatisticDTO initialPlayerStatisticDTO;

    private AreaLocationDTO initialAreaLocationDTO;

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

        final World world = new World();
        world.setStaticName("Winter Wonderland");
        world.setTopicName("UML Winter");
        world.setActive(true);
        world.setMinigameTasks(Set.of());
        world.setNpcs(Set.of());
        world.setBooks(Set.of());
        world.setDungeons(dungeons);
        final List<World> worlds = new ArrayList<>();
        worlds.add(world);

        final Course course = new Course("PSE", "SS-22", "Basic lecture of computer science students", true, worlds);

        initialCourse = courseRepository.save(course);
        initialCourseDTO = courseMapper.courseToCourseDTO(initialCourse);

        initialAreaLocationDTO = new AreaLocationDTO(world.getIndex(), null);

        final PlayerStatistic playerstatistic = new PlayerStatistic();
        playerstatistic.setUserId("45h23o2j432");
        playerstatistic.setUsername("testUser");
        playerstatistic.setCourse(initialCourse);
        playerstatistic.setCurrentArea(initialCourse.getWorlds().stream().findFirst().get());
        playerstatistic.setKnowledge(new Random(10).nextLong());
        playerstatistic.setUnlockedAreas(new ArrayList<>());
        playerstatistic.setCompletedDungeons(new ArrayList<>());
        playerstatistic.setUnlockedTeleporters(new HashSet<>());
        initialPlayerStatistic = playerstatisticRepository.save(playerstatistic);
        initialPlayerStatisticDTO = playerstatisticMapper.playerStatisticToPlayerstatisticDTO(initialPlayerStatistic);

        assertNotNull(initialCourse.getCourseName());

        fullURL = "/courses/" + initialCourse.getId() + "/teleporters";
        objectMapper = new ObjectMapper();

        doNothing().when(jwtValidatorService).validateTokenOrThrow("testToken");
        when(jwtValidatorService.extractUserId("testToken")).thenReturn("testUser");
    }

    @Test
    void submitTeleporterData() throws Exception {
        assertSame(0, initialPlayerStatistic.getUnlockedTeleporters().size());
        final PlayerTeleporterData playerTeleporterData = new PlayerTeleporterData();
        playerTeleporterData.setUserId(initialPlayerStatistic.getUserId());
        playerTeleporterData.setIndex(1);
        playerTeleporterData.setArea(initialAreaLocationDTO);

        final String bodyValue = objectMapper.writeValueAsString(playerTeleporterData);

        final MvcResult result = mvc
            .perform(post(fullURL).cookie(cookie).content(bodyValue).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        final PlayerStatisticDTO playerStatisticDTO = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            PlayerStatisticDTO.class
        );
        assertSame(1, playerStatisticDTO.getUnlockedTeleporters().size());
        TeleporterDTO teleporter = playerStatisticDTO.getUnlockedTeleporters().stream().findFirst().get();
        assertEquals(playerTeleporterData.getArea(), teleporter.getArea());
        assertSame(playerTeleporterData.getIndex(), teleporter.getIndex());
    }

    @Test
    void submitTeleporterDataTwice() throws Exception {
        final PlayerTeleporterData playerTeleporterData = new PlayerTeleporterData();
        playerTeleporterData.setUserId(initialPlayerStatistic.getUserId());
        playerTeleporterData.setIndex(1);
        playerTeleporterData.setArea(initialAreaLocationDTO);

        final String bodyValue = objectMapper.writeValueAsString(playerTeleporterData);

        final MvcResult result = mvc
            .perform(post(fullURL).cookie(cookie).content(bodyValue).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        final PlayerStatisticDTO playerStatisticDTO = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            PlayerStatisticDTO.class
        );
        assertSame(1, playerStatisticDTO.getUnlockedTeleporters().size());
        TeleporterDTO teleporter = playerStatisticDTO.getUnlockedTeleporters().stream().findFirst().get();
        assertEquals(playerTeleporterData.getArea(), teleporter.getArea());
        assertSame(playerTeleporterData.getIndex(), teleporter.getIndex());

        final PlayerTeleporterData playerTeleporterData2 = new PlayerTeleporterData();
        playerTeleporterData.setUserId(initialPlayerStatistic.getUserId());
        playerTeleporterData.setIndex(1);
        playerTeleporterData.setArea(initialAreaLocationDTO);

        final String bodyValue2 = objectMapper.writeValueAsString(playerTeleporterData2);

        mvc
            .perform(post(fullURL).cookie(cookie).content(bodyValue2).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void submitTeleporterData_PlayerDoesNotExist_ThrowNotFound() throws Exception {
        final PlayerTeleporterData playerTeleporterData = new PlayerTeleporterData();
        playerTeleporterData.setUserId(UUID.randomUUID().toString());
        playerTeleporterData.setIndex(1);
        playerTeleporterData.setArea(initialAreaLocationDTO);

        final String bodyValue = objectMapper.writeValueAsString(playerTeleporterData);

        mvc
            .perform(post(fullURL).cookie(cookie).content(bodyValue).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
}
