package de.unistuttgart.overworldbackend.data;

import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.Nullable;

/**
 * Data Transfer Object for NPC.
 *
 * @see NPC
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NPCDTO {

    @Nullable
    UUID id;

    AreaLocationDTO area;
    int index;
    List<String> text;

    @Nullable
    String description;
}
