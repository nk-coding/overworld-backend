package de.unistuttgart.overworldbackend.repositories;

import de.unistuttgart.overworldbackend.data.World;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorldRepository extends JpaRepository<World, UUID> {
  Optional<World> findByIdAndLectureId(UUID id, int lectureId);

  Optional<World> findByStaticNameAndLectureId(String staticName, int lectureId);
  Optional<World> findByIndexAndLectureId(int index, int lectureId);

  Set<World> findAllByLectureId(int lectureId);
}
