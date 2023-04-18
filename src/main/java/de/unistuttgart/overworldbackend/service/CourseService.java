package de.unistuttgart.overworldbackend.service;

import aj.org.objectweb.asm.TypeReference;
import de.unistuttgart.overworldbackend.client.*;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.config.CourseConfig;
import de.unistuttgart.overworldbackend.data.config.DungeonConfig;
import de.unistuttgart.overworldbackend.data.config.WorldConfig;
import de.unistuttgart.overworldbackend.data.enums.Minigame;
import de.unistuttgart.overworldbackend.data.mapper.CourseMapper;
import de.unistuttgart.overworldbackend.repositories.CourseRepository;
import feign.FeignException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseService {

    private static final boolean DEFAULT_IS_ACTIVE = true;

    private static final String CLONE_ERROR_MESSAGE = "Encountered Exception";

    CourseConfig configCourse;

    @Autowired
    ChickenshockClient chickenshockClient;

    @Autowired
    MemoryClient memoryClient;

    @Autowired
    FinitequizClient finitequizClient;

    @Autowired
    TowercrushClient towercrushClient;

    @Autowired
    CrosswordpuzzleClient crosswordpuzzleClient;

    @Autowired
    BugfinderClient bugfinderClient;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseMapper courseMapper;

    public CourseService() {
        configCourse = new CourseConfig();
        final ObjectMapper mapper = new ObjectMapper();

        final InputStream inputStream = TypeReference.class.getResourceAsStream("/config.json");
        try {
            configCourse = mapper.readValue(inputStream, CourseConfig.class);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @throws ResponseStatusException (404) when course by its id could not be found
     * @param id the id of the course searching for
     * @return the found course
     */
    public Course getCourse(final int id) {
        return courseRepository
            .findById(id)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("There is no course with id %s.", id))
            );
    }

    /**
     * Update a course by its id.
     *
     * Only the course name and description is updatable.
     *
     * @throws ResponseStatusException (404) if course, world or dungeon by its id do not exist
     * @param courseId the id of the course that should get updated
     * @param courseDTO the updated parameters
     * @return the updated course as DTO
     */
    public CourseDTO updateCourse(final int courseId, final CourseDTO courseDTO) {
        final Course course = getCourse(courseId);
        course.setCourseName(courseDTO.getCourseName());
        course.setDescription(courseDTO.getDescription());
        course.setActive(courseDTO.isActive());
        course.setSemester(courseDTO.getSemester());
        final Course updatedCourse = courseRepository.save(course);
        return courseMapper.courseToCourseDTO(updatedCourse);
    }

    /**
     * Create a course with initial data.
     *
     * Creates a course with pre generated worlds, dungeons, minigame tasks and npcs.
     *
     * @param courseInit the initial data with the course should be created with
     * @return the created course as DTO with all its generated worlds, dungeons, minigame tasks and npcs
     */
    public CourseDTO createCourse(final CourseInitialData courseInit) {
        final List<World> worlds = new ArrayList<>();
        final AtomicInteger worldId = new AtomicInteger(1);
        configCourse.getWorlds().forEach(worldConfig -> configureWorld(worlds, worldId.getAndIncrement(), worldConfig));

        final Course course = new Course(
            courseInit.getCourseName(),
            courseInit.getSemester(),
            courseInit.getDescription(),
            DEFAULT_IS_ACTIVE,
            worlds
        );
        courseRepository.save(course);
        return courseMapper.courseToCourseDTO(course);
    }

    /**
     * Returns all courses
     * @return all courses
     */
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    /**
     * Delete a course by its id
     *
     * @throws ResponseStatusException (404) when course with its id does not exist
     * @param id the course that should be deleted
     * @return the deleted course as DTO
     */
    public CourseDTO deleteCourse(final int id) {
        final Course course = getCourse(id);
        courseRepository.delete(course);
        return courseMapper.courseToCourseDTO(course);
    }

    private void configureWorld(final List<World> worlds, final int worldId, final WorldConfig worldConfig) {
        final Set<MinigameTask> minigames = new HashSet<>();
        final Set<NPC> npcs = new HashSet<>();
        final Set<Book> books = new HashSet<>();
        final List<Dungeon> dungeons = new ArrayList<>();
        final AtomicInteger dungeonId = new AtomicInteger(1);
        worldConfig
            .getDungeons()
            .forEach(dungeonConfig -> dungeons.add(configureDungeon(dungeonId.getAndIncrement(), dungeonConfig)));
        configureArea(
            minigames,
            npcs,
            books,
            worldConfig.getNumberOfMinigames(),
            worldConfig.getNumberOfNPCs(),
            worldConfig.getNumberOfBooks()
        );
        final World world = new World(
            worldConfig.getStaticName(),
            "",
            false,
            minigames,
            npcs,
            books,
            dungeons,
            worldId
        );
        worlds.add(world);
    }

    private void configureArea(
        final Set<MinigameTask> minigames,
        final Set<NPC> npcs,
        final Set<Book> books,
        final int numberOfMinigames,
        final int numberOfNPCs,
        final int numberOfBooks
    ) {
        for (int minigameIndex = 1; minigameIndex <= numberOfMinigames; minigameIndex++) {
            final MinigameTask minigame = new MinigameTask(null, null, minigameIndex);
            minigames.add(minigame);
        }
        for (int npcIndex = 1; npcIndex <= numberOfNPCs; npcIndex++) {
            final NPC npc = new NPC(new ArrayList<>(), npcIndex);
            npcs.add(npc);
        }
        for (int bookIndex = 1; bookIndex <= numberOfBooks; bookIndex++) {
            final Book book = new Book("", bookIndex);
            books.add(book);
        }
    }

    private Dungeon configureDungeon(final int dungeonId, final DungeonConfig dungeonConfig) {
        final Set<MinigameTask> minigames = new HashSet<>();
        final Set<NPC> npcs = new HashSet<>();
        final Set<Book> books = new HashSet<>();
        configureArea(
            minigames,
            npcs,
            books,
            dungeonConfig.getNumberOfMinigames(),
            dungeonConfig.getNumberOfNPCs(),
            dungeonConfig.getNumberOfBooks()
        );
        return new Dungeon(dungeonConfig.getStaticName(), "", false, minigames, npcs, books, dungeonId);
    }

    public CourseCloneDTO cloneCourse(
        final int id,
        final CourseInitialData courseInitialData,
        final String accessToken
    ) {
        final Set<String> errorMessages = Collections.synchronizedSet(new HashSet<>());
        final Course course = getCourse(id);
        final Course cloneCourse = new Course(
            courseInitialData.getCourseName(),
            courseInitialData.getSemester(),
            courseInitialData.getDescription(),
            DEFAULT_IS_ACTIVE,
            course
                .getWorlds()
                .parallelStream()
                .map(world -> cloneWorld(world, accessToken, errorMessages))
                .collect(Collectors.toCollection(ArrayList::new))
        );

        courseRepository.save(cloneCourse);
        final CourseDTO courseDTO = courseMapper.courseToCourseDTO(cloneCourse);
        return new CourseCloneDTO(
            courseDTO.getId(),
            courseDTO.getCourseName(),
            courseDTO.getSemester(),
            courseDTO.getDescription(),
            courseDTO.isActive(),
            courseDTO.getWorlds(),
            errorMessages
        );
    }

    /**
     * Clones a world
     *
     * Configured needs to calculated because minigames can become not configured if the minigame-backend isn't available.
     * @param oldWorld world to be cloned
     * @param accessToken access Token in the cookie
     * @return cloned world
     */
    private World cloneWorld(final World oldWorld, final String accessToken, final Set<String> errorMessages) {
        final World world = new World(
            oldWorld.getStaticName(),
            oldWorld.getTopicName(),
            false,
            oldWorld.getMinigameTasks().stream().anyMatch(minigame -> Minigame.isConfigured(minigame.getGame())),
            oldWorld
                .getMinigameTasks()
                .parallelStream()
                .map(minigameTask -> cloneMinigameTask(minigameTask, accessToken, errorMessages))
                .collect(Collectors.toCollection(HashSet::new)),
            oldWorld.getNpcs().parallelStream().map(this::cloneNPC).collect(Collectors.toCollection(HashSet::new)),
            oldWorld.getBooks().parallelStream().map(this::cloneBook).collect(Collectors.toCollection(HashSet::new)),
            oldWorld
                .getDungeons()
                .parallelStream()
                .map(dungeon -> cloneDungeon(dungeon, accessToken, errorMessages))
                .sorted(Comparator.comparingInt(Area::getIndex))
                .collect(Collectors.toCollection(ArrayList::new)),
            oldWorld.getIndex()
        );
        world.setConfigured(
            world.getMinigameTasks().stream().anyMatch((minigameTask -> Minigame.isConfigured(minigameTask.getGame())))
        );
        return world;
    }

    /**
     * Clones a dungeon
     *
     * Configured needs to calculated because minigames can become not configured if the minigame-backend isn't available.
     * @param oldDungeon dungeon to be cloned
     * @param accessToken access Token in the cookie
     * @return cloned dungeon
     */
    private Dungeon cloneDungeon(final Dungeon oldDungeon, final String accessToken, final Set<String> errorMessages) {
        final Dungeon dungeon = new Dungeon(
            oldDungeon.getStaticName(),
            oldDungeon.getTopicName(),
            false,
            oldDungeon
                .getMinigameTasks()
                .parallelStream()
                .map(minigameTask -> cloneMinigameTask(minigameTask, accessToken, errorMessages))
                .collect(Collectors.toCollection(HashSet::new)),
            oldDungeon.getNpcs().parallelStream().map(this::cloneNPC).collect(Collectors.toCollection(HashSet::new)),
            oldDungeon.getBooks().parallelStream().map(this::cloneBook).collect(Collectors.toCollection(HashSet::new)),
            oldDungeon.getIndex()
        );
        dungeon.setConfigured(
            dungeon
                .getMinigameTasks()
                .stream()
                .anyMatch((minigameTask -> Minigame.isConfigured(minigameTask.getGame())))
        );
        return dungeon;
    }

    private NPC cloneNPC(final NPC npc) {
        return new NPC(new ArrayList<>(npc.getText()), npc.getDescription(), npc.getIndex());
    }

    private Book cloneBook(final Book book) {
        return new Book(book.getText(), book.getDescription(), book.getIndex());
    }

    private MinigameTask cloneMinigameTask(
        final MinigameTask minigameTask,
        final String accessToken,
        final Set<String> errorMessages
    ) {
        if (minigameTask.getGame() == null) {
            return new MinigameTask(null, minigameTask.getDescription(), null, minigameTask.getIndex());
        }
        return cloneMinigame(minigameTask, accessToken, errorMessages);
    }

    private MinigameTask cloneMinigame(
        final MinigameTask minigameTask,
        final String accessToken,
        final Set<String> errorMessages
    ) {
        if (minigameTask.getConfigurationId() == null) {
            return new MinigameTask(
                minigameTask.getGame(),
                minigameTask.getDescription(),
                null,
                minigameTask.getIndex()
            );
        } else {
            try {
                UUID cloneId = null;
                switch (minigameTask.getGame()) {
                    case NONE:
                        return new MinigameTask(Minigame.NONE, null, minigameTask.getIndex());
                    case CHICKENSHOCK:
                        cloneId = chickenshockClient.postClone(accessToken, minigameTask.getConfigurationId());
                        break;
                    case FINITEQUIZ:
                        cloneId = finitequizClient.postClone(accessToken, minigameTask.getConfigurationId());
                        break;
                    case TOWERCRUSH:
                        cloneId = towercrushClient.postClone(accessToken, minigameTask.getConfigurationId());
                        break;
                    case CROSSWORDPUZZLE:
                        cloneId = crosswordpuzzleClient.postClone(accessToken, minigameTask.getConfigurationId());
                        break;
                    case BUGFINDER:
                        cloneId = bugfinderClient.postClone(accessToken, minigameTask.getConfigurationId());
                        break;
                    case MEMORY:
                        cloneId = memoryClient.postClone(accessToken,minigameTask.getConfigurationId());
                    default:
                        minigameTask.setGame(Minigame.NONE);
                }
                return new MinigameTask(
                    minigameTask.getGame(),
                    minigameTask.getDescription(),
                    cloneId,
                    minigameTask.getIndex()
                );
            } catch (final FeignException e) {
                log.error(CLONE_ERROR_MESSAGE, e);
                errorMessages.add(minigameTask.getGame() + "-backend not present");
                return new MinigameTask(minigameTask.getGame(), "", null, minigameTask.getIndex());
            }
        }
    }
}
