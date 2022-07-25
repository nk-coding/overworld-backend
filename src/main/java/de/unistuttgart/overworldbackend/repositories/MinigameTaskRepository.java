package de.unistuttgart.overworldbackend.repositories;

import de.unistuttgart.overworldbackend.data.MinigameTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MinigameTaskRepository extends JpaRepository<MinigameTask, UUID> {}
