package de.unistuttgart.overworldbackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.enums.Minigame;
import de.unistuttgart.overworldbackend.repositories.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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

    @BeforeEach
    public void createBasicData() {
        courseRepository.deleteAll();

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

        initialTask = initialWorld.getMinigameTasks().stream().filter(task -> task.getIndex() == minigameTask1.getIndex()).findAny().get();

        assertNotNull(initialWorld.getId());
        assertNotNull(initialTask.getId());

        fullURL = String.format("/courses/%d/worlds/%d", initialCourse.getId(), initialWorld.getIndex());

        objectMapper = new ObjectMapper();

        doNothing().when(jwtValidatorService).validateTokenOrThrow("testToken");
        when(jwtValidatorService.extractUserId("testToken")).thenReturn("testUser");
    }
}
