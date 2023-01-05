package de.unistuttgart.overworldbackend.repositories;

import de.unistuttgart.overworldbackend.data.Achievement;
import de.unistuttgart.overworldbackend.data.Course;
import de.unistuttgart.overworldbackend.data.enums.AchievementTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, AchievementTitle> {}
