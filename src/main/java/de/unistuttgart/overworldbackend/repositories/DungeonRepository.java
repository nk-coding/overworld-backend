package de.unistuttgart.overworldbackend.repositories;

import de.unistuttgart.overworldbackend.data.Dungeon;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DungeonRepository extends JpaRepository<Dungeon, UUID> {
  Optional<Dungeon> findByIdAndLectureId(UUID id, int lecture_id);

  Set<Dungeon> findAllByLectureId(int lecture_id);
}
