package de.unistuttgart.overworldbackend.repositories;

import de.unistuttgart.overworldbackend.data.World;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface WorldRepository extends JpaRepository<World, UUID> {
  Optional<World> findByIndexAndLectureId(int index, int lectureId);

  Set<World> findAllByLectureId(int lectureId);
}
