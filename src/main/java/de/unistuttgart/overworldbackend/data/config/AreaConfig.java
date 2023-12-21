package de.unistuttgart.overworldbackend.data.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AreaConfig {
    int amountMinigames;
    int amountNPCs;
    int amountBooks;
    int amountDungeons;
}
