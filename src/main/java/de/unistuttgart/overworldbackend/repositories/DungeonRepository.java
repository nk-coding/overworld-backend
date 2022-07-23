package de.unistuttgart.overworldbackend.repositories;

import de.unistuttgart.overworldbackend.data.Dungeon;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DungeonRepository extends JpaRepository<Dungeon, UUID> {
  Optional<Dungeon> findByIdAndLectureId(UUID id, int lectureId);
  Optional<Dungeon> findByIdAndLectureIdAndWorldId(UUID id, int lectureId, UUID worldId);

  Set<Dungeon> findAllByLectureId(int lectureId);
  Set<Dungeon> findAllByLectureIdAndWorldId(int lectureId, UUID worldId);
}
