package scanner;

import org.brunocvcunha.instagram4j.Instagram4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private BlockingQueue<User> searchUsers = new LinkedBlockingDeque<>();
    private final String INSTAGRAM_USER_UKRAINE = "ukraine";
    private Set<String> foundUsers = ConcurrentHashMap.newKeySet();
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private List<FakeUserWorker> workers = new ArrayList<>();

    public Scanner() {
        fakeUsers = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        fakeUsers = getFakeUsers();
        fillSearchUsers();
        foundUsers = initFoundUsers();
        startWorkers();
    }

    public void startWorkers() {
        for (int i = 0; i < fakeUsers.size(); i++) {
            Instagram4j instagram4j = beanFactory.getBean(Instagram4j.class, fakeUsers.get(i).getUserName(), fakeUsers.get(i).getPassword());
            FakeUserWorker fakeUserWorker = beanFactory.getBean(FakeUserWorker.class, instagram4j, searchUsers, foundUsers);
            workers.add(fakeUserWorker);
            executorService.submit(fakeUserWorker);
        }
    }

    public void stopWorkers() {
        for(FakeUserWorker fakeUserWorker : workers) {
            fakeUserWorker.stop();
        }

        workers.clear();
    }

    public void submitNewFakeUserWorker(FakeUser fakeUser) {
        Instagram4j instagram4j = beanFactory.getBean(Instagram4j.class, fakeUser.getUserName(), fakeUser.getPassword());
        FakeUserWorker fakeUserWorker = beanFactory.getBean(FakeUserWorker.class, instagram4j, searchUsers, foundUsers);
        executorService.submit(fakeUserWorker);
    }

    private void fillSearchUsers() {
        searchUsers.addAll(userRepository.findByIsScannedFalse());

        if(searchUsers.isEmpty()) {
            User user = userRepository.save(new User(INSTAGRAM_USER_UKRAINE, false));
            searchUsers.add(user);
            return;
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

    private Set<String> initFoundUsers() {
        Set<String> result = ConcurrentHashMap.newKeySet();
        for(User user : userRepository.findAll()) {
            result.add(user.getUserName());
        }

        return result;
    }

}
