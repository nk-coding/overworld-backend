package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.Lecture;
import de.unistuttgart.overworldbackend.data.LectureDTO;
import de.unistuttgart.overworldbackend.data.mapper.LectureMapper;
import de.unistuttgart.overworldbackend.repositories.LectureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LectureService {

  @Autowired
  private LectureRepository lectureRepository;

  @Autowired
  private LectureMapper lectureMapper;

  /**
   * @throws ResponseStatusException when lecture by its id could not be found
   * @param id the id of the lecture searching for
   * @return the found lecture
   */
  public LectureDTO getLecture(final int id) {
    return lectureMapper.lectureToLectureDTO(
      lectureRepository
        .findById(id)
        .orElseThrow(() ->
          new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("There is no lecture with id %s.", id))
        )
    );
  }
}
