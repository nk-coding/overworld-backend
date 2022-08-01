package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.overworldbackend.data.CourseDTO;
import de.unistuttgart.overworldbackend.data.CourseInitialData;
import de.unistuttgart.overworldbackend.data.mapper.CourseMapper;
import de.unistuttgart.overworldbackend.repositories.CourseRepository;
import de.unistuttgart.overworldbackend.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Course", description = "Modify course")
@RestController
@Slf4j
@RequestMapping("/courses")
public class CourseController {

  @Autowired
  private CourseRepository courseRepository;

  @Autowired
  private CourseService courseService;

  @Autowired
  private CourseMapper courseMapper;

  @Operation(summary = "Get all courses")
  @GetMapping("")
  public List<CourseDTO> getCourses() {
    log.debug("get courses");
    return courseMapper.coursesToCourseDTOs(courseRepository.findAll());
  }

  @Operation(summary = "Get a course by its id")
  @GetMapping("/{id}")
  public CourseDTO getCourse(@PathVariable int id) {
    log.debug("get course {}", id);
    return courseMapper.courseToCourseDTO(courseService.getCourse(id));
  }

  @Operation(summary = "Create a course")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("")
  public CourseDTO createCourse(@Valid @RequestBody CourseInitialData course) {
    log.debug("create course {}");
    return courseService.createCourse(course);
  }

  @Operation(summary = "Update a course by its id")
  @PutMapping("/{id}")
  public CourseDTO updateCourse(@PathVariable int id, @Valid @RequestBody CourseDTO courseDTO) {
    log.debug("update course {} with {}", id, courseDTO);
    return courseService.updateCourse(id, courseDTO);
  }

  @Operation(summary = "Delete a course by its id")
  @DeleteMapping("/{id}")
  public CourseDTO deleteCourse(@PathVariable int id) {
    log.debug("delete course {}", id);
    return courseService.deleteCourse(id);
  }
}
