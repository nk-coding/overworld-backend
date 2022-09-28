package de.unistuttgart.overworldbackend.data.mapper;

import de.unistuttgart.overworldbackend.data.Course;
import de.unistuttgart.overworldbackend.data.CourseDTO;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { AreaLocationMapper.class })
public interface CourseMapper {
    CourseDTO courseToCourseDTO(final Course course);

    Course courseDTOToCourse(final CourseDTO courseDTO);

    List<CourseDTO> coursesToCourseDTOs(final List<Course> courses);
}
