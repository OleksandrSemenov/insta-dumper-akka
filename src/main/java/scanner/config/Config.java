package scanner.config;

import org.brunocvcunha.instagram4j.Instagram4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import scanner.Scanner;
import scanner.entities.User;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(value = "scanner.repository")
public class Config {
    @Bean
    public Scanner getScanner() {
        return new Scanner();
    }
}
