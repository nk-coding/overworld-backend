package de.unistuttgart.overworldbackend.data.mapper;

import de.unistuttgart.overworldbackend.data.AreaMap;
import de.unistuttgart.overworldbackend.data.AreaMapDTO;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel =  "spring", uses = { AreaLocationMapper.class })
public interface AreaMapMapper {
    AreaMapDTO areaMapToAreaMapDTO(final AreaMap areaMap);

    AreaMap areaMapDTOToAreaMap(final AreaMapDTO areaMapDTO);

    Set<AreaMapDTO> areaMapsToAreaMapDTOs(final Set<AreaMap> areaMaps);
}
