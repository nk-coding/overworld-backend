package de.unistuttgart.overworldbackend.repositories;

import de.unistuttgart.overworldbackend.data.Playerstatistic;
import de.unistuttgart.overworldbackend.data.World;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerstatisticRepository extends JpaRepository<Playerstatistic, UUID> {
  Optional<Playerstatistic> findByLectureIdAndUserId(int lectureId, String userId);
}
