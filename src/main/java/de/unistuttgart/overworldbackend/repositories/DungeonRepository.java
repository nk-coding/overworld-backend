package de.unistuttgart.overworldbackend.repositories;

import de.unistuttgart.overworldbackend.data.Dungeon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface DungeonRepository extends JpaRepository<Dungeon, UUID> {
  Optional<Dungeon> findByIndexAndLectureId(int index, int lectureId);

  Set<Dungeon> findAllByLectureId(int lectureId);
}
