package de.unistuttgart.overworldbackend.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Playerstatistic {
    @Id
    @GeneratedValue(generator = "uuid")
    UUID id;

    @ManyToMany
    List<Area> unlockedAreas;

    @ManyToMany
    List<Dungeon> completedDungeons;

    @ManyToOne
    Area currentArea;

    @ManyToOne
    Lecture lecture;

    @NotNull
    String username;

    long knowledge;
}
