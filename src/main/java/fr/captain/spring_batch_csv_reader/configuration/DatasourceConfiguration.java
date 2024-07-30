package fr.captain.spring_batch_csv_reader.configuration;

import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class DatasourceConfiguration {

    @Bean(name = "csvDb")
    public DataSource dataSource() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        return builder.setType(EmbeddedDatabaseType.H2)
                .addScript("h2/schema-h2.sql")
                .setName("csvDb")
                .build();
                //.setDriverClassName("org.h2.Driver");
                //    .setUrl("jdbc:h2:mem:csvdb");
                //.setUsername("sa");
        //.setPassword("password").build();
    }

    @Bean
    @PersistenceContext
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean localEmf = new LocalContainerEntityManagerFactoryBean();
        localEmf.setDataSource(dataSource());
        localEmf.setPersistenceUnitName("persistenceUnitCaptain");
        localEmf.setPackagesToScan("fr.captain.spring_batch_csv_reader.model");

        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setShowSql(true);

        localEmf.setJpaVendorAdapter(jpaVendorAdapter);
        localEmf.setJpaProperties(additionalProperties());
        return localEmf;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    public Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        return properties;
    }
}
