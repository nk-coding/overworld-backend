package de.unistuttgart.overworldbackend.repositories;

import de.unistuttgart.overworldbackend.data.Lecture;
import de.unistuttgart.overworldbackend.data.PlayerTaskStatistic;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerTaskStatisticRepository extends JpaRepository<PlayerTaskStatistic, UUID> {
  List<PlayerTaskStatistic> findPlayerTaskStatisticByLectureId(int lectureID);
  Optional<PlayerTaskStatistic> findPlayerTaskStatisticByMinigameTaskIdAndLectureIdAndPlayerstatisticId(
    UUID minigameTaskId,
    int lectureId,
    UUID playerstatisticId
  );
}
