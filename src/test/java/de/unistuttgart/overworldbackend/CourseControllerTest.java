package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.CourseMapper;
import de.unistuttgart.overworldbackend.repositories.CourseRepository;
import java.util.*;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@AutoConfigureMockMvc
@Transactional
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CourseControllerTest {

  @Container
  public static PostgreSQLContainer postgresDB = new PostgreSQLContainer("postgres:14-alpine")
    .withDatabaseName("postgres")
    .withUsername("postgres")
    .withPassword("postgres");

  @DynamicPropertySource
  public static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgresDB::getJdbcUrl);
    registry.add("spring.datasource.username", postgresDB::getUsername);
    registry.add("spring.datasource.password", postgresDB::getPassword);
  }

  @Autowired
  private MockMvc mvc;

  @Autowired
  private CourseRepository courseRepository;

  @Autowired
  private CourseMapper courseMapper;

  private String fullURL;
  private ObjectMapper objectMapper;

  private Course initialCourse;
  private CourseDTO initialCourseDTO;

  @BeforeEach
  public void createBasicData() {
    courseRepository.deleteAll();

    final Dungeon dungeon = new Dungeon();
    dungeon.setStaticName("Dark Dungeon");
    dungeon.setTopicName("Dark UML");
    dungeon.setActive(true);
    dungeon.setMinigameTasks(Set.of());
    dungeon.setNpcs(Set.of());
    final List<Dungeon> dungeons = new ArrayList<>();

    final World world = new World();
    world.setStaticName("Winter Wonderland");
    world.setTopicName("UML Winter");
    world.setActive(true);
    world.setMinigameTasks(Set.of());
    world.setNpcs(Set.of());
    world.setDungeons(dungeons);
    final List<World> worlds = new ArrayList<>();
    worlds.add(world);

    final Course course = new Course("PSE", "SS-22", "Basic lecture of computer science students", true, worlds);
    initialCourse = courseRepository.save(course);
    initialCourseDTO = courseMapper.courseToCourseDTO(initialCourse);

    assertNotNull(initialCourse.getCourseName());

    fullURL = "/courses";

    objectMapper = new ObjectMapper();
  }

  @Test
  void getCourse() throws Exception {
    final MvcResult result = mvc
      .perform(get(fullURL + "/" + initialCourseDTO.getId()).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final CourseDTO courseDTOResult = objectMapper.readValue(
      result.getResponse().getContentAsString(),
      CourseDTO.class
    );

    assertEquals(initialCourseDTO, courseDTOResult);
    assertEquals(initialCourseDTO.getId(), courseDTOResult.getId());
  }

  @Test
  void getCourse_DoesNotExist_ThrowsNotFound() throws Exception {
    mvc
      .perform(get(fullURL + "/1").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  void getCourses() throws Exception {
    final MvcResult result = mvc.perform(get(fullURL)).andExpect(status().isOk()).andReturn();

    final Set<CourseDTO> courseDTOResult = Set.of(
      objectMapper.readValue(result.getResponse().getContentAsString(), CourseDTO[].class)
    );

    final CourseDTO courseDTO = courseDTOResult.stream().findFirst().get();
    assertSame(1, courseDTOResult.size());
    assertEquals(initialCourseDTO.getId(), courseDTO.getId());
    assertEquals(initialCourseDTO, courseDTO);
  }

  @Test
  void updateCourse() throws Exception {
    final CourseDTO courseToUpdate = new CourseDTO();
    courseToUpdate.setCourseName("Software-engineering");
    courseToUpdate.setDescription("Basic lecture of software engineering students");

    final String bodyValue = objectMapper.writeValueAsString(courseToUpdate);

    final MvcResult result = mvc
      .perform(put(fullURL + "/" + initialCourseDTO.getId()).content(bodyValue).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final CourseDTO updatedCourse = objectMapper.readValue(result.getResponse().getContentAsString(), CourseDTO.class);

    assertEquals(courseToUpdate.getCourseName(), updatedCourse.getCourseName());
    assertEquals(courseToUpdate.getDescription(), updatedCourse.getDescription());
    assertEquals(
      courseToUpdate.getCourseName(),
      courseRepository.getReferenceById(updatedCourse.getId()).getCourseName()
    );
    assertEquals(
      courseToUpdate.getDescription(),
      courseRepository.getReferenceById(updatedCourse.getId()).getDescription()
    );
  }

  @Test
  void deleteCourse() throws Exception {
    final MvcResult result = mvc
      .perform(delete(fullURL + "/" + initialCourseDTO.getId()).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final CourseDTO courseDTOResult = objectMapper.readValue(
      result.getResponse().getContentAsString(),
      CourseDTO.class
    );

    assertEquals(initialCourseDTO, courseDTOResult);
    assertEquals(initialCourseDTO.getId(), courseDTOResult.getId());
    assertTrue(courseRepository.findAll().isEmpty());
  }

  @Test
  void createCourse() throws Exception {
    final CourseInitialData toCreateCourse = new CourseInitialData("testName", "SS-22", "testDescription");
    final String bodyValue = objectMapper.writeValueAsString(toCreateCourse);

    final MvcResult result = mvc
      .perform(post(fullURL).content(bodyValue).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isCreated())
      .andReturn();

    final CourseDTO createdCourse = objectMapper.readValue(result.getResponse().getContentAsString(), CourseDTO.class);

    assertEquals(toCreateCourse.getCourseName(), createdCourse.getCourseName());
    assertEquals(toCreateCourse.getDescription(), createdCourse.getDescription());
    assertEquals(
      createdCourse.getCourseName(),
      courseRepository.getReferenceById(createdCourse.getId()).getCourseName()
    );
    assertEquals(
      createdCourse.getDescription(),
      courseRepository.getReferenceById(createdCourse.getId()).getDescription()
    );
  }

  @Test
  void createCourse_InvalidSemesterRegex_ThrowsException() throws Exception {
    final CourseInitialData toCreateCourse = new CourseInitialData("testName2", "Sommer 2021", "testDescription");
    final String bodyValue = objectMapper.writeValueAsString(toCreateCourse);

    mvc
      .perform(post(fullURL).content(bodyValue).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest());
  }

  @Test
  void updateCourse_InvalidSemesterRegex_ThrowsException() throws Exception {
    final CourseDTO courseToUpdate = new CourseDTO();
    courseToUpdate.setCourseName("Software-engineering");
    courseToUpdate.setDescription("Basic lecture of software engineering students");
    courseToUpdate.setSemester("Sommer 2022");

    final String bodyValue = objectMapper.writeValueAsString(courseToUpdate);

    mvc
      .perform(put(fullURL + "/" + initialCourseDTO.getId()).content(bodyValue).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest());
  }
}
