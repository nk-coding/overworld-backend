package de.unistuttgart.overworldbackend.data.mapper;

import de.unistuttgart.overworldbackend.data.Book;
import de.unistuttgart.overworldbackend.data.BookDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { AreaLocationMapper.class })
public interface BookMapper {
    BookDTO bookToBookDTO(final Book book);

    Book bookDTOToBook(final BookDTO bookDTO);
}
