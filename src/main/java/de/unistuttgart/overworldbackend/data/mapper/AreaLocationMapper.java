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

  public AreaLocationDTO areaLocationToAreaLocationDTO(AreaLocation areaLocation) {
    if (areaLocation.getDungeon() == null) {
      return new AreaLocationDTO(areaLocation.getWorld().getIndex(), null);
    }
    return new AreaLocationDTO(areaLocation.getWorld().getIndex(), areaLocation.getDungeon().getIndex());
  }

  public List<AreaLocationDTO> areaLocationsToAreaLocationDTOs(List<AreaLocation> areaLocations) {
    List<AreaLocationDTO> list = new ArrayList<>();
    for (AreaLocation areaLocation : areaLocations) {
      list.add(areaLocationToAreaLocationDTO(areaLocation));
    }
    return list;
  }
}
