package scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import scanner.entities.User;
import scanner.repository.SearchStateRepository;
import scanner.repository.UserRepository;
import java.util.regex.Pattern;

@SpringBootApplication
public class Main implements CommandLineRunner {
    @Autowired
    private Scanner scanner;

    @Autowired
    private SearchStateRepository searchStateRepository;

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) {
        //System.out.println(searchStatesDao.getFirstAvailable().getUserName());
        //scanner.scan();
        //searchStateRepository.save(new SearchState("sasha_hudyma", null, false));
        //String name = "Gorlov Egor ";
        //String search = name.trim().replaceAll(Pattern.quote(" ")," & ");
        //System.out.println(search);
        //for(User user : userRepository.findbyFullName(search)) {
        //    System.out.println(user.getFullName());
        //}
    }
}
