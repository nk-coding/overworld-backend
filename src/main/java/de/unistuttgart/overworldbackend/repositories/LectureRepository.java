package de.unistuttgart.overworldbackend.repositories;

import de.unistuttgart.overworldbackend.data.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<Lecture, Integer> {}
