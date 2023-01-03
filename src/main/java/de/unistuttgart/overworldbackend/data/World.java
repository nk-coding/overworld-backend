package de.unistuttgart.overworldbackend.data;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * A world is an instance of an Area.
 * <p>
 * In addition to the functionality offered by dungeons, a world can also hold several dungeons as "sub-levels".
 * @see Dungeon
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class World extends Area {

    @OneToMany(cascade = CascadeType.ALL)
    List<Dungeon> dungeons;

    public World(
        final String staticName,
        final String topicName,
        final boolean active,
        final Set<MinigameTask> minigameTasks,
        final Set<NPC> npcs,
        final Set<Book> books,
        final Set<Teleporter> teleporters,
        final List<Dungeon> dungeons,
        final int index
    ) {
        super(staticName, topicName, active, minigameTasks, npcs, books, teleporters, index);
        this.dungeons = dungeons;
    }

    public World(
        final String staticName,
        final String topicName,
        final boolean active,
        final boolean configured,
        final Set<MinigameTask> minigameTasks,
        final Set<NPC> npcs,
        final Set<Book> books,
        final Set<Teleporter> teleporters,
        final List<Dungeon> dungeons,
        final int index
    ) {
        super(staticName, topicName, active, configured, minigameTasks, npcs, books, teleporters, index);
        this.dungeons = dungeons;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        World world = (World) o;
        return Objects.equals(dungeons, world.dungeons);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}
