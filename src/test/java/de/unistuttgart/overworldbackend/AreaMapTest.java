package de.unistuttgart.overworldbackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.enums.AreaStyle;
import de.unistuttgart.overworldbackend.data.enums.BarrierType;
import de.unistuttgart.overworldbackend.data.enums.FacingDirection;
import de.unistuttgart.overworldbackend.data.mapper.AreaMapMapper;
import de.unistuttgart.overworldbackend.data.mapper.WorldMapper;
import de.unistuttgart.overworldbackend.repositories.CourseRepository;
import de.unistuttgart.overworldbackend.repositories.WorldRepository;
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

import javax.servlet.http.Cookie;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
@SpringBootTest
@Testcontainers
public class AreaMapTest {

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
    private WorldRepository worldRepository;

    @Autowired
    private WorldMapper worldMapper;

    @Autowired
    private AreaMapMapper areaMapMapper;

    private Course initialCourse;
    private World initialWorld;
    private WorldDTO initialWorldDTO;

    private AreaMap initialAreaMap;

    private AreaMapDTO initialAreaMapDTO;

    private ObjectMapper objectMapper;

    private String fullURL;

    @BeforeEach
    public void createBasicData(){
        courseRepository.deleteAll();

        final World world = new World();
        world.setIndex(1);
        world.setStaticName("World 1");
        world.setTopicName("Topic");
        world.setActive(true);
        world.setMinigameTasks(new HashSet<>());
        world.setNpcs(new HashSet<>());
        world.setBooks(new HashSet<>());
        world.setDungeons(new ArrayList<>());
        AreaMap areaMap = new AreaMap(world);
        world.setAreaMap(areaMap);

        final Course course = new Course(
                "Gamify",
                "SS-23",
                "Basic lecture of computer science students",
                true,
                List.of(world)
        );

        initialCourse = courseRepository.save(course);
        initialWorld = initialCourse.getWorlds().stream().findFirst().get();
        initialWorldDTO = worldMapper.worldToWorldDTO(initialWorld);
        initialAreaMap = initialWorld.getAreaMap();
        initialAreaMapDTO = areaMapMapper.areaMapToAreaMapDTO(initialAreaMap);

        assertNotNull(initialWorld.getId());
        assertNotNull(initialWorldDTO.getId());

        fullURL = String.format("/courses/%d/areaMaps", initialCourse.getId());

        objectMapper = new ObjectMapper();

        doNothing().when(jwtValidatorService).validateTokenOrThrow("testToken");
        when(jwtValidatorService.extractUserId("testToken")).thenReturn("testUser");
    }

