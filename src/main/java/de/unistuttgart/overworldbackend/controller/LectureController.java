package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.overworldbackend.data.Lecture;
import de.unistuttgart.overworldbackend.data.LectureDTO;
import de.unistuttgart.overworldbackend.data.World;
import de.unistuttgart.overworldbackend.data.mapper.LectureMapper;
import de.unistuttgart.overworldbackend.repositories.LectureRepository;
import de.unistuttgart.overworldbackend.service.LectureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Lecture", description = "Modify lectures")
@RestController
@Slf4j
@RequestMapping("/lectures")
public class LectureController {

  @Autowired
  private LectureRepository lectureRepository;

  @Autowired
  private LectureService lectureService;

  @Autowired
  private LectureMapper lectureMapper;

  @Operation(summary = "Get all lectures")
  @GetMapping("")
  public List<LectureDTO> getLectures() {
    log.debug("get lectures");
    World world = new World("static name", "other name", true, Set.of(), Set.of(), Set.of());
    Lecture lecture = new Lecture("name", "description", Set.of(world));
    lectureRepository.save(lecture);
    return lectureMapper.lecturesToLectureDTOs(lectureRepository.findAll());
  }

  @Operation(summary = "Get a lecture by its id")
  @GetMapping("/{id}")
  public LectureDTO getLecture(@PathVariable int id) {
    log.debug("get lecture {}", id);
    return lectureService.getLecture(id);
  }

  @Operation(summary = "Create a lecture")
  @PostMapping("")
  public void createLecture() {
    log.debug("create lecture {}");
  }

  @Operation(summary = "Update a lecture by its id")
  @PutMapping("/{id}")
  public void updateLecture(@PathVariable int id) {
    log.debug("update lecture {}", id);
  }

  @Operation(summary = "Delete a lecture by its id")
  @DeleteMapping("/{id}")
  public void deleteLecture(@PathVariable int id) {
    log.debug("delete lecture {}", id);
  }
}
