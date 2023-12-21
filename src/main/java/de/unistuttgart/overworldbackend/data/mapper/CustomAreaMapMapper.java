package de.unistuttgart.overworldbackend.data.mapper;

import de.unistuttgart.overworldbackend.data.CustomAreaMap;
import de.unistuttgart.overworldbackend.data.CustomAreaMapDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { AreaLocationMapper.class })
public interface CustomAreaMapMapper {
    CustomAreaMapDTO customAreaMapToCustomAreaMapDTO(final CustomAreaMap customAreaMap);

    CustomAreaMap customAreaMapDTOToCustomAreaMap(final CustomAreaMapDTO customAreaMapDTO);
}
