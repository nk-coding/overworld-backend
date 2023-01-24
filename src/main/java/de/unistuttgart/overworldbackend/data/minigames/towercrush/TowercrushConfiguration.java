package de.unistuttgart.overworldbackend.data.minigames.towercrush;

import java.util.Set;
import java.util.UUID;
import lombok.Data;

@Data
public class TowercrushConfiguration {

    UUID id;

    Set<TowercrushQuestion> questions;
}
