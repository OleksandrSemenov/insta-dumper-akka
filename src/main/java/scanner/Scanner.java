package scanner;

import org.brunocvcunha.instagram4j.Instagram4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import scanner.dto.UserDTO;
import scanner.entities.FakeUser;
import scanner.entities.User;
import scanner.repository.FakeUserRepository;
import scanner.repository.UserRepository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class Scanner {
    @Autowired
    private BeanFactory beanFactory;
    @Autowired
    private FakeUserRepository fakeUserRepository;
    @Autowired
    private UserRepository userRepository;
    private List<FakeUser> fakeUsers;
    private final String INSTAGRAM_USER_UKRAINE = "ukraine";
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private List<FakeUserWorker> workers = new ArrayList<>();
    private List<Future<?>> futures = new ArrayList<>();

    public Scanner() {
        fakeUsers = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        fakeUsers = getFakeUsers();
        fillSearchUsers();
        startWorkers();
    }

    public void startWorkers() {
        for (FakeUser fakeUser : fakeUsers) {
            Instagram4j instagram4j = beanFactory.getBean(Instagram4j.class, fakeUser.getUserName(), fakeUser.getPassword());
            FakeUserWorker fakeUserWorker = beanFactory.getBean(FakeUserWorker.class, instagram4j);
            workers.add(fakeUserWorker);
            futures.add(executorService.submit(fakeUserWorker));
        }
    }

    public void stopWorkers() {
        for(int i = 0; i < futures.size(); i++) {
            futures.get(i).cancel(true);
        }

        futures.clear();
        workers.clear();
    }

    public void submitNewFakeUserWorker(FakeUser fakeUser) {
        Instagram4j instagram4j = beanFactory.getBean(Instagram4j.class, fakeUser.getUserName(), fakeUser.getPassword());
        FakeUserWorker fakeUserWorker = beanFactory.getBean(FakeUserWorker.class, instagram4j);
        workers.add(fakeUserWorker);
        executorService.submit(fakeUserWorker);
    }

    private void fillSearchUsers() {
        if(userRepository.count() == 0) {
            User user = userRepository.save(new User(INSTAGRAM_USER_UKRAINE, false));
        }
    }

    private List<FakeUser> getFakeUsers() {
        List<FakeUser> fakeUsers = fakeUserRepository.findAll();

        if(fakeUsers.isEmpty()) {
            FakeUser fakeUser = new FakeUser("vasyarogov1959", "badalandabadec");
            fakeUserRepository.save(fakeUser);
            fakeUsers.add(fakeUser);
        }

        return fakeUsers;
    }
}
