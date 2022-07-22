package de.unistuttgart.overworldbackend.data.mapper;

import de.unistuttgart.overworldbackend.data.Lecture;
import de.unistuttgart.overworldbackend.data.LectureDTO;
import java.util.List;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LectureMapper {
  LectureDTO lectureToLectureDTO(final Lecture lecture);

  Lecture lectureDTOToLecture(final LectureDTO lectureDTO);

  List<LectureDTO> lecturesToLectureDTOs(final List<Lecture> lectures);
}
