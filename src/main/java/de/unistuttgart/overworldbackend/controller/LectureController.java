package de.unistuttgart.overworldbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Lecture", description = "Modify lectures")
@RestController
@Slf4j
@RequestMapping("/lectures")
public class LectureController {

  @Operation(summary = "Get all lectures")
  @GetMapping("")
  public void getLectures() {
    log.debug("get lectures");
  }

  @Operation(summary = "Get a lecture by its id")
  @GetMapping("/{id}")
  public void getLecture(@PathVariable int id) {
    log.debug("get lecture {}", id);
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
