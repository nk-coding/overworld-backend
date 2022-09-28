package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.data.Course;
import de.unistuttgart.overworldbackend.data.World;
import de.unistuttgart.overworldbackend.data.WorldDTO;
import de.unistuttgart.overworldbackend.data.mapper.WorldMapper;
import de.unistuttgart.overworldbackend.repositories.CourseRepository;
import java.util.Arrays;
import java.util.Set;
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
class WorldControllerTest {

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
    private WorldMapper worldMapper;

    private String fullURL;
    private ObjectMapper objectMapper;

    private Course initialCourse;
    private World initialWorld;
    private WorldDTO initialWorldDTO;

    @BeforeEach
    public void createBasicData() {
        courseRepository.deleteAll();

        final World world = new World();
        world.setIndex(1);
        world.setStaticName("Winter Wonderland");
        world.setTopicName("UML Winter");
        world.setActive(true);
        world.setMinigameTasks(Set.of());
        world.setNpcs(Set.of());
        world.setBooks(Set.of());
        world.setDungeons(Arrays.asList());

        final Course course = new Course(
            "PSE",
            "SS-22",
            "Basic lecture of computer science students",
            true,
            Arrays.asList(world)
        );
        initialCourse = courseRepository.save(course);
        initialWorld = initialCourse.getWorlds().stream().findAny().get();
        initialWorldDTO = worldMapper.worldToWorldDTO(initialWorld);

        assertNotNull(initialWorld.getId());
        assertNotNull(initialWorldDTO.getId());

        fullURL = String.format("/courses/%d/worlds", initialCourse.getId());

        objectMapper = new ObjectMapper();

        doNothing().when(jwtValidatorService).validateTokenOrThrow("testToken");
        when(jwtValidatorService.extractUserId("testToken")).thenReturn("testUser");
    }

    @Test
    void getWorldsFromCourse() throws Exception {
        final MvcResult result = mvc
            .perform(get(fullURL).cookie(cookie).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        final Set<WorldDTO> worlds = Set.of(
            objectMapper.readValue(result.getResponse().getContentAsString(), WorldDTO[].class)
        );
        final WorldDTO worldDTO = worlds
            .stream()
            .filter(world -> world.getIndex() == initialWorldDTO.getIndex())
            .findAny()
            .get();
        assertSame(1, worlds.size());
        assertEquals(initialWorldDTO.getId(), worldDTO.getId());
        assertEquals(initialWorldDTO, worldDTO);
    }

    @Test
    void getWorldFromCourse() throws Exception {
        final MvcResult result = mvc
            .perform(
                get(fullURL + "/" + initialWorldDTO.getIndex()).cookie(cookie).contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andReturn();

        final WorldDTO worldDTO = objectMapper.readValue(result.getResponse().getContentAsString(), WorldDTO.class);

        assertEquals(initialWorldDTO.getId(), worldDTO.getId());
        assertEquals(initialWorldDTO, worldDTO);
    }

    @Test
    void getWorldFromCourse_DoesNotExist_ThrowsNotFound() throws Exception {
        mvc
            .perform(get(fullURL + "/" + Integer.MAX_VALUE).cookie(cookie).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn();
    }

    @Test
    void updateWorldFromCourse() throws Exception {
        final String newTopicName = "Closed Topic";
        final boolean newActiveStatus = false;
        final WorldDTO updatedWorldDTO = worldMapper.worldToWorldDTO(initialWorld);
        updatedWorldDTO.setActive(newActiveStatus);
        updatedWorldDTO.setTopicName(newTopicName);
        final String bodyValue = objectMapper.writeValueAsString(updatedWorldDTO);

        final MvcResult result = mvc
            .perform(
                put(fullURL + "/" + initialWorldDTO.getIndex())
                    .cookie(cookie)
                    .content(bodyValue)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andReturn();

        final WorldDTO updatedWorldDTOResult = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            WorldDTO.class
        );

        assertEquals(initialWorldDTO.getId(), updatedWorldDTOResult.getId());
        assertEquals(initialWorldDTO.getIndex(), updatedWorldDTOResult.getIndex());
        assertEquals(newTopicName, updatedWorldDTOResult.getTopicName());
        assertEquals(newActiveStatus, updatedWorldDTOResult.isActive());
        assertEquals(initialWorldDTO.getStaticName(), updatedWorldDTOResult.getStaticName());
        assertEquals(initialWorldDTO.getDungeons(), updatedWorldDTOResult.getDungeons());
        assertEquals(initialWorldDTO.getNpcs(), updatedWorldDTOResult.getNpcs());
        assertEquals(initialWorldDTO.getMinigameTasks(), updatedWorldDTOResult.getMinigameTasks());
        assertEquals(initialWorldDTO, updatedWorldDTOResult);
    }

    @Test
    void updateWorldFromCourse_DoNotUpdatedStaticName() throws Exception {
        final String newTopicName = "Closed Topic";
        final String newStaticName = "PSE World Override Static Name";
        final boolean newActiveStatus = false;
        final WorldDTO updatedWorldDTO = worldMapper.worldToWorldDTO(initialWorld);
        updatedWorldDTO.setActive(newActiveStatus);
        updatedWorldDTO.setTopicName(newTopicName);
        updatedWorldDTO.setStaticName(newStaticName);
        final String bodyValue = objectMapper.writeValueAsString(updatedWorldDTO);

        final MvcResult result = mvc
            .perform(
                put(fullURL + "/" + initialWorld.getIndex())
                    .cookie(cookie)
                    .content(bodyValue)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andReturn();

        final WorldDTO updatedWorldDTOResult = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            WorldDTO.class
        );

        assertEquals(initialWorldDTO.getId(), updatedWorldDTOResult.getId());
        assertEquals(initialWorldDTO.getIndex(), updatedWorldDTOResult.getIndex());
        assertEquals(newTopicName, updatedWorldDTOResult.getTopicName());
        assertEquals(newActiveStatus, updatedWorldDTOResult.isActive());
        assertNotEquals(newStaticName, updatedWorldDTOResult.getStaticName());
        assertEquals(initialWorldDTO.getStaticName(), updatedWorldDTOResult.getStaticName());
        assertEquals(initialWorldDTO.getDungeons(), updatedWorldDTOResult.getDungeons());
        assertEquals(initialWorldDTO.getNpcs(), updatedWorldDTOResult.getNpcs());
        assertEquals(initialWorldDTO.getMinigameTasks(), updatedWorldDTOResult.getMinigameTasks());
        assertEquals(initialWorldDTO, updatedWorldDTOResult);
    }
}
