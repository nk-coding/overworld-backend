package de.unistuttgart.overworldbackend.repositories;

import java.util.UUID;
import de.unistuttgart.overworldbackend.data.AreaMap;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaMapRepository extends  JpaRepository<AreaMap, UUID>{
    Optional<AreaMap> findByCourseIdAndAreaId(int courseId, UUID areaID);
}
