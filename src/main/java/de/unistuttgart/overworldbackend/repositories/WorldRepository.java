package de.unistuttgart.overworldbackend.repositories;

import de.unistuttgart.overworldbackend.data.World;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorldRepository extends JpaRepository<World, UUID> {
  Optional<World> findByIdAndLectureId(UUID id, int lecture_id);

  Set<World> findAllByLectureId(int lecture_id);
}
