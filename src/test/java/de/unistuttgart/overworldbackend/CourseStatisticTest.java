package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.enums.Minigame;
import de.unistuttgart.overworldbackend.data.mapper.CourseMapper;
import de.unistuttgart.overworldbackend.data.statistics.CompletedMinigames;
import de.unistuttgart.overworldbackend.data.statistics.LastPlayed;
import de.unistuttgart.overworldbackend.data.statistics.PlayerJoinedStatistic;
import de.unistuttgart.overworldbackend.data.statistics.UnlockedAreaAmount;
import de.unistuttgart.overworldbackend.repositories.CourseRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerStatisticRepository;
import de.unistuttgart.overworldbackend.service.*;
import java.time.LocalDateTime;
import java.util.*;
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
public class CourseStatisticTest {

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

    @Autowired
    PlayerStatisticRepository playerStatisticRepository;

    @Autowired
    PlayerStatisticService playerStatisticService;

    @Autowired
    CourseService courseService;

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    WorldService worldService;

    @Autowired
    MinigameTaskService minigameTaskService;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    PlayerTaskStatisticService playerTaskStatisticService;

    @MockBean
    JWTValidatorService jwtValidatorService;

    private MinigameTask initialMinigameTask;

    private NPC initialNPC;

    private PlayerStatistic initialPlayerStatistic;

    private PlayerStatisticDTO initialPlayerStatisticDTO;

    private Course initialCourse;

    private ObjectMapper objectMapper;

    private World initialWorld;

    private World initialWorld2;

    private Dungeon initialDungeon;

    final Cookie cookie = new Cookie("access_token", "testToken");

    private List<PlayerStatistic> playerStatisticsList;

    private String fullURL;

    @BeforeEach
    public void createBasicData() {
        courseRepository.deleteAll();

        final MinigameTask dungeonMinigameTask1 = new MinigameTask();
        dungeonMinigameTask1.setConfigurationId(UUID.randomUUID());
        dungeonMinigameTask1.setGame(Minigame.BUGFINDER);
        dungeonMinigameTask1.setIndex(1);

        final MinigameTask dungeonMinigameTask2 = new MinigameTask();
        dungeonMinigameTask2.setConfigurationId(UUID.randomUUID());
        dungeonMinigameTask2.setGame(Minigame.CHICKENSHOCK);
        dungeonMinigameTask2.setIndex(2);

        final Set<MinigameTask> dungeonMinigameTasks = new HashSet<>();
        dungeonMinigameTasks.add(dungeonMinigameTask1);
        dungeonMinigameTasks.add(dungeonMinigameTask2);

        final Dungeon dungeon = new Dungeon();
        dungeon.setStaticName("Dark Dungeon");
        dungeon.setTopicName("Dark UML");
        dungeon.setActive(true);
        dungeon.setConfigured(true);
        dungeon.setMinigameTasks(dungeonMinigameTasks);
        dungeon.setNpcs(Set.of());
        dungeon.setBooks(Set.of());
        final List<Dungeon> dungeons = new ArrayList<>();
        dungeons.add(dungeon);

        final MinigameTask minigameTask = new MinigameTask();
        minigameTask.setConfigurationId(UUID.randomUUID());
        minigameTask.setGame(Minigame.BUGFINDER);
        minigameTask.setIndex(1);

        final Set<MinigameTask> minigameTasks = new HashSet<>();
        minigameTasks.add(minigameTask);

        final World world = new World();
        world.setIndex(1);
        world.setStaticName("Winter Wonderland");
        world.setTopicName("UML Winter");
        world.setActive(true);
        world.setConfigured(true);
        world.setMinigameTasks(minigameTasks);
        world.setNpcs(Set.of());
        world.setBooks(Set.of());
        world.setDungeons(dungeons);

        final World world2 = new World();
        world2.setIndex(2);
        world2.setStaticName("Blooming Savanna");
        world2.setTopicName("UML Summer");
        world2.setActive(true);
        world2.setConfigured(true);
        world2.setMinigameTasks(Set.of());
        world2.setNpcs(Set.of());
        world2.setBooks(Set.of());
        world2.setDungeons(Arrays.asList());

        final List<World> worlds = new ArrayList<>();
        worlds.add(world);
        worlds.add(world2);

        final Course course = new Course("PSE", "SS-22", "Basic lecture of computer science students", true, worlds);

        initialCourse = courseRepository.save(course);

        initialWorld =
            initialCourse
                .getWorlds()
                .stream()
                .filter(searchWorld -> searchWorld.getIndex() == world.getIndex())
                .findFirst()
                .get();

        initialWorld2 =
            initialCourse
                .getWorlds()
                .stream()
                .filter(searchWorld -> searchWorld.getIndex() == world2.getIndex())
                .findFirst()
                .get();

        initialDungeon = initialWorld.getDungeons().stream().findFirst().get();

        initialMinigameTask = initialWorld.getMinigameTasks().stream().findFirst().get();

        playerStatisticsList = new ArrayList<>();

        for (int i = 0; i < 40; i++) {
            final PlayerRegistrationDTO playerRegistrationDTO = new PlayerRegistrationDTO(
                "testUser" + i,
                "testUserName" + i
            );
            final PlayerStatisticDTO initialPlayerDTO = playerStatisticService.createPlayerStatisticInCourse(
                initialCourse.getId(),
                playerRegistrationDTO
            );
            playerStatisticsList.add(playerStatisticRepository.findById(initialPlayerDTO.getId()).get());
        }

        final PlayerRegistrationDTO playerRegistrationDTO = new PlayerRegistrationDTO("testUser", "testUserName");
        initialPlayerStatisticDTO =
            playerStatisticService.createPlayerStatisticInCourse(initialCourse.getId(), playerRegistrationDTO);
        assert initialPlayerStatisticDTO.getId() != null;
        initialPlayerStatistic = playerStatisticRepository.findById(initialPlayerStatisticDTO.getId()).get();

        final PlayerTaskStatisticData playerTaskStatisticData = new PlayerTaskStatisticData();

        playerTaskStatisticData.setGame(minigameTask.getGame());
        playerTaskStatisticData.setUserId(initialPlayerStatistic.getUserId());
        playerTaskStatisticData.setScore(100);
        playerTaskStatisticData.setConfigurationId(minigameTask.getConfigurationId());

        playerTaskStatisticService.submitData(playerTaskStatisticData);

        fullURL = String.format("/courses/%s/statistics", initialCourse.getId());

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        doNothing().when(jwtValidatorService).validateTokenOrThrow("testToken");
        when(jwtValidatorService.extractUserId("testToken")).thenReturn("testUser");
    }

