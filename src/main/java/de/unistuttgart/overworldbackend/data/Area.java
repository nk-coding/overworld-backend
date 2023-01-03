package de.unistuttgart.overworldbackend.data;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Represents an area of the overworld map.
 * <p>
 * An area is either a world or a dungeon, and contains multiple minigame tasks and NPCs to interact with.
 */
@Entity
@Inheritance
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Area {

    @Id
    @GeneratedValue(generator = "uuid")
    UUID id;

    int index;

    /**
     * A name given by the Gamify-IT development team to be displayed in the overworld.
     */
    @NotNull
    String staticName;

    /**
     * A name given by the lecturer to describe the main content in the area such as {@code UML diagrams}.
     */
    String topicName;

    /**
     * Whether the area should be shown to students.
     *
     * @see #configured
     */
    boolean active;

    /**
     * Whether the lecturer has configured the area already.
     * <p>
     * The relation between {@link #active} and {@code configured} is {@code active implies configured}.<br>
     * The case {@code active && !configured} is illegal.
     */
    boolean configured;

    @JsonManagedReference(value = "area-minigames")
    @OneToMany(cascade = CascadeType.ALL)
    Set<MinigameTask> minigameTasks = new HashSet<>();

    @JsonManagedReference(value = "area-npcs")
    @OneToMany(cascade = CascadeType.ALL)
    Set<NPC> npcs = new HashSet<>();

    @JsonManagedReference(value = "area-books")
    @OneToMany(cascade = CascadeType.ALL)
    Set<Book> books = new HashSet<>();

    @JsonManagedReference(value = "area-teleporters")
    @OneToMany(cascade = CascadeType.ALL)
    Set<Teleporter> teleporters = new HashSet<>();

    @ManyToOne
    Course course;

    protected Area(
        final String staticName,
        final String topicName,
        final boolean active,
        final Set<MinigameTask> minigameTasks,
        final Set<NPC> npcs,
        final Set<Book> books,
        final Set<Teleporter> teleporters,
        final int index
    ) {
        this.staticName = staticName;
        this.topicName = topicName;
        this.active = active;
        this.minigameTasks = minigameTasks;
        this.npcs = npcs;
        this.books = books;
        this.teleporters = teleporters;
        this.index = index;
    }

    protected Area(
        final String staticName,
        final String topicName,
        final boolean active,
        final boolean configured,
        final Set<MinigameTask> minigameTasks,
        final Set<NPC> npcs,
        final Set<Book> books,
        final Set<Teleporter> teleporters,
        final int index
    ) {
        this.staticName = staticName;
        this.topicName = topicName;
        this.active = active;
        this.configured = configured;
        this.minigameTasks = minigameTasks;
        this.npcs = npcs;
        this.books = books;
        this.teleporters = teleporters;
        this.index = index;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Area area = (Area) o;
        return id.equals(area.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
