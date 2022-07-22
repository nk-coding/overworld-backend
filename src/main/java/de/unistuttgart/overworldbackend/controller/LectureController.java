package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.LectureMapper;
import de.unistuttgart.overworldbackend.repositories.LectureRepository;
import de.unistuttgart.overworldbackend.service.LectureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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
    return lectureMapper.lecturesToLectureDTOs(lectureRepository.findAll());
  }

  @Operation(summary = "Get a lecture by its id")
  @GetMapping("/{id}")
  public LectureDTO getLecture(@PathVariable int id) {
    log.debug("get lecture {}", id);
    return lectureMapper.lectureToLectureDTO(lectureService.getLecture(id));
  }

  @Operation(summary = "Create a lecture")
  @PostMapping("")
  public LectureDTO createLecture(@RequestBody LectureInitialData lecture) {
    log.debug("create lecture {}");
    return lectureService.createLecture(lecture);
  }

  @Operation(summary = "Update a lecture by its id")
  @PutMapping("/{id}")
  public LectureDTO updateLecture(@PathVariable int id, @RequestBody LectureDTO lectureDTO) {
    log.debug("update lecture {}", id);
    return lectureService.updateLecture(lectureDTO);
  }

  @Operation(summary = "Delete a lecture by its id")
  @DeleteMapping("/{id}")
  public LectureDTO deleteLecture(@PathVariable int id) {
    log.debug("delete lecture {}", id);
    return lectureService.deleteLecture(id);
  }
}
