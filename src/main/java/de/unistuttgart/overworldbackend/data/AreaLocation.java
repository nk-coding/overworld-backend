package de.unistuttgart.overworldbackend.data;

import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
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

  public AreaLocation(final World world) {
    this.world = world;
  }

  public AreaLocation(final World world, final Dungeon dungeon) {
    this.world = world;
    this.dungeon = dungeon;
  }
}
