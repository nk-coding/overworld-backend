package de.unistuttgart.overworldbackend.data.mapper;

import de.unistuttgart.overworldbackend.data.Area;
import de.unistuttgart.overworldbackend.data.AreaLocationDTO;
import de.unistuttgart.overworldbackend.data.Dungeon;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AreaLocationMapper {
  default AreaLocationDTO areaToAreaLocationDTO(final Area area) {
    return area instanceof Dungeon dungeon
      ? new AreaLocationDTO(dungeon.getWorld().getIndex(), dungeon.getIndex())
      : new AreaLocationDTO(area.getIndex(), null);
  }

  default List<AreaLocationDTO> areaToAreaLocationDTOs(final List<Area> areaLocations) {
    return areaLocations.parallelStream().map(this::areaToAreaLocationDTO).toList();
  }
}
