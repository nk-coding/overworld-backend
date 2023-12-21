package de.unistuttgart.overworldbackend.service;

import aj.org.objectweb.asm.TypeReference;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.config.AreaConfig;
import de.unistuttgart.overworldbackend.data.mapper.AreaMapMapper;
import de.unistuttgart.overworldbackend.data.mapper.WorldMapper;
import de.unistuttgart.overworldbackend.repositories.WorldRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.ClientHttpResponseStatusCodeException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class WorldService {

    AreaConfig areaConfig;

    @Autowired
    private WorldRepository worldRepository;

    @Autowired
    private WorldMapper worldMapper;

    @Autowired
    private AreaMapMapper areaMapMapper;

    public WorldService(){
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
     * Get a world of a course
     *
     * @throws ResponseStatusException (404) if world with its static name could not be found in the course
     * @param courseId the id of the course the world is part of
     * @param worldIndex the index of the world searching for
     * @return the found world object
     */
    public World getWorldByIndexFromCourse(final int courseId, final int worldIndex) {
        return worldRepository
            .findByIndexAndCourseId(worldIndex, courseId)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format(
                        "There is no world with static name %s in the course with ID %s.",
                        worldIndex,
                        courseId
                    )
                )
            );
    }

    /**
     * Update a world by its id from a course.
     *
     * Only the topic name and active status is updatable.
     *
     * @throws ResponseStatusException (404) if course or world by its id do not exist
     * @param courseId the id of the course the world is part of
     * @param worldIndex the index of the world that should get updated
     * @param worldDTO the updated parameters
     * @return the updated world as DTO
     */
    public WorldDTO updateWorldFromCourse(final int courseId, final int worldIndex, final WorldDTO worldDTO) {
        final World world = getWorldByIndexFromCourse(courseId, worldIndex);
        world.setTopicName(worldDTO.getTopicName());
        world.setActive(worldDTO.isActive());
        final World updatedWorld = worldRepository.save(world);
        return worldMapper.worldToWorldDTO(updatedWorld);
    }

    /**
     * Returns the World with the given index of the given course
     * @param worldIndex index of the world
     * @param courseId id of the course
     * @return Optional World if the world exists
     */
    public Optional<World> getOptionalWorldByIndexFromCourse(final int worldIndex, final int courseId) {
        return worldRepository.findByIndexAndCourseId(worldIndex, courseId);
    }

    /**
     * Returns all worlds of a course
     * @param courseId id of the course
     * @return Set of Worlds of the course
     */
    public Set<World> getAllWorldsFromCourse(final int courseId) {
        return worldRepository.findAllByCourseId(courseId);
    }

    /**
     * Updates the content of a world with the given area map dto
     *
     * @throws ResponseStatusException (404) if world with its index could not be found in the course
     * @throws ResponseStatusException (400) if area map is generated, but no custom area map is provided
     * @param courseId the id of the course the world is part of
     * @param worldIndex the index of the world
     * @param areaMapDTO the update parameters
     */
    public void updateWorldContent(final int courseId, final int worldIndex, final AreaMapDTO areaMapDTO){
        final World world = getWorldByIndexFromCourse(courseId, worldIndex);

        AreaMap newAreaMap = areaMapMapper.areaMapDTOToAreaMap(areaMapDTO);
        world.getAreaMap().setGeneratedArea(newAreaMap.isGeneratedArea());
        if(newAreaMap.isGeneratedArea())
        {
            world.getAreaMap().setCustomAreaMap(newAreaMap.getCustomAreaMap());
        }
        else
        {
            world.getAreaMap().setCustomAreaMap(null);
        }

        if(areaMapDTO.isGeneratedArea())
        {
            if(areaMapDTO.getCustomAreaMap() == null)
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No custom area map provided");
            }
            updateMinigames(world, areaMapDTO.getCustomAreaMap().getMinigameSpots().size());
            updateNPCs(world, areaMapDTO.getCustomAreaMap().getNpcSpots().size());
            updateBooks(world, areaMapDTO.getCustomAreaMap().getBookSpots().size());
            updateDungeons(world, areaMapDTO.getCustomAreaMap().getSceneTransitionSpots().size());
        }
        else
        {
            updateMinigames(world, areaConfig.getAmountMinigames());
            updateNPCs(world, areaConfig.getAmountNPCs());
            updateBooks(world, areaConfig.getAmountBooks());
            updateDungeons(world, areaConfig.getAmountDungeons());
        }

        worldRepository.save(world);
    }

    /**
     * Adds or removes minigames until number of minigames specified is reached
     * @param world the world to edit
     * @param numberOfMinigames the amount of minigames the world should contain
     */
    private void updateMinigames(World world, int numberOfMinigames){
        if(world.getMinigameTasks().size() > numberOfMinigames) {
            Set<MinigameTask> remainingMinigames = new HashSet<>();
            List<MinigameTask> minigamesList = world.getMinigameTasks().stream().toList();
            for (int index = 0; index < numberOfMinigames; index++) {
                remainingMinigames.add(minigamesList.get(index));
            }
            world.setMinigameTasks(remainingMinigames);
        }
        if(world.getMinigameTasks().size() < numberOfMinigames){
            for(int minigameIndex = world.getMinigameTasks().size()+1; minigameIndex<=numberOfMinigames; minigameIndex++)
            {
                final MinigameTask minigame = new MinigameTask(null, null, minigameIndex);
                world.getMinigameTasks().add(minigame);
            }
        }
    }

    /**
     * Adds or removes npcs until number of npcs specified is reached
     * @param world the world to edit
     * @param numberOfNPCs the amount of npcs the world should contain
     */
    private void updateNPCs(World world, int numberOfNPCs){
        if(world.getNpcs().size() > numberOfNPCs) {
            Set<NPC> remainingNPCs = new HashSet<>();
            List<NPC> npcList = world.getNpcs().stream().toList();
            for (int index = 0; index < numberOfNPCs; index++) {
                remainingNPCs.add(npcList.get(index));
            }
            world.setNpcs(remainingNPCs);
        }
        if(world.getNpcs().size() < numberOfNPCs){
            for(int npcIndex = world.getNpcs().size()+1; npcIndex<=numberOfNPCs; npcIndex++)
            {
                final NPC npc = new NPC(new ArrayList<>(), npcIndex);
                world.getNpcs().add(npc);
            }
        }
    }

    /**
     * Adds or removes books until number of books specified is reached
     * @param world the world to edit
     * @param numberOfBooks the amount of books the world should contain
     */
    private void updateBooks(World world, int numberOfBooks){
        if(world.getBooks().size() > numberOfBooks) {
            Set<Book> remainingBooks = new HashSet<>();
            List<Book> bookList = world.getBooks().stream().toList();
            for (int index = 0; index < numberOfBooks; index++) {
                remainingBooks.add(bookList.get(index));
            }
            world.setBooks(remainingBooks);
        }
        if(world.getBooks().size() < numberOfBooks){
            for(int bookIndex = world.getBooks().size()+1; bookIndex<=numberOfBooks; bookIndex++)
            {
                final Book book = new Book("", bookIndex);
                world.getBooks().add(book);
            }
        }
    }

    /**
     * Adds or removes dungeons until number of dungeons specified is reached
     * @param world the world to edit
     * @param numberOfDungeons the amount of dungeons the world should contain
     */
    private void updateDungeons(World world, int numberOfDungeons){
        if(world.getDungeons().size() > numberOfDungeons)
        {
            List<Dungeon> remainingDungeons = new ArrayList<>();
            for(int index = 0; index<numberOfDungeons; index++)
            {
                remainingDungeons.add(world.getDungeons().get(index));
            }
            world.setDungeons(remainingDungeons);
        }
        if(world.getDungeons().size() < numberOfDungeons)
        {
            for(int dungeonIndex = world.getDungeons().size()+1; dungeonIndex <= numberOfDungeons; dungeonIndex++)
            {
                Set<MinigameTask> minigames = getDefaultMinigames();
                Set<NPC> npcs = getDefaultNPCs();
                Set<Book> books = getDefaultBooks();
                Dungeon dungeon = new Dungeon("Dungeon " + dungeonIndex, "", false, minigames, npcs, books, dungeonIndex);
                world.getDungeons().add(dungeon);
            }
        }
    }

    /**
     * Returns a set containing the number of default minigames an area has
     * @return a set of minigames
     */
    private Set<MinigameTask> getDefaultMinigames(){
        int numberOfMinigames = areaConfig.getAmountMinigames();
        Set<MinigameTask> minigames = new HashSet<>();
        for (int minigameIndex = 1; minigameIndex <= numberOfMinigames; minigameIndex++) {
            final MinigameTask minigame = new MinigameTask(null, null, minigameIndex);
            minigames.add(minigame);
        }
        return minigames;
    }

    /**
     * Returns a set containing the number of default npcs an area has
     * @return a set of npcs
     */
    private Set<NPC> getDefaultNPCs(){
        int numberOfNPCs = areaConfig.getAmountNPCs();
        Set<NPC> npcs = new HashSet<>();
        for (int npcIndex = 1; npcIndex <= numberOfNPCs; npcIndex++) {
            final NPC npc = new NPC(new ArrayList<>(), npcIndex);
            npcs.add(npc);
        }
        return npcs;
    }

    /**
     * Returns a set containing the number of default bookss an area has
     * @return a set of books
     */
    private Set<Book> getDefaultBooks(){
        int numberOfBooks = areaConfig.getAmountBooks();
        Set<Book> books = new HashSet<>();
        for (int bookIndex = 1; bookIndex <= numberOfBooks; bookIndex++) {
            final Book book = new Book("", bookIndex);
            books.add(book);
        }
        return books;
    }
}
