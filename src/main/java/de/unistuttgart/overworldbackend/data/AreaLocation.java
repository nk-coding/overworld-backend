package de.unistuttgart.overworldbackend.data;

import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "world_id", "dungeon_id" }) })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AreaLocation {

  @Id
  @GeneratedValue(generator = "uuid")
  UUID id;

  @NotNull
  @ManyToOne
  World world;

  @ManyToOne
  Dungeon dungeon;

  public AreaLocation(World world, Dungeon dungeon) {
    this.world = world;
    this.dungeon = dungeon;
  }
}
