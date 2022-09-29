package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.CourseMapper;
import de.unistuttgart.overworldbackend.data.mapper.DungeonMapper;
import de.unistuttgart.overworldbackend.data.mapper.PlayerStatisticMapper;
import de.unistuttgart.overworldbackend.data.mapper.WorldMapper;
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
class PlayerStatisticControllerTest {

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
    private WorldMapper worldMapper;

    @Autowired
    private DungeonMapper dungeonMapper;

    @Autowired
    private PlayerStatisticMapper playerstatisticMapper;

    private String fullURL;
    private ObjectMapper objectMapper;

    private Course initialCourse;
    private CourseDTO initialCourseDTO;
    private World initialWorld;
    private WorldDTO initialWorldDTO;

    private World initialWorld2;

    private Dungeon initialDungeon;
    private DungeonDTO initialDungeonDTO;

    private PlayerStatistic initialPlayerStatistic;
    private PlayerStatisticDTO initialPlayerStatisticDTO;

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
        dungeon.setBooks(Set.of());

        final List<Dungeon> dungeons = new ArrayList<>();
        dungeons.add(dungeon);

        final World world1 = new World();
        world1.setIndex(1);
        world1.setStaticName("Winter Wonderland");
        world1.setTopicName("UML Winter");
        world1.setActive(true);
        world1.setMinigameTasks(Set.of());
        world1.setNpcs(Set.of());
        world1.setDungeons(dungeons);
        world1.setBooks(Set.of());
        final List<World> worlds = new ArrayList<>();
        worlds.add(world1);

        final World world2 = new World();
        world2.setIndex(2);
        world2.setStaticName("Blooming Savanna");
        world2.setTopicName("UML Summer");
        world2.setActive(true);
        world2.setMinigameTasks(Set.of());
        world2.setNpcs(Set.of());
        world2.setDungeons(List.of());
        world2.setBooks(Set.of());
        worlds.add(world2);

        final Course course = new Course("PSE", "SS-22", "Basic lecture of computer science students", true, worlds);
        initialCourse = courseRepository.save(course);
        initialCourseDTO = courseMapper.courseToCourseDTO(initialCourse);

        initialWorld = initialCourse.getWorlds().stream().filter(world -> world.getIndex() == 1).findFirst().get();
        initialWorldDTO = worldMapper.worldToWorldDTO(initialWorld);

        initialWorld2 = initialCourse.getWorlds().stream().filter(world -> world.getIndex() == 2).findFirst().get();

        initialDungeon = initialWorld.getDungeons().stream().findFirst().get();
        initialDungeonDTO = dungeonMapper.dungeonToDungeonDTO(initialDungeon);

        final PlayerStatistic playerstatistic = new PlayerStatistic();

        playerstatistic.setUserId("45h23o2j432");
        playerstatistic.setUsername("testUser");
        playerstatistic.setCourse(initialCourse);
        playerstatistic.setCurrentArea(initialWorld);
        playerstatistic.setKnowledge(new Random(10).nextLong());
        final ArrayList<Area> unlockedAreas = new ArrayList<>();
        unlockedAreas.add(initialWorld);
        playerstatistic.setUnlockedAreas(unlockedAreas);
        playerstatistic.setCompletedDungeons(new ArrayList<>());
        initialPlayerStatistic = playerstatisticRepository.save(playerstatistic);
        initialPlayerStatisticDTO = playerstatisticMapper.playerStatisticToPlayerstatisticDTO(initialPlayerStatistic);

        assertNotNull(initialCourse.getCourseName());

        assertNotNull(initialPlayerStatistic.getId());
        assertNotNull(initialPlayerStatisticDTO.getId());

        fullURL = "/courses/" + initialCourseDTO.getId() + "/playerstatistics";

        objectMapper = new ObjectMapper();

