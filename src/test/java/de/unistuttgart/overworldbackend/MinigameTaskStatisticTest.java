package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.enums.Minigame;
import de.unistuttgart.overworldbackend.data.statistics.MinigameHighscoreDistribution;
import de.unistuttgart.overworldbackend.data.statistics.MinigameSuccessRateStatistic;
import de.unistuttgart.overworldbackend.repositories.CourseRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerStatisticRepository;
import de.unistuttgart.overworldbackend.service.MinigameTaskStatisticService;
import de.unistuttgart.overworldbackend.service.PlayerStatisticService;
import de.unistuttgart.overworldbackend.service.PlayerTaskStatisticService;
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
public class MinigameTaskStatisticTest {

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

    private String fullURL;
    private ObjectMapper objectMapper;

    @Autowired
    private CourseRepository courseRepository;

    private Course initialCourse;
    private World initialWorld;
    private MinigameTask initialTask;

    @Autowired
    private PlayerStatisticService playerStatisticService;

    @Autowired
    private PlayerTaskStatisticService playerTaskStatisticService;

    @Autowired
    private PlayerStatisticRepository playerStatisticRepository;

    @BeforeEach
    public void createBasicData() {
        courseRepository.deleteAll();
        playerStatisticRepository.deleteAll();

        final MinigameTask minigameTask1 = new MinigameTask();
        minigameTask1.setConfigurationId(UUID.randomUUID());
        minigameTask1.setGame(Minigame.BUGFINDER);
        minigameTask1.setIndex(1);

        final Set<MinigameTask> worldMinigames = new HashSet<>();
        worldMinigames.add(minigameTask1);

        final World world = new World();
        world.setIndex(1);
        world.setStaticName("Winter Wonderland");
        world.setTopicName("UML Winter");
        world.setActive(true);
        world.setMinigameTasks(worldMinigames);
        world.setNpcs(Set.of());
        world.setBooks(Set.of());
        world.setDungeons(Arrays.asList());
        world.setConfigured(true);

        final Course course = new Course(
            "PSE",
            "SS-22",
            "Basic lecture of computer science students",
            true,
            Arrays.asList(world)
        );
        initialCourse = courseRepository.save(course);

        initialWorld = initialCourse.getWorlds().stream().findFirst().get();

        initialTask =
            initialWorld
                .getMinigameTasks()
                .stream()
                .filter(task -> task.getIndex() == minigameTask1.getIndex())
                .findAny()
                .get();

        assertNotNull(initialWorld.getId());
        assertNotNull(initialTask.getId());

        fullURL =
            String.format(
                "/courses/%d/worlds/%d/minigame-tasks/%d/statistics",
                initialCourse.getId(),
                initialWorld.getIndex(),
                minigameTask1.getIndex()
            );

        // create 100 player statistics with random tries of minigame runs
        for (int i = 0; i < 100; i++) {
            final PlayerStatisticDTO playerStatisticDTO = playerStatisticService.createPlayerStatisticInCourse(
                course.getId(),
                new PlayerRegistrationDTO(String.valueOf(i), "testUser" + i)
            );

            final int tries = new Random().nextInt(1, 5);
            for (int j = 0; j < tries; j++) {
                final int score = new Random().nextInt(0, 100);
                playerTaskStatisticService.submitData(
                    new PlayerTaskStatisticData(
                        minigameTask1.getGame(),
                        minigameTask1.getConfigurationId(),
                        score,
                        playerStatisticDTO.getUserId()
                    )
                );
            }
        }

        objectMapper = new ObjectMapper();

        doNothing().when(jwtValidatorService).validateTokenOrThrow("testToken");
        when(jwtValidatorService.extractUserId("testToken")).thenReturn("testUser");
    }

    @Test
    public void testGetHighscoreDistribution() throws Exception {
        final MvcResult result = mvc
            .perform(get(fullURL + "/highscore-distribution").cookie(cookie).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        final List<MinigameHighscoreDistribution> highscoreDistributions = Arrays.asList(
            objectMapper.readValue(result.getResponse().getContentAsString(), MinigameHighscoreDistribution[].class)
        );
        assertSame(
            MinigameTaskStatisticService.DEFAULT_DISTRIBUTION_PERCENTAGES.size() - 1,
            highscoreDistributions.size()
        );
        for (int i = 0; i < highscoreDistributions.size() - 2; i++) {
            assertTrue(
                highscoreDistributions.get(i).getFromScore() <= highscoreDistributions.get(i + 1).getFromScore()
            );
            assertTrue(highscoreDistributions.get(i).getToScore() <= highscoreDistributions.get(i + 1).getToScore());
        }
    }

    @Test
    public void testGetHighscoreDistributionWithOwnPercentages() throws Exception {
        final MvcResult result = mvc
            .perform(
                get(fullURL + "/highscore-distribution")
                    .param("timeDistributionPercentages", "0,10,20,30,40,50,60,70,80,90,100")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andReturn();

        final List<MinigameHighscoreDistribution> highscoreDistributions = Arrays.asList(
            objectMapper.readValue(result.getResponse().getContentAsString(), MinigameHighscoreDistribution[].class)
        );
        assertSame(10, highscoreDistributions.size());
        for (int i = 0; i < highscoreDistributions.size() - 2; i++) {
            assertTrue(
                highscoreDistributions.get(i).getFromScore() <= highscoreDistributions.get(i + 1).getFromScore()
            );
            assertTrue(highscoreDistributions.get(i).getToScore() <= highscoreDistributions.get(i + 1).getToScore());
        }
    }

    @Test
    public void testGetHighscoreDistributionWithOwnPercentages_IllegalList_ThrowsBadRequest() throws Exception {
        mvc
            .perform(
                get(fullURL + "/highscore-distribution")
                    .param("timeDistributionPercentages", "100,10,20,30,40,50,60,70,80,90,0")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest())
            .andReturn();
    }

    @Test
    public void testGetSuccessRateStatistic() throws Exception {
        final MvcResult result = mvc
            .perform(get(fullURL + "/success-rate").cookie(cookie).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        final MinigameSuccessRateStatistic minigameSuccessRateStatistic = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            MinigameSuccessRateStatistic.class
        );

        final int actualAmountOfTotalPlayerStatistics = initialCourse.getPlayerStatistics().size();
        int amountOfPlayerStatisticsInStatistic = 0;
        double successPlayers = 0;
        for (int players : minigameSuccessRateStatistic.getSuccessRateDistribution().values()) {
            amountOfPlayerStatisticsInStatistic += players;
            successPlayers += players;
        }
        for (int players : minigameSuccessRateStatistic.getFailureRateDistribution().values()) {
            amountOfPlayerStatisticsInStatistic += players;
        }
        assertSame(actualAmountOfTotalPlayerStatistics, amountOfPlayerStatisticsInStatistic);
        assertEquals(
            successPlayers / actualAmountOfTotalPlayerStatistics,
            minigameSuccessRateStatistic.getSuccessRate(),
            0.01
        );
    }
}
