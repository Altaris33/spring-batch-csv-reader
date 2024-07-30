package fr.captain.spring_batch_csv_reader.dao;

import fr.captain.spring_batch_csv_reader.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByTitle(String title);
}
