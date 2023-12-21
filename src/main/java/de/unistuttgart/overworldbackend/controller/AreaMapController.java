package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.data.AreaMapDTO;
import de.unistuttgart.overworldbackend.data.mapper.AreaMapMapper;
import de.unistuttgart.overworldbackend.service.AreaMapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static de.unistuttgart.overworldbackend.data.Roles.LECTURER_ROLE;

@Tag(name = "AreaMap", description = "Get and update area maps from a course")
@RestController
@Slf4j
@RequestMapping("/courses/{courseId}/areaMaps")
public class AreaMapController {
    @Autowired
    JWTValidatorService jwtValidatorService;

    @Autowired
    private AreaMapMapper areaMapMapper;

    @Autowired
    private AreaMapService areaMapService;

    @Operation(summary = "Get all area maps from a course by its id")
    @GetMapping("")
    public Set<AreaMapDTO> getAreaMaps(
            @PathVariable final int courseId,
            @CookieValue("access_token") final String accessToken
    ){
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("get area maps of course {}", courseId);
        return areaMapMapper.areaMapsToAreaMapDTOs(areaMapService.getAllAreaMapsFromCourse(courseId));
    }

    @Operation(summary = "Get area map of a world by its index from a course by its id")
    @GetMapping("/{worldIndex}")
    public AreaMapDTO getAreaMapFromWorld(
            @PathVariable final int courseId,
            @PathVariable final int worldIndex,
            @CookieValue("access_token") final String accessToken
    ){
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("get area map from world {} of course {}", worldIndex, courseId);
        return areaMapMapper.areaMapToAreaMapDTO(areaMapService.getAreaMapFromWorld(courseId, worldIndex));
    }

    @Operation(summary = "Get area map of a dungeon by its index from a course by its id")
    @GetMapping("/{worldIndex}/dungeon/{dungeonIndex}")
    public AreaMapDTO getAreaMapFromDungeon(
            @PathVariable final int courseId,
            @PathVariable final int worldIndex,
            @PathVariable final int dungeonIndex,
            @CookieValue("access_token") final String accessToken
    ){
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("get area map from dungeon {} of world {} of course {}", dungeonIndex, worldIndex, courseId);
        return areaMapMapper.areaMapToAreaMapDTO(areaMapService.getAreaMapFromDungeon(courseId, worldIndex, dungeonIndex));
    }

    @Operation(summary = "Update a area map from a world by its index from a course")
    @PutMapping("/{worldIndex}")
    public AreaMapDTO updateAreaMapFromWorld(
            @PathVariable final int courseId,
            @PathVariable final int worldIndex,
            @RequestBody final AreaMapDTO areaMapDTO,
            @CookieValue("access_token") final String accessToken
    ){
        jwtValidatorService.validateTokenOrThrow(accessToken);
        jwtValidatorService.hasRolesOrThrow(accessToken, LECTURER_ROLE);
        log.debug("update area map from world {} of course {} with {}", worldIndex, courseId, areaMapDTO);
        return areaMapService.updateAreaMapOfWorld(courseId, worldIndex, areaMapDTO);
    }

    @Operation(summary = "Update a area map from a dungeon by its index from a course")
    @PutMapping("/{worldIndex}/dungeon/{dungeonIndex}")
    public AreaMapDTO updateAreaMapFromDungeon(
            @PathVariable final int courseId,
            @PathVariable final int worldIndex,
            @PathVariable final int dungeonIndex,
            @RequestBody final AreaMapDTO areaMapDTO,
            @CookieValue("access_token") final String accessToken
    ){
        jwtValidatorService.validateTokenOrThrow(accessToken);
        jwtValidatorService.hasRolesOrThrow(accessToken, LECTURER_ROLE);
        log.debug("update area map from dungeon {} from world {} of course {} with {}", dungeonIndex, worldIndex, courseId, areaMapDTO);
        return areaMapService.updateAreaMapOfDungeon(courseId, worldIndex, dungeonIndex, areaMapDTO);
    }
}
