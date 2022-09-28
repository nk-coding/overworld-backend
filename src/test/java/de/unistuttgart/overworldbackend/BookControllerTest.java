package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.BookMapper;
import de.unistuttgart.overworldbackend.data.mapper.DungeonMapper;
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
class BookControllerTest {

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

  @MockBean
  JWTValidatorService jwtValidatorService;

  final Cookie cookie = new Cookie("access_token", "testToken");

  @Autowired
  private CourseRepository courseRepository;

  @Autowired
  private WorldMapper worldMapper;

  @Autowired
  private DungeonMapper dungeonMapper;

  @Autowired
  private BookMapper bookMapper;

  private String fullURL;
  private String fullDungeonURL;
  private ObjectMapper objectMapper;

  private Course initialCourse;
  private World initialWorld;
  private WorldDTO initialWorldDTO;
  private Dungeon initialDungoen;
  private DungeonDTO initialDungeonDTO;

  private Book initialBook;
  private BookDTO initialBookDTO;
  private Book initialDungeonBook;
  private BookDTO initialDungeonBookDTO;

  @BeforeEach
  public void createBasicData() {
    courseRepository.deleteAll();

    final Book book = new Book();
    book.setText("A large text");
    book.setIndex(1);

    final Book dungeonBook = new Book();
    dungeonBook.setText("A much bigger text for a dungeon");
    dungeonBook.setIndex(1);

    final Dungeon dungeon = new Dungeon();
    dungeon.setIndex(1);
    dungeon.setStaticName("Dark Dungeon");
    dungeon.setTopicName("Dark UML");
    dungeon.setActive(true);
    dungeon.setMinigameTasks(Set.of());
    dungeon.setNpcs(Set.of());
    dungeon.setBooks(Set.of(dungeonBook));

    final World world = new World();
    world.setIndex(1);
    world.setStaticName("Winter Wonderland");
    world.setTopicName("UML Winter");
    world.setActive(true);
    world.setMinigameTasks(Set.of());
    world.setNpcs(Set.of());
    world.setBooks(Set.of(book));
    world.setDungeons(Arrays.asList(dungeon));

    final Course course = new Course(
      "PSE",
      "SS-22",
      "Basic lecture of computer science students",
      true,
      Arrays.asList(world)
    );
    initialCourse = courseRepository.save(course);

    initialWorld = initialCourse.getWorlds().stream().findFirst().get();
    initialWorldDTO = worldMapper.worldToWorldDTO(initialWorld);

    initialDungoen = initialWorld.getDungeons().stream().findFirst().get();
    initialDungeonDTO = dungeonMapper.dungeonToDungeonDTO(initialDungoen);

    initialBook = initialWorld.getBooks().stream().findFirst().get();
    initialBookDTO = bookMapper.bookToBookDTO(initialBook);

    initialDungeonBook = initialDungoen.getBooks().stream().findFirst().get();
    initialDungeonBookDTO = bookMapper.bookToBookDTO(initialDungeonBook);

    assertNotNull(initialWorld.getId());
    assertNotNull(initialWorldDTO.getId());

    assertNotNull(initialBook.getId());
    assertNotNull(initialBookDTO.getId());

    assertNotNull(initialDungeonBook.getId());
    assertNotNull(initialDungeonBookDTO.getId());

    fullURL = String.format("/courses/%d/worlds/%d/books", initialCourse.getId(), initialWorld.getIndex());
    fullDungeonURL =
      String.format(
        "/courses/%d/worlds/%d/dungeons/%d/books",
        initialCourse.getId(),
        initialWorld.getIndex(),
        initialDungoen.getIndex()
      );

    objectMapper = new ObjectMapper();

    doNothing().when(jwtValidatorService).validateTokenOrThrow("testToken");
    when(jwtValidatorService.extractUserId("testToken")).thenReturn("testUser");
  }

  @Test
  void updateBookFromWorld_DoesNotExist_ThrowsNotFound() throws Exception {
    final BookDTO bookDTO = new BookDTO();
    bookDTO.setText("Hey ho");
    final String bodyValue = objectMapper.writeValueAsString(bookDTO);
    mvc
      .perform(
        put(fullURL + "/" + Integer.MAX_VALUE).cookie(cookie).content(bodyValue).contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  void updateBookFromWorld() throws Exception {
    final String newText = "New text incoming";
    final String newDescription = "Book with new text";
    final BookDTO updateBookDTO = new BookDTO();
    updateBookDTO.setText(newText);
    updateBookDTO.setDescription(newDescription);

    final String bodyValue = objectMapper.writeValueAsString(updateBookDTO);

    final MvcResult result = mvc
      .perform(
        put(fullURL + "/" + initialBookDTO.getIndex())
          .cookie(cookie)
          .content(bodyValue)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andReturn();

    final BookDTO updatedBookDTOResult = objectMapper.readValue(
      result.getResponse().getContentAsString(),
      BookDTO.class
    );

    assertEquals(initialBookDTO.getId(), updatedBookDTOResult.getId());
    assertEquals(newText, updatedBookDTOResult.getText());
    assertEquals(newDescription, updatedBookDTOResult.getDescription());
    assertEquals(initialBookDTO.getIndex(), updatedBookDTOResult.getIndex());
  }

  @Test
  void updateBookFromDungeon_DoesNotExist_ThrowsNotFound() throws Exception {
    final BookDTO bookDTO = new BookDTO();
    bookDTO.setText("Hey ho");
    final String bodyValue = objectMapper.writeValueAsString(bookDTO);
    mvc
      .perform(
        put(fullDungeonURL + "/" + Integer.MAX_VALUE)
          .cookie(cookie)
          .content(bodyValue)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  void updateBookFromDungeon() throws Exception {
    final String newText = "New text incoming";
    final String newDescription = "Book with new text";
    final BookDTO updateBookDTO = new BookDTO();
    updateBookDTO.setText(newText);
    updateBookDTO.setDescription(newDescription);

    final String bodyValue = objectMapper.writeValueAsString(updateBookDTO);

    final MvcResult result = mvc
      .perform(
        put(fullDungeonURL + "/" + initialDungeonBookDTO.getIndex())
          .cookie(cookie)
          .content(bodyValue)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andReturn();

    final BookDTO updatedBookDTOResult = objectMapper.readValue(
      result.getResponse().getContentAsString(),
      BookDTO.class
    );

    assertEquals(initialDungeonBookDTO.getId(), updatedBookDTOResult.getId());
    assertEquals(newText, updatedBookDTOResult.getText());
    assertEquals(newDescription, updatedBookDTOResult.getDescription());
    assertEquals(initialDungeonBookDTO.getIndex(), updatedBookDTOResult.getIndex());
  }
}
