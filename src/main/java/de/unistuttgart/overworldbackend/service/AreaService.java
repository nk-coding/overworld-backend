package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.Area;
import de.unistuttgart.overworldbackend.repositories.AreaBaseRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AreaService {

  @Autowired
  private AreaBaseRepository<Area> areaBaseRepository;

  /**
   * Get an area of a lecture
   *
   * @throws ResponseStatusException (404) if area with its id could not be found in the lecture
   * @param lectureId the id of the lecture the area should be part of
   * @param areaId the id of the area searching for
   * @return the found area object
   */
  public Area getAreaFromLectureOrThrowNotFound(final int lectureId, final UUID areaId) {
    return areaBaseRepository
      .findByIdAndLectureId(areaId, lectureId)
      .orElseThrow(() ->
        new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          String.format("There is no area with id %s in lecture with id %s.", areaId, lectureId)
        )
      );
  }
}
