package de.unistuttgart.overworldbackend.data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Store the progress of a player with its player statistic of an interaction with a npc.
 *
 * @see PlayerStatistic
 * @see NPC
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "player_statistic_id", "npc_id", "course_id" }) })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerNPCStatistic {

    @Id
    @GeneratedValue(generator = "uuid")
    UUID id;

    @ManyToOne
    PlayerStatistic playerStatistic;

    @ManyToOne
    NPC npc;

    @ManyToOne
    Course course;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    Set<PlayerNPCActionLog> playerNPCActionLogs = new HashSet<>();

    boolean completed;

    public void addActionLog(final PlayerNPCActionLog actionLog) {
        this.playerNPCActionLogs.add(actionLog);
    }
}
