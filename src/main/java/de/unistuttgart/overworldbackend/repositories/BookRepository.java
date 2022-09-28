package de.unistuttgart.overworldbackend.repositories;

import de.unistuttgart.overworldbackend.data.Book;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
    Optional<Book> findByIndexAndCourseIdAndAreaId(int bookIndex, int courseId, UUID areaId);
}
