package scanner.config;

import org.brunocvcunha.instagram4j.Instagram4j;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import scanner.FakeUserWorker;
import scanner.SearchStateManager;
import scanner.Scanner;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("scanner.repository")
public class Config {
    @Bean
    @Scope(value = "prototype")
    @Lazy(value = true)
    public Instagram4j getInstagram(String userName, String password) {
        return Instagram4j.builder().username(userName).password(password).build();
    }

    @Bean
    @Scope(value = "prototype")
    @Lazy(value = true)
    public FakeUserWorker getFakeUserWorker(Instagram4j instagram4j) {
        return new FakeUserWorker(instagram4j);
    }

    @Bean
    public SearchStateManager getManageSearchState(){
        return new SearchStateManager();
    }

    @Bean
    public Scanner getScanner() {
        return new Scanner();
    }
}