    /**
     * Checks that retrieving an existing area map works
     * @throws Exception
     */
    @Test
    void getAreaMaps() throws Exception {
        final MvcResult result = mvc
                .perform(get(fullURL).cookie(cookie).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final List<AreaMapDTO> areaMapDTOs = List.of(
                objectMapper.readValue(result.getResponse().getContentAsString(), AreaMapDTO[].class)
        );

        final AreaMapDTO areaMapDTO = areaMapDTOs
                .stream()
                .findFirst()
                .get();

        assertSame(1, areaMapDTOs.size());
        assertEquals(initialAreaMapDTO.getId(), areaMapDTO.getId());
        assertEquals(initialAreaMapDTO, areaMapDTO);
    }

    /**
     * Checks that retrieving all area maps works
     * @throws Exception
     */
    @Test
    void getAreaMap() throws Exception {
        final MvcResult result = mvc
                .perform(get(fullURL + "/" + initialWorldDTO.getIndex()).cookie(cookie).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final AreaMapDTO areaMapDTO = objectMapper.readValue(result.getResponse().getContentAsString(), AreaMapDTO.class);

        assertEquals(initialAreaMapDTO.getId(), areaMapDTO.getId());
        assertEquals(initialAreaMapDTO, areaMapDTO);
    }

    /**
     * Checks that retrieving a not exisiting area map throws a not found error
     * @throws Exception
     */
    @Test
    void getAreaMap_DoesNotExist_ThrowsNotFound() throws Exception {
        final MvcResult result = mvc
                .perform(get(fullURL + "/" + Integer.MAX_VALUE).cookie(cookie).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    /**
     * Checks that updating an area map with a valid AreaMapDTO updates all values
     * @throws Exception
     */
    @Test
    void updateAreaMap() throws Exception {
        final AreaMapDTO updatedAreaMapDTO = areaMapMapper.areaMapToAreaMapDTO(initialAreaMap);
        updatedAreaMapDTO.setGeneratedArea(true);
        updatedAreaMapDTO.setCustomAreaMap(getSmallCustomAreaMapDTO());

        final String bodyValue = objectMapper.writeValueAsString(updatedAreaMapDTO);

        final MvcResult result = mvc
                .perform(
                        put(fullURL + "/" + initialWorldDTO.getIndex())
                                .cookie(cookie)
                                .content(bodyValue)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        final AreaMapDTO updatedAreaMapDTOResult = objectMapper.readValue(
                result.getResponse().getContentAsString(), AreaMapDTO.class
        );

        assertTrue(updatedAreaMapDTOResult.isGeneratedArea());

        assertNotNull(updatedAreaMapDTOResult.getCustomAreaMap());

        assertEquals(1, updatedAreaMapDTOResult.getCustomAreaMap().getLayout().getArea().getWorldIndex());
        assertEquals(0, updatedAreaMapDTOResult.getCustomAreaMap().getLayout().getArea().getDungeonIndex());
        assertEquals(1, updatedAreaMapDTOResult.getCustomAreaMap().getLayout().getSizeX());
        assertEquals(1, updatedAreaMapDTOResult.getCustomAreaMap().getLayout().getSizeY());
        assertEquals("DRUNKARDS_WALK", updatedAreaMapDTOResult.getCustomAreaMap().getLayout().getGeneratorType());
        assertEquals("seed", updatedAreaMapDTOResult.getCustomAreaMap().getLayout().getSeed());
        assertEquals(66, updatedAreaMapDTOResult.getCustomAreaMap().getLayout().getAccessability());
        assertEquals("SAVANNA", updatedAreaMapDTOResult.getCustomAreaMap().getLayout().getStyle());

        assertEquals(1, updatedAreaMapDTOResult.getCustomAreaMap().getMinigameSpots().size());
        assertEquals(1, updatedAreaMapDTOResult.getCustomAreaMap().getNpcSpots().size());
        assertEquals(1, updatedAreaMapDTOResult.getCustomAreaMap().getBookSpots().size());
        assertEquals(1, updatedAreaMapDTOResult.getCustomAreaMap().getTeleporterSpots().size());
        assertEquals(1, updatedAreaMapDTOResult.getCustomAreaMap().getBarrierSpots().size());
        assertEquals(1, updatedAreaMapDTOResult.getCustomAreaMap().getSceneTransitionSpots().size());
    }

    /**
     * Checks that updating an area map creating minigame task / npc / book objects in its world
     * @throws Exception
     */
    @Test
    void updateAreaMap_CreateObjects() throws Exception {
        final AreaMapDTO updatedAreaMapDTO = areaMapMapper.areaMapToAreaMapDTO(initialAreaMap);
        updatedAreaMapDTO.setGeneratedArea(true);
        updatedAreaMapDTO.setCustomAreaMap(getBigCustomAreaMapDTO());

        final String bodyValue = objectMapper.writeValueAsString(updatedAreaMapDTO);

        final MvcResult result = mvc
                .perform(
                        put(fullURL + "/" + initialWorldDTO.getIndex())
                                .cookie(cookie)
                                .content(bodyValue)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        final AreaMapDTO updatedAreaMapDTOResult = objectMapper.readValue(
                result.getResponse().getContentAsString(), AreaMapDTO.class
        );

        World world = worldRepository.findByIndexAndCourseId(initialWorld.getIndex(), initialCourse.getId()).get();

        assertEquals(15, world.getMinigameTasks().size());
        assertEquals(12, world.getNpcs().size());
        assertEquals(10, world.getBooks().size());
    }

    /**
     * Checks that updating an area map creates dungeons in its world
     * @throws Exception
     */
    @Test
    void updateAreaMap_CreateDungeons() throws Exception {
        final AreaMapDTO updatedAreaMapDTO = areaMapMapper.areaMapToAreaMapDTO(initialAreaMap);
        updatedAreaMapDTO.setGeneratedArea(true);
        updatedAreaMapDTO.setCustomAreaMap(getBigCustomAreaMapDTO());

        final String bodyValue = objectMapper.writeValueAsString(updatedAreaMapDTO);

        final MvcResult result = mvc
                .perform(
                        put(fullURL + "/" + initialWorldDTO.getIndex())
                                .cookie(cookie)
                                .content(bodyValue)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        final AreaMapDTO updatedAreaMapDTOResult = objectMapper.readValue(
                result.getResponse().getContentAsString(), AreaMapDTO.class
        );

        World world = worldRepository.findByIndexAndCourseId(initialWorld.getIndex(), initialCourse.getId()).get();

        assertEquals(5, world.getDungeons().size());

        world.getDungeons().forEach(dungeon -> {
            assertEquals(12, dungeon.getMinigameTasks().size());
            assertEquals(10, dungeon.getNpcs().size());
            assertEquals(5, dungeon.getBooks().size());
        });
    }

    /**
     * Checks that updating an area map removes minigame task / npc / book objects in its world
     * @throws Exception
     */
    @Test
    void updateAreaMap_RemovesObjects() throws Exception {
        final AreaMapDTO updatedAreaMapDTO = areaMapMapper.areaMapToAreaMapDTO(initialAreaMap);
        updatedAreaMapDTO.setGeneratedArea(true);
        updatedAreaMapDTO.setCustomAreaMap(getSmallCustomAreaMapDTO());

        final String bodyValue = objectMapper.writeValueAsString(updatedAreaMapDTO);

        final MvcResult result = mvc
                .perform(
                        put(fullURL + "/" + initialWorldDTO.getIndex())
                                .cookie(cookie)
                                .content(bodyValue)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        final AreaMapDTO updatedAreaMapDTOResult = objectMapper.readValue(
                result.getResponse().getContentAsString(), AreaMapDTO.class
        );

        World world = worldRepository.findByIndexAndCourseId(initialWorld.getIndex(), initialCourse.getId()).get();

        assertEquals(1, world.getMinigameTasks().size());
        assertEquals(1, world.getNpcs().size());
        assertEquals(1, world.getBooks().size());
    }

    /**
     * Checks that updating an area map removes dungeons in its world
     * @throws Exception
     */
    @Test
    void updateAreaMap_RemoveDungeons() throws Exception {
        final AreaMapDTO updatedAreaMapDTO = areaMapMapper.areaMapToAreaMapDTO(initialAreaMap);
        updatedAreaMapDTO.setGeneratedArea(true);
        updatedAreaMapDTO.setCustomAreaMap(getSmallCustomAreaMapDTO());

        final String bodyValue = objectMapper.writeValueAsString(updatedAreaMapDTO);

        final MvcResult result = mvc
                .perform(
                        put(fullURL + "/" + initialWorldDTO.getIndex())
                                .cookie(cookie)
                                .content(bodyValue)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        final AreaMapDTO updatedAreaMapDTOResult = objectMapper.readValue(
                result.getResponse().getContentAsString(), AreaMapDTO.class
        );

        World world = worldRepository.findByIndexAndCourseId(initialWorld.getIndex(), initialCourse.getId()).get();

        assertEquals(1, world.getDungeons().size());

        Dungeon dungeon = world.getDungeons().stream().findFirst().get();

        assertEquals(12, dungeon.getMinigameTasks().size());
        assertEquals(10, dungeon.getNpcs().size());
        assertEquals(5, dungeon.getBooks().size());
    }

    /**
     * Checks that resetting an area map works
     * @throws Exception
     */
    @Test
    void updateAreaMap_ResetFlag() throws Exception {
        final AreaMapDTO updatedAreaMapDTO = areaMapMapper.areaMapToAreaMapDTO(initialAreaMap);
        updatedAreaMapDTO.setGeneratedArea(false);

        final String bodyValue = objectMapper.writeValueAsString(updatedAreaMapDTO);

        final MvcResult result = mvc
                .perform(
                        put(fullURL + "/" + initialWorldDTO.getIndex())
                                .cookie(cookie)
                                .content(bodyValue)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        final AreaMapDTO updatedAreaMapDTOResult = objectMapper.readValue(
                result.getResponse().getContentAsString(), AreaMapDTO.class
        );

        assertFalse(updatedAreaMapDTOResult.isGeneratedArea());
        assertNull(updatedAreaMapDTOResult.getCustomAreaMap());
    }

    /**
     * Checks that resetting an area map resets the objects in its world and dungeons to default
     * @throws Exception
     */
    @Test
    void updateAreaMap_ResetToDefaultObjects() throws  Exception {
        final AreaMapDTO updatedAreaMapDTO = areaMapMapper.areaMapToAreaMapDTO(initialAreaMap);
        updatedAreaMapDTO.setGeneratedArea(false);

        final String bodyValue = objectMapper.writeValueAsString(updatedAreaMapDTO);

        final MvcResult result = mvc
                .perform(
                        put(fullURL + "/" + initialWorldDTO.getIndex())
                                .cookie(cookie)
                                .content(bodyValue)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        final AreaMapDTO updatedAreaMapDTOResult = objectMapper.readValue(
                result.getResponse().getContentAsString(), AreaMapDTO.class
        );

        World world = worldRepository.findByIndexAndCourseId(initialWorld.getIndex(), initialCourse.getId()).get();
        assertEquals(12, world.getMinigameTasks().size());
        assertEquals(10, world.getNpcs().size());
        assertEquals(5, world.getBooks().size());

        assertEquals(4, world.getDungeons().size());

        world.getDungeons().forEach(dungeon -> {
            assertEquals(12, dungeon.getMinigameTasks().size());
            assertEquals(10, dungeon.getNpcs().size());
            assertEquals(5, dungeon.getBooks().size());
        });
    }

    /**
     * Creates and returns a custom area map dto containing one of each object type
     * @return
     */
    private CustomAreaMapDTO getSmallCustomAreaMapDTO() {
        CustomAreaMapDTO customAreaMapDTO = new CustomAreaMapDTO();

        LayoutDTO layoutDTO = new LayoutDTO();
        AreaLocationDTO area = new AreaLocationDTO();
        area.setWorldIndex(1);
        area.setDungeonIndex(0);
        layoutDTO.setArea(area);
        layoutDTO.setSizeX(1);
        layoutDTO.setSizeY(1);
        layoutDTO.setGeneratorType("DRUNKARDS_WALK");
        layoutDTO.setSeed("seed");
        layoutDTO.setAccessability(66);
        layoutDTO.setStyle("SAVANNA");
        customAreaMapDTO.setLayout(layoutDTO);

        AreaLocationDTO areaLocationDTO = new AreaLocationDTO();
        areaLocationDTO.setWorldIndex(1);
        areaLocationDTO.setDungeonIndex(0);

        PositionDTO position = new PositionDTO();
        position.setX(0);
        position.setY(0);

        MinigameSpotDTO minigameSpotDTO = new MinigameSpotDTO();
        minigameSpotDTO.setArea(areaLocationDTO);
        minigameSpotDTO.setPosition(position);
        minigameSpotDTO.setIndex(1);
        customAreaMapDTO.setMinigameSpots(List.of(minigameSpotDTO));

        NPCSpotDTO npcSpotDTO = new NPCSpotDTO();
        npcSpotDTO.setArea(areaLocationDTO);
        npcSpotDTO.setPosition(position);
        npcSpotDTO.setIndex(1);
        npcSpotDTO.setName("NPC");
        npcSpotDTO.setSpriteName("Sprite");
        npcSpotDTO.setIconName("Icon");
        customAreaMapDTO.setNpcSpots(List.of(npcSpotDTO));

        BookSpotDTO bookSpotDTO = new BookSpotDTO();
        bookSpotDTO.setArea(areaLocationDTO);
        bookSpotDTO.setPosition(position);
        bookSpotDTO.setIndex(1);
        bookSpotDTO.setName("Book");
        customAreaMapDTO.setBookSpots(List.of(bookSpotDTO));

        TeleporterSpotDTO teleporterSpotDTO = new TeleporterSpotDTO();
        teleporterSpotDTO.setArea(areaLocationDTO);
        teleporterSpotDTO.setPosition(position);
        teleporterSpotDTO.setIndex(1);
        teleporterSpotDTO.setName("Teleporter 1");
        customAreaMapDTO.setTeleporterSpots(List.of(teleporterSpotDTO));

        BarrierSpotDTO barrierSpotDTO = new BarrierSpotDTO();
        barrierSpotDTO.setArea(areaLocationDTO);
        barrierSpotDTO.setPosition(position);
        barrierSpotDTO.setType(BarrierType.worldBarrier);
        barrierSpotDTO.setDestinationAreaIndex(2);
        customAreaMapDTO.setBarrierSpots(List.of(barrierSpotDTO));

        SceneTransitionSpotDTO sceneTransitionSpotDTO = new SceneTransitionSpotDTO();
        sceneTransitionSpotDTO.setArea(areaLocationDTO);
        sceneTransitionSpotDTO.setPosition(position);
        PositionDTO size = new PositionDTO();
        size.setX(1);
        size.setY(1);
        sceneTransitionSpotDTO.setSize(size);
        sceneTransitionSpotDTO.setAreaToLoad(areaLocationDTO);
        sceneTransitionSpotDTO.setFacingDirection(FacingDirection.SOUTH);
        customAreaMapDTO.setSceneTransitionSpots(List.of(sceneTransitionSpotDTO));

        return customAreaMapDTO;
    }

    /**
     * Creates and returns a custom area map dto containing more than default of each object type
     * @return
     */
    private CustomAreaMapDTO getBigCustomAreaMapDTO() {
        CustomAreaMapDTO customAreaMapDTO = new CustomAreaMapDTO();

        LayoutDTO layoutDTO = new LayoutDTO();
        AreaLocationDTO area = new AreaLocationDTO();
        area.setWorldIndex(1);
        area.setWorldIndex(0);
        layoutDTO.setArea(area);
        layoutDTO.setSizeX(1);
        layoutDTO.setSizeY(1);
        layoutDTO.setGeneratorType("DRUNKARDS_WALK");
        layoutDTO.setSeed("seed");
        layoutDTO.setAccessability(66);
        layoutDTO.setStyle("SAVANNA");
        customAreaMapDTO.setLayout(layoutDTO);

        AreaLocationDTO areaLocationDTO = new AreaLocationDTO();
        areaLocationDTO.setWorldIndex(1);
        areaLocationDTO.setDungeonIndex(0);

        PositionDTO position = new PositionDTO();
        position.setX(0);
        position.setY(0);

        MinigameSpotDTO minigameSpotDTO = new MinigameSpotDTO();
        minigameSpotDTO.setArea(areaLocationDTO);
        minigameSpotDTO.setPosition(position);
        minigameSpotDTO.setIndex(1);
        List<MinigameSpotDTO> minigameSpots = new ArrayList<>();
        for(int i=0; i<15; i++)
        {
            minigameSpots.add(minigameSpotDTO);
        }
        customAreaMapDTO.setMinigameSpots(minigameSpots);

        NPCSpotDTO npcSpotDTO = new NPCSpotDTO();
        npcSpotDTO.setArea(areaLocationDTO);
        npcSpotDTO.setPosition(position);
        npcSpotDTO.setIndex(1);
        npcSpotDTO.setName("NPC");
        npcSpotDTO.setSpriteName("Sprite");
        npcSpotDTO.setIconName("Icon");
        List<NPCSpotDTO> npcSpots = new ArrayList<>();
        for(int i=0; i<12; i++)
        {
            npcSpots.add(npcSpotDTO);
        }
        customAreaMapDTO.setNpcSpots(npcSpots);

        BookSpotDTO bookSpotDTO = new BookSpotDTO();
        bookSpotDTO.setArea(areaLocationDTO);
        bookSpotDTO.setPosition(position);
        bookSpotDTO.setIndex(1);
        bookSpotDTO.setName("Book");
        List<BookSpotDTO> books = new ArrayList<>();
        for(int i=0; i<10; i++)
        {
            books.add(bookSpotDTO);
        }
        customAreaMapDTO.setBookSpots(books);

        SceneTransitionSpotDTO sceneTransitionSpotDTO = new SceneTransitionSpotDTO();
        sceneTransitionSpotDTO.setArea(areaLocationDTO);
        sceneTransitionSpotDTO.setPosition(position);
        PositionDTO size = new PositionDTO();
        size.setX(1);
        size.setY(1);
        sceneTransitionSpotDTO.setSize(size);
        sceneTransitionSpotDTO.setAreaToLoad(areaLocationDTO);
        sceneTransitionSpotDTO.setFacingDirection(FacingDirection.SOUTH);
        List<SceneTransitionSpotDTO> dungeons = new ArrayList<>();
        for(int i=0; i<5; i++)
        {
            dungeons.add(sceneTransitionSpotDTO);
        }
        customAreaMapDTO.setSceneTransitionSpots(dungeons);

        return customAreaMapDTO;
    }

}
