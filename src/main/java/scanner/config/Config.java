package scanner.config;

import org.brunocvcunha.instagram4j.Instagram4j;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import scanner.FakeUserWorker;
import scanner.ManageSearchState;
import scanner.Scanner;
import scanner.dao.FakeUsersDaoImpl;
import scanner.dao.SearchStatesDaoImpl;
import scanner.dao.UsersDaoImpl;
import scanner.dao.interfaces.FakeUsersDao;
import scanner.dao.interfaces.SearchStatesDao;
import scanner.dao.interfaces.UsersDao;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
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
    public ManageSearchState getManageSearchState(){
        return new ManageSearchState(getSearchStateDaoImpl());
    }

    @Bean
    public Scanner getScanner() {
        return new Scanner();
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql:instagramScanner");
        dataSource.setUsername("postgres");
        dataSource.setPassword("123321");
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(new String[] { "scanner.entities" });

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(getHibernateProperties());

        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf){
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);

        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public FakeUsersDao getFakeUserDaoImpl(){
        return new FakeUsersDaoImpl();
    }

    @Bean
    public SearchStatesDao getSearchStateDaoImpl(){
        return new SearchStatesDaoImpl();
    }

    @Bean
    public UsersDao getUserDaoImpl(){
        return new UsersDaoImpl();
    }

    @Bean
    public FakeUsersDao fakeUsersDao(){
        return new FakeUsersDaoImpl();
    }

    private Properties getHibernateProperties() {
        Properties properties = new Properties();
        properties.put(AvailableSettings.DIALECT, "org.hibernate.dialect.PostgreSQL9Dialect");
        properties.put(AvailableSettings.SHOW_SQL, "true");
        properties.put(AvailableSettings.HBM2DDL_AUTO, "update");
        properties.put(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS, "org.springframework.orm.hibernate5.SpringSessionContext");
        properties.put(AvailableSettings.NON_CONTEXTUAL_LOB_CREATION, "true");
        return properties;
    }
}
