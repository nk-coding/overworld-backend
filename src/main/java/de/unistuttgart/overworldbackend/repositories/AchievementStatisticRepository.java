package de.unistuttgart.overworldbackend.repositories;

import de.unistuttgart.overworldbackend.data.Achievement;
import de.unistuttgart.overworldbackend.data.AchievementStatistic;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AchievementStatisticRepository extends JpaRepository<AchievementStatistic, UUID> {
    List<AchievementStatistic> findAllByPlayerUserId(String playerId);

    Optional<AchievementStatistic> findByPlayerUserIdAndAchievement(
        final String playerId,
        final Achievement achievement
    );
}
