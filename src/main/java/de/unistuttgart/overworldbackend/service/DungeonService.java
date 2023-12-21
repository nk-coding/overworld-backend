package de.unistuttgart.overworldbackend.service;

import aj.org.objectweb.asm.TypeReference;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.config.AreaConfig;
import de.unistuttgart.overworldbackend.data.mapper.AreaMapMapper;
import de.unistuttgart.overworldbackend.data.mapper.DungeonMapper;
import de.unistuttgart.overworldbackend.repositories.DungeonRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class DungeonService {

    AreaConfig areaConfig;

    @Autowired
    private DungeonRepository dungeonRepository;

    @Autowired
    private DungeonMapper dungeonMapper;

    @Autowired
    private AreaMapMapper areaMapMapper;

    public DungeonService(){
        areaConfig = new AreaConfig();
        final ObjectMapper mapper = new ObjectMapper();

        final InputStream inputStream = TypeReference.class.getResourceAsStream("/areaConfig.json");
        try {
            areaConfig = mapper.readValue(inputStream, AreaConfig.class);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a dungeon of a course and a world
     *
     * @throws ResponseStatusException (404) if dungeon with its static name could not be found in the course
     * @param courseId the id of the course the dungeon is part of
     * @param worldIndex the index of the world the dungeon is part of
     * @param dungeonIndex the index of the dungeon searching of
     * @return the found dungeon object
     */
    public Dungeon getDungeonByIndexFromCourse(final int courseId, final int worldIndex, final int dungeonIndex) {
        return dungeonRepository
            .findAllByIndexAndCourseId(dungeonIndex, courseId)
            .parallelStream()
            .filter(dungeon -> dungeon.getWorld().getIndex() == worldIndex)
            .findAny()
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format(
                        "There is no dungeon with index %s in world with index %s in course with id %s.",
                        dungeonIndex,
                        worldIndex,
                        courseId
                    )
                )
            );
    }

    /**
     * Get dungeons of a course and a world
     *
     * @throws ResponseStatusException (404) if world with its static name could not be found in the course
     * @param courseId the id of the course the dungeons are part of
     * @param worldIndex the index of the world the dungeons are part of
     * @return the found dungeon object
     */
    public Set<DungeonDTO> getDungeonsFromWorld(final int courseId, final int worldIndex) {
        return dungeonMapper.dungeonsToDungeonDTOs(
            dungeonRepository
                .findAllByCourseId(courseId)
                .parallelStream()
                .filter(dungeon -> dungeon.getWorld().getIndex() == worldIndex)
                .collect(Collectors.toSet())
        );
    }

    /**
     * Update a dungeon by its id from a course and a world.
     * Only the topic name and active status is updatable.
     *
     * @throws ResponseStatusException (404) if course, world or dungeon by its id do not exist
     * @param courseId the id of the course the dungeon is part of
     * @param worldIndex the index of the world where the dungeon should be listed
     * @param dungeonIndex the index of the dungeon that should get updated
     * @param dungeonDTO the updated parameters
     * @return the updated dungeon as DTO
     */
    public DungeonDTO updateDungeonFromCourse(
        final int courseId,
        final int worldIndex,
        final int dungeonIndex,
        final DungeonDTO dungeonDTO
    ) {
        final Dungeon dungeon = getDungeonByIndexFromCourse(courseId, worldIndex, dungeonIndex);
        dungeon.setTopicName(dungeonDTO.getTopicName());
        dungeon.setActive(dungeonDTO.isActive());
        final Dungeon updatedDungeon = dungeonRepository.save(dungeon);
        return dungeonMapper.dungeonToDungeonDTO(updatedDungeon);
    }

    /**
     * Updates the content of a dungeon with the given area map dto
     *
     * @throws ResponseStatusException (404) if dungeon with its index could not be found in the course
     * @throws ResponseStatusException (400) if area map is generated, but no custom area map is provided
     * @param courseId the id of the course the dungeon is part of
     * @param worldIndex the index of the world the dungeon is in
     * @param dungeonIndex the index of the dungeon
     * @param areaMapDTO the update parameters
     */
    public void updateDungeonContent(final int courseId, final int worldIndex, final int dungeonIndex, final AreaMapDTO areaMapDTO){
        final Dungeon dungeon = getDungeonByIndexFromCourse(courseId, worldIndex, dungeonIndex);

        AreaMap newAreaMap = areaMapMapper.areaMapDTOToAreaMap(areaMapDTO);
        dungeon.getAreaMap().setGeneratedArea(newAreaMap.isGeneratedArea());
        if(newAreaMap.isGeneratedArea())
        {
            dungeon.getAreaMap().setCustomAreaMap(newAreaMap.getCustomAreaMap());
        }
        else
        {
            dungeon.getAreaMap().setCustomAreaMap(null);
        }

        if(areaMapDTO.isGeneratedArea())
        {
            if(areaMapDTO.getCustomAreaMap() == null)
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No custom area map provided");
            }
            updateMinigames(dungeon, areaMapDTO.getCustomAreaMap().getMinigameSpots().size());
            updateNPCs(dungeon, areaMapDTO.getCustomAreaMap().getNpcSpots().size());
            updateBooks(dungeon, areaMapDTO.getCustomAreaMap().getBookSpots().size());
        }
        else
        {
            updateMinigames(dungeon, areaConfig.getAmountMinigames());
            updateNPCs(dungeon, areaConfig.getAmountNPCs());
            updateBooks(dungeon, areaConfig.getAmountBooks());
        }

        dungeonRepository.save(dungeon);
    }

    /**
     * Adds or removes minigames until number of minigames specified is reached
     * @param dungeon the dungeon to edit
     * @param numberOfMinigames the amount of minigames the dungeon should contain
     */
    private void updateMinigames(Dungeon dungeon, int numberOfMinigames){
        if(dungeon.getMinigameTasks().size() > numberOfMinigames) {
            Set<MinigameTask> remainingMinigames = new HashSet<>();
            List<MinigameTask> minigamesList = dungeon.getMinigameTasks().stream().toList();
            for (int index = 0; index < numberOfMinigames; index++) {
                remainingMinigames.add(minigamesList.get(index));
            }
            dungeon.setMinigameTasks(remainingMinigames);
        }
        if(dungeon.getMinigameTasks().size() < numberOfMinigames){
            for(int minigameIndex = dungeon.getMinigameTasks().size()+1; minigameIndex<=numberOfMinigames; minigameIndex++)
            {
                final MinigameTask minigame = new MinigameTask(null, null, minigameIndex);
                dungeon.getMinigameTasks().add(minigame);
            }
        }
    }

    /**
     * Adds or removes npcs until number of npcs specified is reached
     * @param dungeon the dungeon to edit
     * @param numberOfNPCs the amount of npcs the dungeon should contain
     */
    private void updateNPCs(Dungeon dungeon, int numberOfNPCs){
        if(dungeon.getNpcs().size() > numberOfNPCs) {
            Set<NPC> remainingNPCs = new HashSet<>();
            List<NPC> npcList = dungeon.getNpcs().stream().toList();
            for (int index = 0; index < numberOfNPCs; index++) {
                remainingNPCs.add(npcList.get(index));
            }
            dungeon.setNpcs(remainingNPCs);
        }
        if(dungeon.getNpcs().size() < numberOfNPCs){
            for(int npcIndex = dungeon.getNpcs().size()+1; npcIndex<=numberOfNPCs; npcIndex++)
            {
                final NPC npc = new NPC(new ArrayList<>(), npcIndex);
                dungeon.getNpcs().add(npc);
            }
        }
    }

    /**
     * Adds or removes books until number of books specified is reached
     * @param dungeon the dungeon to edit
     * @param numberOfBooks the amount of books the dungeon should contain
     */
    private void updateBooks(Dungeon dungeon, int numberOfBooks){
        if(dungeon.getBooks().size() > numberOfBooks) {
            Set<Book> remainingBooks = new HashSet<>();
            List<Book> bookList = dungeon.getBooks().stream().toList();
            for (int index = 0; index < numberOfBooks; index++) {
                remainingBooks.add(bookList.get(index));
            }
            dungeon.setBooks(remainingBooks);
        }
        if(dungeon.getBooks().size() < numberOfBooks){
            for(int bookIndex = dungeon.getBooks().size()+1; bookIndex<=numberOfBooks; bookIndex++)
            {
                final Book book = new Book("", bookIndex);
                dungeon.getBooks().add(book);
            }
        }
    }
}
