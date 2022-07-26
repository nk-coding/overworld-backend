package de.unistuttgart.overworldbackend.data.mapper;

import de.unistuttgart.overworldbackend.data.AreaLocation;
import de.unistuttgart.overworldbackend.data.AreaLocationDTO;
import de.unistuttgart.overworldbackend.repositories.PlayerstatisticRepository;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class AreaLocationMapper {

  public AreaLocationDTO areaLocationToAreaLocationDTO(final AreaLocation areaLocation) {
    if (areaLocation.getDungeon() == null) {
      return new AreaLocationDTO(areaLocation.getWorld().getIndex(), null);
    }
    return new AreaLocationDTO(areaLocation.getWorld().getIndex(), areaLocation.getDungeon().getIndex());
  }

  public List<AreaLocationDTO> areaLocationsToAreaLocationDTOs(final List<AreaLocation> areaLocations) {
    final List<AreaLocationDTO> list = new ArrayList<>();
    for (final AreaLocation areaLocation : areaLocations) {
      list.add(areaLocationToAreaLocationDTO(areaLocation));
    }
    return list;
  }
}
