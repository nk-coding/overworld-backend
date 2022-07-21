package de.unistuttgart.overworldbackend.repositories;

import de.unistuttgart.overworldbackend.data.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OverworldRepository extends JpaRepository<Configuration, Long> {
  Configuration findByStaticWorldId(String staticWorldId);
}
