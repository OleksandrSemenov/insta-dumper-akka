package scanner;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import scanner.actors.ScannerActor;
import scanner.dto.UserDTO;
import scanner.entities.FakeUser;
import scanner.entities.User;
import scanner.repository.FakeUserRepository;
import scanner.repository.FollowerRepository;
import scanner.repository.UserRepository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.*;

public class Scanner {
    @Autowired
    private FakeUserRepository fakeUserRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FollowerRepository followerRepository;
    private List<FakeUser> fakeUsers;
    private final String INSTAGRAM_USER_UKRAINE = "ukraine";
    private ActorRef scannerActor;

    public Scanner() {
        fakeUsers = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        ActorSystem system = ActorSystem.create("dump-system");
        scannerActor = system.actorOf(Props.create(ScannerActor.class, userRepository, followerRepository), "scanner");
        fakeUsers = getFakeUsers();
        fillSearchUsers();
        startWorkers();
        sendScanUsers();
    }

    public void startWorkers() {
        for (FakeUser fakeUser : fakeUsers) {
            Instagram4j instagram4j = Instagram4j.builder().username(fakeUser.getUserName()).password(fakeUser.getPassword()).build();
            scannerActor.tell(instagram4j, ActorRef.noSender());
        }
    }

    public void stopWorkers() {

    }

    public void submitNewFakeUserWorker(FakeUser fakeUser) {
        Instagram4j instagram4j = Instagram4j.builder().username(fakeUser.getUserName()).password(fakeUser.getPassword()).build();
        scannerActor.tell(instagram4j, ActorRef.noSender());
    }

    private void sendScanUsers(){
        for(User user : userRepository.getSearchUsers()){
            scannerActor.tell(new UserDTO(user.getId(), user.getUserName()), ActorRef.noSender());
        }
    }

    private void fillSearchUsers() {
        if(userRepository.count() == 0) {
            userRepository.save(new User(INSTAGRAM_USER_UKRAINE, false));
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