        doNothing().when(jwtValidatorService).validateTokenOrThrow("testToken");
        when(jwtValidatorService.extractUserId("testToken")).thenReturn(initialPlayerStatistic.getUserId());
    }

    @Test
    void getPlayerStatistic() throws Exception {
        final MvcResult result = mvc
            .perform(
                get(fullURL + "/" + initialPlayerStatisticDTO.getUserId())
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andReturn();

        final PlayerStatisticDTO playerStatisticDTOResult = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            PlayerStatisticDTO.class
        );

        assertEquals(initialPlayerStatisticDTO, playerStatisticDTOResult);
        assertEquals(initialPlayerStatisticDTO.getId(), playerStatisticDTOResult.getId());
    }

    @Test
    void getOwnPlayerStatistic() throws Exception {
        final MvcResult result = mvc
            .perform(get(fullURL).cookie(cookie).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        final PlayerStatisticDTO playerStatisticDTOResult = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            PlayerStatisticDTO.class
        );

        assertEquals(initialPlayerStatisticDTO, playerStatisticDTOResult);
        assertEquals(initialPlayerStatisticDTO.getId(), playerStatisticDTOResult.getId());
    }

    @Test
    void getPlayerStatistic_DoesNotExist_ThrowsNotFound() throws Exception {
        mvc
            .perform(get(fullURL + "/" + UUID.randomUUID()).cookie(cookie).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn();
    }

    @Test
    void createPlayerStatistic() throws Exception {
        final Player newPlayer = new Player("n423l34213", "newPlayer");
        final String bodyValue = objectMapper.writeValueAsString(newPlayer);

        final MvcResult result = mvc
            .perform(post(fullURL).content(bodyValue).cookie(cookie).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn();

        final PlayerStatisticDTO createdPlayerStatisticDTOResult = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            PlayerStatisticDTO.class
        );

        assertEquals(0, createdPlayerStatisticDTOResult.getKnowledge());
        assertEquals(newPlayer.getUserId(), createdPlayerStatisticDTOResult.getUserId());
        assertEquals(newPlayer.getUsername(), createdPlayerStatisticDTOResult.getUsername());
        assertEquals(new AreaLocationDTO(1, null), createdPlayerStatisticDTOResult.getCurrentArea());
        assertEquals(Arrays.asList(new AreaLocationDTO(1, null)), createdPlayerStatisticDTOResult.getUnlockedAreas());
    }

    @Test
    void updatePlayerStatistic() throws Exception {
        final PlayerStatisticDTO updatedPlayerStatistic = playerstatisticMapper.playerStatisticToPlayerstatisticDTO(
            initialPlayerStatistic
        );
        final AreaLocationDTO newAreaLocation = new AreaLocationDTO(initialWorld.getIndex(), initialDungeon.getIndex());
        updatedPlayerStatistic.setCurrentArea(newAreaLocation);

        final String bodyValue = objectMapper.writeValueAsString(updatedPlayerStatistic);

        final MvcResult result = mvc
            .perform(
                put(fullURL + "/" + initialPlayerStatisticDTO.getUserId())
                    .cookie(cookie)
                    .content(bodyValue)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andReturn();

        final PlayerStatisticDTO updatedPlayerStatisticDTOResult = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            PlayerStatisticDTO.class
        );

        assertEquals(newAreaLocation, updatedPlayerStatisticDTOResult.getCurrentArea());
        assertEquals(initialPlayerStatistic.getId(), updatedPlayerStatisticDTOResult.getId());
        assertEquals(initialPlayerStatistic.getUserId(), updatedPlayerStatisticDTOResult.getUserId());
        assertEquals(initialPlayerStatistic.getUsername(), updatedPlayerStatisticDTOResult.getUsername());
    }

    @Test
    void updatePlayerStatisticWorld() throws Exception {
        final PlayerStatisticDTO updatedPlayerStatistic = playerstatisticMapper.playerStatisticToPlayerstatisticDTO(
            initialPlayerStatistic
        );
        final AreaLocationDTO newAreaLocation = new AreaLocationDTO(initialWorld2.getIndex(), null);
        updatedPlayerStatistic.setCurrentArea(newAreaLocation);

        final String bodyValue = objectMapper.writeValueAsString(updatedPlayerStatistic);

        final MvcResult result = mvc
            .perform(
                put(fullURL + "/" + initialPlayerStatisticDTO.getUserId())
                    .cookie(cookie)
                    .content(bodyValue)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andReturn();

        final PlayerStatisticDTO updatedPlayerStatisticDTOResult = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            PlayerStatisticDTO.class
        );

        assertEquals(newAreaLocation, updatedPlayerStatisticDTOResult.getCurrentArea());
        assertEquals(initialPlayerStatistic.getId(), updatedPlayerStatisticDTOResult.getId());
        assertEquals(initialPlayerStatistic.getUserId(), updatedPlayerStatisticDTOResult.getUserId());
        assertEquals(initialPlayerStatistic.getUsername(), updatedPlayerStatisticDTOResult.getUsername());
    }

    @Test
    void updatePlayerStatistic_AreaLocationDoesNotExist_ThrowsBadRequest() throws Exception {
        final PlayerStatisticDTO updatedPlayerStatistic = playerstatisticMapper.playerStatisticToPlayerstatisticDTO(
            initialPlayerStatistic
        );
        final AreaLocationDTO newAreaLocation = new AreaLocationDTO(Integer.MAX_VALUE, Integer.MAX_VALUE);
        updatedPlayerStatistic.setCurrentArea(newAreaLocation);

        final String bodyValue = objectMapper.writeValueAsString(updatedPlayerStatistic);

        mvc
            .perform(
                put(fullURL + "/" + initialPlayerStatisticDTO.getUserId())
                    .cookie(cookie)
                    .content(bodyValue)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest());
    }
}
