package de.unistuttgart.overworldbackend.data;

import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Data Transfer Object to communicate a change of a npc interaction from a player.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerNPCStatisticData {

    @NotNull
    UUID npcId;

    @NotNull
    boolean completed;

    @NotNull
    String userId;
}
