package scanner;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;
import scanner.dao.FakeUsersDaoImpl;
import scanner.dao.interfaces.FakeUsersDao;
import scanner.dao.interfaces.SearchStatesDao;
import scanner.entities.FakeUser;
import scanner.entities.SearchState;
import scanner.repository.SearchStateRepository;

import javax.persistence.EntityManager;

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
        //searchStatesDao.add(new SearchState("sasha_hudyma", null, false));
    }
}