    @Test
    void testGetPlayerJoinedStatistic() throws Exception {
        final MvcResult result = mvc
            .perform(get(fullURL + "/players-joined").cookie(cookie).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        final PlayerJoinedStatistic playerJoinedStatistic = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            PlayerJoinedStatistic.class
        );
        assertEquals(41, playerJoinedStatistic.getTotalPlayers());
        assertTrue(
            isSameDay(
                initialPlayerStatistic.getCreated(),
                playerJoinedStatistic.getJoined().stream().findFirst().get().getDate()
            )
        );
    }

    @Test
    void testGetLastPlayedStatistic() throws Exception {
        final MvcResult result = mvc
            .perform(get(fullURL + "/last-played").cookie(cookie).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        final List<LastPlayed> lastPlayedList = List.of(
            objectMapper.readValue(result.getResponse().getContentAsString(), LastPlayed[].class)
        );
        assertEquals(7, lastPlayedList.size());
        assertEquals(41, lastPlayedList.get(0).getPlayers());
        assertEquals(1, lastPlayedList.get(0).getHour());
    }

    @Test
    void testUnlockedAreas() throws Exception {
        final MvcResult result = mvc
            .perform(get(fullURL + "/unlocked-areas").cookie(cookie).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        final List<UnlockedAreaAmount> unlockedAreas = List.of(
            objectMapper.readValue(result.getResponse().getContentAsString(), UnlockedAreaAmount[].class)
        );
        assertTrue(unlockedAreas.stream().anyMatch(unlockedAreaAmount -> unlockedAreaAmount.getLevel() == 2));
        assertTrue(unlockedAreas.stream().anyMatch(unlockedAreaAmount -> unlockedAreaAmount.getLevel() == 1));
        assertEquals(
            1,
            unlockedAreas.stream().filter(unlockedAreaAmount -> unlockedAreaAmount.getLevel() == 2).toList().size()
        );
        assertEquals(3, unlockedAreas.size());
        assertEquals(41, unlockedAreas.get(0).getPlayers());
        assertEquals(1, unlockedAreas.get(1).getPlayers());
    }

    @Test
    void testCompletedMinigames() throws Exception {
        final MvcResult result = mvc
            .perform(get(fullURL + "/completed-minigames").cookie(cookie).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        final Set<CompletedMinigames> completedMinigames = Set.of(
            objectMapper.readValue(result.getResponse().getContentAsString(), CompletedMinigames[].class)
        );
        assertTrue(
            completedMinigames
                .stream()
                .anyMatch(completedMinigames1 ->
                    completedMinigames1.getAmountOfCompletedMinigames() == 1 && completedMinigames1.getPlayers() == 1
                )
        );
        assertTrue(
            completedMinigames
                .stream()
                .anyMatch(completedMinigames1 ->
                    completedMinigames1.getAmountOfCompletedMinigames() == 0 && completedMinigames1.getPlayers() == 40
                )
        );
        assertEquals(2, completedMinigames.size());
    }

    private static boolean isSameDay(final LocalDateTime date1, final LocalDateTime date2) {
        return (
            date1.getDayOfYear() == date2.getDayOfYear() &&
            date1.getYear() == date2.getYear() &&
            date1.getMonthValue() == date2.getMonthValue()
        );
    }
}
