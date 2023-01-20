package de.unistuttgart.overworldbackend.data;

import de.unistuttgart.overworldbackend.data.enums.Keybinding;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import reactor.util.annotation.Nullable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KeybindingStatisticDTO {

    @Nullable
    UUID id;

    Keybinding keybinding;

    String key;
}
