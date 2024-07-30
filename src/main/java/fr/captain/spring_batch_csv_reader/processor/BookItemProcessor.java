package fr.captain.spring_batch_csv_reader.processor;

import fr.captain.spring_batch_csv_reader.model.Book;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class BookItemProcessor implements ItemProcessor<Book, Book> {

    @Override
    public Book process(final Book book) throws Exception {
        // Add any necessary processing logic here
        System.out.println("Processor: " + book.toString());
        return book;
    }
}
