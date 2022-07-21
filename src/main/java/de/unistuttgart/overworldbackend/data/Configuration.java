package de.unistuttgart.overworldbackend.data;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "configurations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Configuration {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  private long id;

  @Column(nullable = false, unique = true)
  private String staticWorldId;

  @Column(nullable = false, unique = true)
  private String configurationString;

  @Column(nullable = false)
  private String minigameType;
}
