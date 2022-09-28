package de.unistuttgart.overworldbackend.data;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.Nullable;

/**
 * Data Transfer Object for Book.
 *
 * @see Book
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookDTO {

    @Nullable
    UUID id;

    AreaLocationDTO area;
    int index;
    String text;

    @Nullable
    String description;
}
