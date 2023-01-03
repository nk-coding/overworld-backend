package de.unistuttgart.overworldbackend.data;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.UUID;
import javax.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "index", "area_id", "course_id" }) })
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Teleporter {

    @Id
    @GeneratedValue(generator = "uuid")
    UUID id;

    String name;
    int index;

    @OneToOne(cascade = CascadeType.ALL)
    Position position;

    @JsonBackReference(value = "course-teleporters")
    @ManyToOne
    Course course;

    @JsonBackReference(value = "area-teleporters")
    @ManyToOne
    Area area;

    public Teleporter(final String name, final Position position, final int index) {
        this.name = name;
        this.position = position;
        this.index = index;
    }
}
