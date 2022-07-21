package de.unistuttgart.overworldbackend.repositories;

import de.unistuttgart.overworldbackend.data.Area;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AreaBaseRepository<T extends Area> extends JpaRepository<T, UUID> {}
