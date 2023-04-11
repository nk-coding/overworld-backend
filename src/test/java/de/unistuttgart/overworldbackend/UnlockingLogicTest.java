package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.enums.Minigame;
import de.unistuttgart.overworldbackend.data.mapper.CourseMapper;
import de.unistuttgart.overworldbackend.data.mapper.DungeonMapper;
import de.unistuttgart.overworldbackend.data.mapper.MinigameTaskMapper;
import de.unistuttgart.overworldbackend.data.mapper.WorldMapper;
import de.unistuttgart.overworldbackend.repositories.*;
import de.unistuttgart.overworldbackend.service.CourseService;
import de.unistuttgart.overworldbackend.service.MinigameTaskService;
import de.unistuttgart.overworldbackend.service.PlayerStatisticService;
import de.unistuttgart.overworldbackend.service.PlayerTaskStatisticService;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@AutoConfigureMockMvc
@Transactional
@SpringBootTest
@Testcontainers
public class UnlockingLogicTest {

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

    @MockBean
    JWTValidatorService jwtValidatorService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private WorldRepository worldRepository;

    @Autowired
    private DungeonRepository dungeonRepository;

    @Autowired
    private WorldMapper worldMapper;

    @Autowired
    private MinigameTaskMapper minigameTaskMapper;

    @Autowired
    private MinigameTaskRepository minigameTaskRepository;

    @Autowired
    private DungeonMapper dungeonMapper;

    @Autowired
    private CourseService courseService;

    @Autowired
    private MinigameTaskService minigameTaskService;

    @Autowired
    private PlayerStatisticService playerStatisticService;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private PlayerTaskStatisticService playerTaskStatisticService;

    @Autowired
    private PlayerStatisticRepository playerStatisticRepository;

    private CourseDTO initialCourseDTO;

    private Course initialCourse;

    private MinigameTaskDTO minigameTaskDTO1;

    private MinigameTaskDTO minigameTaskDTO2;

    private MinigameTaskDTO minigameTaskDTO3;

    private MinigameTaskDTO minigameTaskDTO4;

    private PlayerStatisticDTO playerStatisticDTO;

    private PlayerStatistic playerStatistic;

    @BeforeEach
    public void createBasicData() {
        doNothing().when(jwtValidatorService).validateTokenOrThrow("testToken");
        when(jwtValidatorService.extractUserId("testToken")).thenReturn("testUser");
        courseRepository.deleteAll();

        final CourseInitialData courseInitialData = new CourseInitialData("TestCourse", "SS-21", "TestDescription");

        initialCourseDTO = courseService.createCourse(courseInitialData);

        minigameTaskDTO1 =
            minigameTaskService.getMinigameTaskFromArea(initialCourseDTO.getId(), 1, Optional.empty(), 1);

        minigameTaskDTO1.setGame(Minigame.BUGFINDER);
        minigameTaskDTO1.setConfigurationId(UUID.randomUUID());

        minigameTaskDTO1 =
            minigameTaskService.updateMinigameTaskFromArea(
                initialCourseDTO.getId(),
                1,
                Optional.empty(),
                1,
                minigameTaskDTO1
            );

        minigameTaskDTO2 = minigameTaskService.getMinigameTaskFromArea(initialCourseDTO.getId(), 1, Optional.of(1), 1);

        minigameTaskDTO2.setGame(Minigame.CROSSWORDPUZZLE);
        minigameTaskDTO2.setConfigurationId(UUID.randomUUID());

        minigameTaskDTO2 =
            minigameTaskService.updateMinigameTaskFromArea(
                initialCourseDTO.getId(),
                1,
                Optional.of(1),
                1,
                minigameTaskDTO2
            );

        minigameTaskDTO3 =
            minigameTaskService.getMinigameTaskFromArea(initialCourseDTO.getId(), 2, Optional.empty(), 1);

        minigameTaskDTO3.setGame(Minigame.BUGFINDER);
        minigameTaskDTO3.setConfigurationId(UUID.randomUUID());

        minigameTaskDTO3 =
            minigameTaskService.updateMinigameTaskFromArea(
                initialCourseDTO.getId(),
                2,
                Optional.empty(),
                1,
                minigameTaskDTO3
            );

        initialCourse = courseService.getCourse(initialCourseDTO.getId());

        initialCourseDTO = courseMapper.courseToCourseDTO(initialCourse);

        final PlayerRegistrationDTO playerRegistrationDTO = new PlayerRegistrationDTO("UserId", "Username");

        playerStatisticDTO =
            playerStatisticService.createPlayerStatisticInCourse(initialCourseDTO.getId(), playerRegistrationDTO);

        assert playerStatisticDTO.getId() != null;
        playerStatistic = playerStatisticRepository.findById(playerStatisticDTO.getId()).get();
    }

