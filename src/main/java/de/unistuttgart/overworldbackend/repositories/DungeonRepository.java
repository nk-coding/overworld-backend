package de.unistuttgart.overworldbackend.repositories;

import de.unistuttgart.overworldbackend.data.Dungeon;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DungeonRepository extends JpaRepository<Dungeon, UUID> {
    Set<Dungeon> findAllByIndexAndCourseId(int index, int courseId);

    Set<Dungeon> findAllByCourseId(int courseId);
}
