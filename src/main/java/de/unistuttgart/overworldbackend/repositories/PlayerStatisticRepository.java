package de.unistuttgart.overworldbackend.repositories;

import de.unistuttgart.overworldbackend.data.PlayerStatistic;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerStatisticRepository extends JpaRepository<PlayerStatistic, UUID> {
    boolean existsByCourseIdAndUserId(int courseId, String userId);
    Optional<PlayerStatistic> findByCourseIdAndUserId(int courseId, String userId);
    Set<PlayerStatistic> deletePlayerStatisticsByCourseId(int courseId);
}
