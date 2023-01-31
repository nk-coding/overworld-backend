package de.unistuttgart.overworldbackend.data;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Teleporter {

    @Id
    @GeneratedValue(generator = "uuid")
    UUID id;

    int index;

    @ManyToOne
    Area area;

    @ManyToOne
    Course course;

    public Teleporter(final int index, final Area area, final Course course) {
        this.index = index;
        this.area = area;
        this.course = course;
    }
}
