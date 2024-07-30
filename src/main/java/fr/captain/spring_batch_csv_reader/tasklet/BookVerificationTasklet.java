package fr.captain.spring_batch_csv_reader.tasklet;

import fr.captain.spring_batch_csv_reader.dao.BookRepository;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookVerificationTasklet implements Tasklet {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        ExecutionContext context
                = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
        List<String> processedBookTitles = (List<String>) context.get("processedBookTitles");

        if (processedBookTitles != null) {
            for (String title: processedBookTitles) {
                if (bookRepository.existsByTitle(title)) {
                    System.out.println("Book title found: " + title);
                }
            }
        }
        return RepeatStatus.FINISHED;
    }
}
