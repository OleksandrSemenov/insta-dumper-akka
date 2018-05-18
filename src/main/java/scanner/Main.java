package scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import scanner.repository.SearchStateRepository;

@SpringBootApplication
public class Main implements CommandLineRunner {
    @Autowired
    private Scanner scanner;

    @Autowired
    private SearchStateRepository searchStateRepository;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) {
        //System.out.println(searchStatesDao.getFirstAvailable().getUserName());
        scanner.scan();
        //searchStateRepository.save(new SearchState("sasha_hudyma", null, false));
    }
}
