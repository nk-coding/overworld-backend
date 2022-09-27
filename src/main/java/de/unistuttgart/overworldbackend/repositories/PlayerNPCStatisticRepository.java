package de.unistuttgart.overworldbackend.repositories;

import de.unistuttgart.overworldbackend.data.PlayerNPCStatistic;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerNPCStatisticRepository extends JpaRepository<PlayerNPCStatistic, UUID> {
    List<PlayerNPCStatistic> findByCourseId(int courseId);
    List<PlayerNPCStatistic> findByNpcId(UUID npcId);
    Optional<PlayerNPCStatistic> findByNpcIdAndCourseIdAndPlayerStatisticId(
        UUID npcId,
        int courseId,
        UUID playerStatisticId
    );
}
