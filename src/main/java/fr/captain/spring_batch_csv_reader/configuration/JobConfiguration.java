package fr.captain.spring_batch_csv_reader.configuration;

import fr.captain.spring_batch_csv_reader.model.Book;
import fr.captain.spring_batch_csv_reader.processor.BookItemProcessor;
import fr.captain.spring_batch_csv_reader.writer.BookItemWriter;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.autoconfigure.batch.JobLauncherApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing(dataSourceRef = "csvDb")
@EnableConfigurationProperties(value = BatchProperties.class)
public class JobConfiguration {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private BookItemProcessor processor;

    @Autowired
    private BookItemWriter writer;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private JobRepository jobRepository;

    @Bean
    public JobLauncherApplicationRunner jobLauncherApplicationRunner(JobLauncher launcher, JobExplorer explorer,
                                                    JobRepository repository, BatchProperties properties) {
        JobLauncherApplicationRunner runner = new JobLauncherApplicationRunner(launcher, explorer, repository);
        String jobName = properties.getJob().getName();
        if (!jobName.isEmpty()) {
            runner.setJobName(jobName);
        }
        return runner;
    }

    @Bean
    public Job importBookJob() throws Exception {
        return new JobBuilder("importBookJob", jobRepository)
                .start(step1())
                .incrementer(new RunIdIncrementer())
                .listener(new JobExecutionListener() {
                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                            System.out.println("Job COMPLETED");
                        }
                    }
                })
                .build();
    }

    @Bean
    public Step step1() throws Exception {
        return new StepBuilder("step1", jobRepository)
                .<Book, Book>chunk(10, transactionManager)
                .reader(reader())
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public FlatFileItemReader<Book> reader() {
        return new FlatFileItemReaderBuilder<Book>()
                .name("bookItemReader")
                .resource(new ClassPathResource("books.csv"))
                .delimited()
                .names(new String[]{"title", "author", "isbn"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Book>() {{
                    setTargetType(Book.class);
                }})
                .build();
    }
}
