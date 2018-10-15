package scanner.config;

import akka.actor.ActorSystem;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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

import static scanner.config.SpringExtension.SPRING_EXTENSION_PROVIDER;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(value = "scanner.repository")
public class Config {
    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public ActorSystem actorSystem(){
        ActorSystem system = ActorSystem.create("dump-system");
        SPRING_EXTENSION_PROVIDER.get(system)
                .initialize(applicationContext);
        return system;
    }

    @Bean
    public Scanner getScanner() {
        return new Scanner();
    }
}
