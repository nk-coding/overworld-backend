package de.unistuttgart.overworldbackend.data;

import de.unistuttgart.overworldbackend.data.enums.Binding;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import reactor.util.annotation.Nullable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KeybindingDTO {

    @Nullable
    UUID id;

    Binding binding;

    String key;
}
