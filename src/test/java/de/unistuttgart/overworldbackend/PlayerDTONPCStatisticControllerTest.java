package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.CourseMapper;
import de.unistuttgart.overworldbackend.data.mapper.NPCMapper;
import de.unistuttgart.overworldbackend.data.mapper.PlayerStatisticMapper;
import de.unistuttgart.overworldbackend.repositories.CourseRepository;
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
class PlayerDTONPCStatisticControllerTest {

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
    private PlayerNPCStatisticService playerNPCStatisticService;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private PlayerStatisticMapper playerstatisticMapper;

    @Autowired
    private NPCMapper npcMapper;

    private String fullURL;
    private String fullURLWithoutPlayerId;
    private ObjectMapper objectMapper;

    private Course initialCourse;
    private CourseDTO initialCourseDTO;

    private PlayerStatistic initialPlayerStatistic;
    private PlayerStatisticDTO initialPlayerStatisticDTO;

    private NPC initialNPC;

    private NPCDTO initialNpcDTO;

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

        final List<String> npcText = new ArrayList<>();
        npcText.add("NPCText");

        final NPC npc = new NPC();
        npc.setText(npcText);
        npc.setIndex(1);

        final Set<NPC> npcs = new HashSet<>();
        npcs.add(npc);

        final World world = new World();
        world.setStaticName("Winter Wonderland");
        world.setTopicName("UML Winter");
        world.setActive(true);
        world.setMinigameTasks(Set.of());
        world.setNpcs(npcs);
        world.setDungeons(dungeons);
        world.setBooks(Set.of());
        final List<World> worlds = new ArrayList<>();
        worlds.add(world);

        final Course course = new Course("PSE", "SS-22", "Basic lecture of computer science students", true, worlds);

        final PlayerStatistic playerstatistic = new PlayerStatistic();
        playerstatistic.setUserId("45h23o2j432");
        playerstatistic.setUsername("testUser");
        playerstatistic.setCourse(course);
        playerstatistic.setCurrentArea(world);
        playerstatistic.setKnowledge(new Random(10).nextLong());
        playerstatistic.setUnlockedAreas(new ArrayList<>());
        playerstatistic.setCompletedDungeons(new ArrayList<>());
        course.addPlayerStatistic(playerstatistic);

        initialCourse = courseRepository.save(course);
        initialCourseDTO = courseMapper.courseToCourseDTO(initialCourse);

        initialPlayerStatistic = course.getPlayerStatistics().stream().findAny().get();
        initialPlayerStatisticDTO = playerstatisticMapper.playerStatisticToPlayerstatisticDTO(initialPlayerStatistic);

        initialNPC = initialCourse.getWorlds().stream().findFirst().get().getNpcs().stream().findFirst().get();
        initialNpcDTO = npcMapper.npcToNPCDTO(initialNPC);

        assertNotNull(initialCourse.getCourseName());

        assertEquals(initialCourse.getId(), initialNPC.getCourse().getId());
        assertEquals(initialCourse.getId(), initialPlayerStatistic.getCourse().getId());

        assertEquals(initialCourse, initialNPC.getCourse());
        assertEquals(initialCourse.getWorlds().stream().findFirst().get(), initialNPC.getArea());

        fullURL =
            String.format(
                "/courses/%d/playerstatistics/%s/player-npc-statistics",
                initialCourse.getId(),
                initialPlayerStatistic.getUserId()
            );
        fullURLWithoutPlayerId =
            String.format("/courses/%d/playerstatistics/player-npc-statistics", initialCourse.getId());

        objectMapper = new ObjectMapper();

        doNothing().when(jwtValidatorService).validateTokenOrThrow("testToken");
        when(jwtValidatorService.extractUserId("testToken")).thenReturn(initialPlayerStatistic.getUserId());
    }

    @Test
    void getNPCStatistics() throws Exception {
        final PlayerNPCStatisticDTO statistic = playerNPCStatisticService.submitData(
            new PlayerNPCStatisticData(initialNPC.getId(), true, initialPlayerStatistic.getUserId())
        );

        final MvcResult result = mvc
            .perform(get(fullURL).cookie(cookie).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        final List<PlayerNPCStatisticDTO> playerNPCStatisticDTOs = Arrays.asList(
            objectMapper.readValue(result.getResponse().getContentAsString(), PlayerNPCStatisticDTO[].class)
        );
        assertEquals(statistic, playerNPCStatisticDTOs.get(0));
    }

    @Test
    void getOwnNPCStatistics() throws Exception {
        final PlayerNPCStatisticDTO statistic = playerNPCStatisticService.submitData(
            new PlayerNPCStatisticData(initialNPC.getId(), true, initialPlayerStatistic.getUserId())
        );

        final MvcResult result = mvc
            .perform(get(fullURLWithoutPlayerId).cookie(cookie).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        final List<PlayerNPCStatisticDTO> playerNPCStatisticDTOs = Arrays.asList(
            objectMapper.readValue(result.getResponse().getContentAsString(), PlayerNPCStatisticDTO[].class)
        );
        assertEquals(statistic, playerNPCStatisticDTOs.get(0));
    }

    @Test
    void getNPCStatistic() throws Exception {
        final PlayerNPCStatisticDTO statistic = playerNPCStatisticService.submitData(
            new PlayerNPCStatisticData(initialNPC.getId(), true, initialPlayerStatistic.getUserId())
        );

        final MvcResult result = mvc
            .perform(get(fullURL + "/" + statistic.getId()).cookie(cookie).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        final PlayerNPCStatisticDTO playerNPCStatisticDTO = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            PlayerNPCStatisticDTO.class
        );
        assertEquals(statistic, playerNPCStatisticDTO);
        assertEquals(initialNpcDTO, playerNPCStatisticDTO.getNpc());
        assertNotNull(playerNPCStatisticDTO.getNpc().getArea());
        assertEquals(initialNpcDTO.getArea(), playerNPCStatisticDTO.getNpc().getArea());
    }

    @Test
    void getOwnNPCStatistic() throws Exception {
        final PlayerNPCStatisticDTO statistic = playerNPCStatisticService.submitData(
            new PlayerNPCStatisticData(initialNPC.getId(), true, initialPlayerStatistic.getUserId())
        );

        final MvcResult result = mvc
            .perform(
                get(fullURLWithoutPlayerId + "/" + statistic.getId())
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andReturn();

        final PlayerNPCStatisticDTO playerNPCStatisticDTO = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            PlayerNPCStatisticDTO.class
        );
        assertEquals(statistic, playerNPCStatisticDTO);
        assertEquals(initialNpcDTO, playerNPCStatisticDTO.getNpc());
        assertNotNull(playerNPCStatisticDTO.getNpc().getArea());
        assertEquals(initialNpcDTO.getArea(), playerNPCStatisticDTO.getNpc().getArea());
    }

    @Test
    void getNPCStatistic_DoesNotExist_ThrowsNotFound() throws Exception {
        final PlayerNPCStatisticDTO statistic = playerNPCStatisticService.submitData(
            new PlayerNPCStatisticData(initialNPC.getId(), true, initialPlayerStatistic.getUserId())
        );

        final MvcResult result = mvc
            .perform(get(fullURL + "/" + UUID.randomUUID()).cookie(cookie).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn();
    }
}
