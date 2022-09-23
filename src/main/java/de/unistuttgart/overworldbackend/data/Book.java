package de.unistuttgart.overworldbackend.data;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.UUID;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.Nullable;

/**
 * A Book has a (large) text.
 *
 * A Book is located in an area and a player can read it.
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "index", "area_id", "course_id" }) })
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Book {

  @Id
  @GeneratedValue(generator = "uuid")
  UUID id;

  int index;

  String text;

  @Nullable
  String description;

  @ManyToOne
  Course course;

  @ManyToOne
  Area area;

  public Book(final String text, final int index) {
    this.text = text;
    this.index = index;
  }

  public Book(final String text, String description, final int index) {
    this.text = text;
    this.index = index;
    this.description = description;
  }
}
