package de.unistuttgart.overworldbackend.data;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.unistuttgart.overworldbackend.data.enums.Keybinding;
import java.util.UUID;
import javax.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "player_user_id", "keybinding" }) })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KeybindingStatistic {

    @Id
    @GeneratedValue(generator = "uuid")
    UUID id;

    @JsonBackReference(value = "achievement-player")
    @ManyToOne
    Player player;

    @Enumerated
    Keybinding keybinding;

    String key;
}
