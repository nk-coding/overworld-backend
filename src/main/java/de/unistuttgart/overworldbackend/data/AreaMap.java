package de.unistuttgart.overworldbackend.data;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lombok.experimental.FieldDefaults;

import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.UUID;

/**
 * Represents the state of an area map, whether it is a generated area map or a default one
 * It contains a flag and, if it is a generated area map, a custom area map
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "course_id", "area_id" }) })
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AreaMap {

    @Id
    @GeneratedValue(generator = "uuid")
    UUID id;

    @JsonBackReference(value = "course-area-maps")
    @ManyToOne
    Course course;

    @JsonBackReference(value = "area-map")
    @OneToOne
    Area area;

    boolean generatedArea;

    @Nullable
    @OneToOne(cascade = CascadeType.ALL)
    CustomAreaMap customAreaMap;

    public AreaMap(Area area){
        this.area = area;
        course = area.getCourse();
        generatedArea = false;
    }
}
