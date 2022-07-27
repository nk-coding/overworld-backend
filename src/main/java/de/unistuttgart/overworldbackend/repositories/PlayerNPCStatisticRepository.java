package de.unistuttgart.overworldbackend.repositories;

import de.unistuttgart.overworldbackend.data.PlayerNPCStatistic;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerNPCStatisticRepository extends JpaRepository<PlayerNPCStatistic, UUID> {
  List<PlayerNPCStatistic> findByLectureId(int lectureId);
  Optional<PlayerNPCStatistic> findByNpcIdAndLectureIdAndPlayerStatisticId(
    UUID npcId,
    int lectureId,
    UUID playerStatisticId
  );
}
