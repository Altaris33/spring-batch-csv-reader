package fr.captain.spring_batch_csv_reader.writer;

import fr.captain.spring_batch_csv_reader.dao.BookRepository;
import fr.captain.spring_batch_csv_reader.model.Book;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BookItemWriter implements ItemWriter<Book> {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public void write(Chunk<? extends Book> books) throws Exception {
        books.forEach((book) -> System.out.println("Writing to DB" + book.toString()));
        bookRepository.saveAll(books);
    }
}
