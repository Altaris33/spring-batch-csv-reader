package fr.captain.spring_batch_csv_reader.processor;

import fr.captain.spring_batch_csv_reader.model.Book;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BookItemProcessor implements ItemProcessor<Book, Book> {

    private StepExecution stepExecution;

    @BeforeStep
    public void saveStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public Book process(final Book book) throws Exception {

        ExecutionContext context = this.stepExecution.getJobExecution().getExecutionContext();
        List<String> processedBookTitles = (List<String>) context.get("processedBookTitles");

        if (processedBookTitles == null) {
            processedBookTitles = new ArrayList<>();
        }

        processedBookTitles.add(book.getTitle());
        context.put("processedBookTitles", processedBookTitles);
        return book;
    }

    @AfterStep
    public void afterStep(StepExecution stepExecution) {
        ExecutionContext executionContext = stepExecution.getExecutionContext();
        List<String> processedBookTitles = (List<String>) executionContext.get("processedBookTitles");
        if (processedBookTitles != null) {
            System.out.println("Processed Book Titles: " + processedBookTitles);
        }
    }
}