    @Test
    void testUnlockingAfterAreaCompleted() {
        final PlayerTaskStatisticData playerTaskStatisticData1 = new PlayerTaskStatisticData(
            minigameTaskDTO1.getGame(),
            minigameTaskDTO1.getConfigurationId(),
            100,
            playerStatisticDTO.getUserId()
        );
        playerTaskStatisticService.submitData(playerTaskStatisticData1);

        final PlayerTaskStatisticData playerTaskStatisticData2 = new PlayerTaskStatisticData(
            minigameTaskDTO2.getGame(),
            minigameTaskDTO2.getConfigurationId(),
            100,
            playerStatisticDTO.getUserId()
        );
        playerTaskStatisticService.submitData(playerTaskStatisticData2);

        final PlayerTaskStatisticData playerTaskStatisticData3 = new PlayerTaskStatisticData(
            minigameTaskDTO3.getGame(),
            minigameTaskDTO3.getConfigurationId(),
            100,
            playerStatisticDTO.getUserId()
        );
        playerTaskStatisticService.submitData(playerTaskStatisticData3);

        assertTrue(playerStatistic.getUnlockedAreas().contains(initialCourse.getWorlds().get(0)));
        assertTrue(playerStatistic.getUnlockedAreas().contains(initialCourse.getWorlds().get(0).getDungeons().get(0)));
        assertTrue(playerStatistic.getUnlockedAreas().contains(initialCourse.getWorlds().get(1)));

        minigameTaskDTO4 = minigameTaskService.getMinigameTaskFromArea(initialCourseDTO.getId(), 2, Optional.of(1), 1);

        minigameTaskDTO4.setGame(Minigame.BUGFINDER);
        minigameTaskDTO4.setConfigurationId(UUID.randomUUID());

        minigameTaskDTO4 =
            minigameTaskService.updateMinigameTaskFromArea(
                initialCourseDTO.getId(),
                2,
                Optional.of(1),
                1,
                minigameTaskDTO3
            );

        playerStatistic = playerStatisticRepository.findById(playerStatisticDTO.getId()).get();

        assertTrue(playerStatistic.getUnlockedAreas().contains(initialCourse.getWorlds().get(1).getDungeons().get(0)));
        assertEquals(4, playerStatistic.getUnlockedAreas().size());
    }

