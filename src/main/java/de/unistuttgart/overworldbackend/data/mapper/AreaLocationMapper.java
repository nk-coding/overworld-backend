package de.unistuttgart.overworldbackend.data.mapper;

import de.unistuttgart.overworldbackend.data.AreaLocation;
import de.unistuttgart.overworldbackend.data.AreaLocationDTO;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class AreaLocationMapper {

  public AreaLocationDTO areaLocationToAreaLocationDTO(final AreaLocation areaLocation) {
    return new AreaLocationDTO(
      areaLocation.getWorld().getIndex(),
      areaLocation.getDungeon() != null ? areaLocation.getDungeon().getIndex() : null
    );
  }

  public List<AreaLocationDTO> areaLocationsToAreaLocationDTOs(final List<AreaLocation> areaLocations) {
    return areaLocations.parallelStream().map(this::areaLocationToAreaLocationDTO).toList();
  }
}
