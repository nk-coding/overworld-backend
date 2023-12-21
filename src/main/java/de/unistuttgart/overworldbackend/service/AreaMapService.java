package de.unistuttgart.overworldbackend.service;

import aj.org.objectweb.asm.TypeReference;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.config.AreaConfig;
import de.unistuttgart.overworldbackend.data.mapper.AreaMapMapper;
import de.unistuttgart.overworldbackend.data.mapper.CustomAreaMapMapper;
import de.unistuttgart.overworldbackend.repositories.AreaMapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class AreaMapService {

    @Autowired
    private WorldService worldService;

    @Autowired
    private DungeonService dungeonService;

    @Autowired
    private AreaMapMapper areaMapMapper;

    /**
     * Get the AreaMap from a world by its index and a course by its id
     * @throws ResponseStatusException (404) if world with its index could not be found in the course
     * @param courseId the id of the course
     * @param worldIndex the index of the world
     * @return the found AreaMap
     */
    public AreaMap getAreaMapFromWorld(final int courseId, final int worldIndex)
    {
        return worldService
                .getWorldByIndexFromCourse(courseId, worldIndex)
                .getAreaMap();
    }

    /**
     * Get the AreaMap from a dungeon by its index, the world its in by its index and a course by its id
     * @throws ResponseStatusException (404) if dungeon with its index could not be found in the course
     * @param courseId the id of the course
     * @param worldIndex the index of the world
     * @param dungeonIndex the index of the dungeon
     * @return the found AreaMap
     */
    public AreaMap getAreaMapFromDungeon(final int courseId, final int worldIndex, final int dungeonIndex)
    {
        return dungeonService
                .getDungeonByIndexFromCourse(courseId, worldIndex, dungeonIndex)
                .getAreaMap();
    }

    /**
     * Get the AreaMaps from all areas of a course by its id
     * @param courseId the id of the course
     * @return Set of AreaMaps of the course
     */
    public Set<AreaMap> getAllAreaMapsFromCourse(final int courseId)
    {
        Set<AreaMap> areaMaps = new HashSet<>();
        Set<World> worlds = worldService.getAllWorldsFromCourse(courseId);
        for (World world: worlds) {
            areaMaps.add(world.getAreaMap());
            List<Dungeon> dungeons = world.getDungeons();
            for (Dungeon dungeon: dungeons) {
                areaMaps.add(dungeon.getAreaMap());
            }
        }
        return areaMaps;
    }

    /**
     * Update an area map of a world by its id from a course
     * @throws ResponseStatusException (404) if world with its index could not be found in the course
     * @param courseId the id of the course the world is part of
     * @param worldIndex the index of the world
     * @param areaMapDTO the updated area map
     * @return the updated area map as DTO
     */
    public AreaMapDTO updateAreaMapOfWorld(final int courseId, final int worldIndex, final AreaMapDTO areaMapDTO){
        worldService.updateWorldContent(courseId, worldIndex, areaMapDTO);
        final AreaMap updatedAreaMap = getAreaMapFromWorld(courseId, worldIndex);
        return areaMapMapper.areaMapToAreaMapDTO(updatedAreaMap);
    }

    /**
     * Update an area map of a dungeon by its id from a course
     * @throws ResponseStatusException (404) if dungeon with its index could not be found in the course
     * @param courseId the id of the course the dungeon is part of
     * @param worldIndex the index of the world
     * @param dungeonIndex the index of the dungeon
     * @param areaMapDTO the updated area map
     * @return the updated area map as DTO
     */
    public AreaMapDTO updateAreaMapOfDungeon(final int courseId, final int worldIndex, final int dungeonIndex, final AreaMapDTO areaMapDTO){
        dungeonService.updateDungeonContent(courseId, worldIndex, dungeonIndex, areaMapDTO);
        final AreaMap updatedAreaMap = getAreaMapFromDungeon(courseId, worldIndex, dungeonIndex);
        return areaMapMapper.areaMapToAreaMapDTO(updatedAreaMap);
    }
}
