package de.unistuttgart.overworldbackend.repositories;

import de.unistuttgart.overworldbackend.data.Area;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaBaseRepository<T extends Area> extends JpaRepository<T, UUID> {
  Optional<Area> findByStaticNameAndCourseId(String staticName, int courseId);
}
