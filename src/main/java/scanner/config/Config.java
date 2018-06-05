package scanner.config;

import org.brunocvcunha.instagram4j.Instagram4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import scanner.FakeUserWorker;
import scanner.SearchStateManager;
import scanner.Scanner;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(value = "scanner.repository")
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
