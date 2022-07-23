package de.unistuttgart.overworldbackend.repositories;

import de.unistuttgart.overworldbackend.data.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Integer> {}
