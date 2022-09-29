package de.unistuttgart.overworldbackend.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Objects;
import java.util.Set;

/**
 * A dungeon is an instance of an Area.
 * <p>
 * A dungeon can be accessed through a world and contains multiple minigame tasks and NPCs to interact with.
 * @see Area
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Dungeon extends Area {

    @ManyToOne
    World world;

    public Dungeon(
        final String staticName,
        final String topicName,
        final boolean active,
        final Set<MinigameTask> minigameTasks,
        final Set<NPC> npcs,
        final Set<Book> books,
        final int index
    ) {
        super(staticName, topicName, active, minigameTasks, npcs, books, index);
    }

    public Dungeon(
            final String staticName,
            final String topicName,
            final boolean active,
            final boolean configured,
            final Set<MinigameTask> minigameTasks,
            final Set<NPC> npcs,
            final Set<Book> books,
            final int index
    ) {
        super(staticName, topicName, active, configured, minigameTasks, npcs, books, index);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Dungeon dungeon = (Dungeon) o;
        return Objects.equals(world, dungeon.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}
