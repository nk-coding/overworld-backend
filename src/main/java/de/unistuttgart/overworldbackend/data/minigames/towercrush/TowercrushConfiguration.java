package de.unistuttgart.overworldbackend.data.minigames.towercrush;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class TowercrushConfiguration {

    UUID id;

    Set<TowercrushQuestion> questions;
}
