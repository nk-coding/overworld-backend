package de.unistuttgart.overworldbackend.data;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.UUID;

/**
 * A Layout represents the size and structure of an area map
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Layout
{
    @Id
    @GeneratedValue(generator = "uuid")
    UUID id;

    @ManyToOne(cascade = CascadeType.ALL)
    AreaLocation area;

    int sizeX;
    int sizeY;
    String generatorType;
    String seed;
    int accessability;
    String style;
}
