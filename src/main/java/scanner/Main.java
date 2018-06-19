package scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import scanner.entities.User;
import scanner.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
@SpringBootApplication
public class Main implements CommandLineRunner {
    @Autowired
    private Scanner scanner;

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) {
        //scanner.scan();
        //CREATE INDEX fts_idx ON users USING GIN (to_tsvector('simple', full_name));
    }
}
