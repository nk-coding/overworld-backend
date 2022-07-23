package de.unistuttgart.overworldbackend.repositories;

import de.unistuttgart.overworldbackend.data.PlayerTaskActionLog;
import de.unistuttgart.overworldbackend.data.Playerstatistic;
import de.unistuttgart.overworldbackend.data.World;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerTaskActionLogRepository extends JpaRepository<PlayerTaskActionLog, UUID> {}
