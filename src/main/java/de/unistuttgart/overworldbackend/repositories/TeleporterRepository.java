package de.unistuttgart.overworldbackend.repositories;

import de.unistuttgart.overworldbackend.data.NPC;
import de.unistuttgart.overworldbackend.data.Teleporter;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeleporterRepository extends JpaRepository<Teleporter, UUID> {
    Optional<Teleporter> findByIndexAndCourseIdAndAreaId(int teleporterIndex, int courseId, UUID areaId);
}
