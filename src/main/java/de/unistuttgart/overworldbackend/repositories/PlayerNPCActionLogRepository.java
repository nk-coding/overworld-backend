package de.unistuttgart.overworldbackend.repositories;

import de.unistuttgart.overworldbackend.data.PlayerNPCActionLog;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerNPCActionLogRepository extends JpaRepository<PlayerNPCActionLog, UUID> {}
