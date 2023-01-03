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

    public Teleporter(final int index, final Area area) {
        this.index = index;
        this.area = area;
    }
}