    @Test
    void testNotUnlockingIfAreaNotCompleted() {
        final PlayerTaskStatisticData playerTaskStatisticData1 = new PlayerTaskStatisticData(
            minigameTaskDTO1.getGame(),
            minigameTaskDTO1.getConfigurationId(),
            100,
            playerStatisticDTO.getUserId()
        );
        playerTaskStatisticService.submitData(playerTaskStatisticData1);

        final PlayerTaskStatisticData playerTaskStatisticData2 = new PlayerTaskStatisticData(
            minigameTaskDTO2.getGame(),
            minigameTaskDTO2.getConfigurationId(),
            100,
            playerStatisticDTO.getUserId()
        );
        playerTaskStatisticService.submitData(playerTaskStatisticData2);

        final PlayerTaskStatisticData playerTaskStatisticData3 = new PlayerTaskStatisticData(
            minigameTaskDTO3.getGame(),
            minigameTaskDTO3.getConfigurationId(),
            0,
            playerStatisticDTO.getUserId()
        );
        playerTaskStatisticService.submitData(playerTaskStatisticData3);

        assertTrue(playerStatistic.getUnlockedAreas().contains(initialCourse.getWorlds().get(0)));
        assertTrue(playerStatistic.getUnlockedAreas().contains(initialCourse.getWorlds().get(0).getDungeons().get(0)));
        assertTrue(playerStatistic.getUnlockedAreas().contains(initialCourse.getWorlds().get(1)));

        minigameTaskDTO4 = minigameTaskService.getMinigameTaskFromArea(initialCourseDTO.getId(), 2, Optional.of(1), 1);

        minigameTaskDTO4.setGame(Minigame.BUGFINDER);
        minigameTaskDTO4.setConfigurationId(UUID.randomUUID());

        minigameTaskDTO4 =
            minigameTaskService.updateMinigameTaskFromArea(
                initialCourseDTO.getId(),
                2,
                Optional.of(1),
                1,
                minigameTaskDTO3
            );
        playerStatistic = playerStatisticRepository.findById(playerStatisticDTO.getId()).get();
        assertFalse(playerStatistic.getUnlockedAreas().contains(initialCourse.getWorlds().get(1).getDungeons().get(0)));
        assertEquals(3, playerStatistic.getUnlockedAreas().size());
    }

    @Test
    void testUnlockingBetween() {
        final PlayerTaskStatisticData playerTaskStatisticData1 = new PlayerTaskStatisticData(
            minigameTaskDTO1.getGame(),
            minigameTaskDTO1.getConfigurationId(),
            100,
            playerStatisticDTO.getUserId()
        );
        playerTaskStatisticService.submitData(playerTaskStatisticData1);

        final PlayerTaskStatisticData playerTaskStatisticData2 = new PlayerTaskStatisticData(
            minigameTaskDTO2.getGame(),
            minigameTaskDTO2.getConfigurationId(),
            100,
            playerStatisticDTO.getUserId()
        );
        playerTaskStatisticService.submitData(playerTaskStatisticData2);

        final PlayerTaskStatisticData playerTaskStatisticData3 = new PlayerTaskStatisticData(
            minigameTaskDTO3.getGame(),
            minigameTaskDTO3.getConfigurationId(),
            100,
            playerStatisticDTO.getUserId()
        );
        playerTaskStatisticService.submitData(playerTaskStatisticData3);

        assertTrue(playerStatistic.getUnlockedAreas().contains(initialCourse.getWorlds().get(0)));
        assertTrue(playerStatistic.getUnlockedAreas().contains(initialCourse.getWorlds().get(0).getDungeons().get(0)));
        assertTrue(playerStatistic.getUnlockedAreas().contains(initialCourse.getWorlds().get(1)));

        minigameTaskDTO4 = minigameTaskService.getMinigameTaskFromArea(initialCourseDTO.getId(), 1, Optional.of(2), 1);

        minigameTaskDTO4.setGame(Minigame.BUGFINDER);
        minigameTaskDTO4.setConfigurationId(UUID.randomUUID());

        minigameTaskDTO4 =
            minigameTaskService.updateMinigameTaskFromArea(
                initialCourseDTO.getId(),
                1,
                Optional.of(2),
                1,
                minigameTaskDTO3
            );

        playerStatistic = playerStatisticRepository.findById(playerStatisticDTO.getId()).get();

        assertTrue(playerStatistic.getUnlockedAreas().contains(initialCourse.getWorlds().get(0).getDungeons().get(1)));
        assertEquals(4, playerStatistic.getUnlockedAreas().size());
    }
}
