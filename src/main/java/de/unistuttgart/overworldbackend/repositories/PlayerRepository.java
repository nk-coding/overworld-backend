package de.unistuttgart.overworldbackend.repositories;

import de.unistuttgart.overworldbackend.data.Course;
import de.unistuttgart.overworldbackend.data.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {}
