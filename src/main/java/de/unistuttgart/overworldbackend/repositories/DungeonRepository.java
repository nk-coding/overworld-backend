package de.unistuttgart.overworldbackend.repositories;

import de.unistuttgart.overworldbackend.data.Dungeon;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DungeonRepository extends JpaRepository<Dungeon, UUID> {
  Set<Dungeon> findAllByIndexAndLectureId(int index, int lectureId);

  Set<Dungeon> findAllByLectureId(int lectureId);
}
