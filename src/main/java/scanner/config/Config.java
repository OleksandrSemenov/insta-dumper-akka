package scanner.config;

import org.brunocvcunha.instagram4j.Instagram4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import scanner.FakeUserWorker;
import scanner.Scanner;
import scanner.dto.UserDTO;
import scanner.entities.User;

import javax.sql.DataSource;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(value = "scanner.repository")
public class Config {
    @Bean
    @Scope(value = "prototype")
    @Lazy(value = true)
    public FakeUserWorker getFakeUserWorker(Instagram4j instagram4j) {
        return new FakeUserWorker(instagram4j);
    }

    @Bean
    public Scanner getScanner() {
        return new Scanner();
    }

    @Bean
    public BlockingQueue<User> scanUsers(){
        return new LinkedBlockingQueue<User>();
    }

}
