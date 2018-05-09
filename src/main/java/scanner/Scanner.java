package scanner;

import org.brunocvcunha.instagram4j.Instagram4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import scanner.entities.FakeUser;
import scanner.repository.FakeUserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Scanner {
    @Autowired
    private BeanFactory beanFactory;
    @Autowired
    FakeUserRepository fakeUserRepository;
    List<FakeUser> fakeUsers;

    public Scanner() {
        fakeUsers = new ArrayList<>();
    }

    public void scan() {
        fakeUsers = fakeUserRepository.findAll();
        ExecutorService executorService = Executors.newFixedThreadPool(fakeUsers.size());

        for (int i = 0; i < fakeUsers.size(); i++) {
            Instagram4j instagram4j = beanFactory.getBean(Instagram4j.class, fakeUsers.get(i).getUserName(), fakeUsers.get(i).getPassword());
            FakeUserWorker fakeUserWorker = beanFactory.getBean(FakeUserWorker.class, instagram4j);
            executorService.submit(fakeUserWorker);
        }
    }

}
