package de.unistuttgart.overworldbackend.data;

import de.unistuttgart.overworldbackend.data.enums.AreaStyle;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.UUID;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a generated area.
 * It contains the layout and all objects.
 */
@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomAreaMap {

    @Id
    @GeneratedValue(generator = "uuid")
    UUID id;

    @ManyToOne(cascade = CascadeType.ALL)
    Layout layout;

    @OneToMany(cascade = CascadeType.ALL)
    Set<MinigameSpot> minigameSpots = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    Set<NPCSpot> npcSpots = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    Set<BookSpot> bookSpots = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    Set<BarrierSpot> barrierSpots = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    Set<TeleporterSpot> teleporterSpots = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    Set<SceneTransitionSpot> sceneTransitionSpots = new HashSet<>();
}
