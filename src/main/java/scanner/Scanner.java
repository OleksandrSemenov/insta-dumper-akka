package scanner;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import scanner.actors.ScannerActor;
import scanner.dto.FakeUserDTO;
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
    private List<FakeUser> fakeUsers;
    private final String INSTAGRAM_USER_UKRAINE = "ukraine";
    private ActorRef scannerActor;
    @Autowired
    private ActorSystem system;
    public Scanner() {
        fakeUsers = new ArrayList<>();
    }
    private boolean scannerWork;

    @PostConstruct
    public void init() {
        scannerActor = system.actorOf(Props.create(ScannerActor.class), "scanner");
        fakeUsers = getFakeUsers();
        fillSearchUsers();
        startWorkers();
        sendScanUsers();
        scannerWork = true;
    }

    public void startWorkers() {
        for (FakeUser fakeUser : fakeUsers) {
            scannerActor.tell(new FakeUserDTO(fakeUser.getUserName(), fakeUser.getPassword()), ActorRef.noSender());
        }
    }

    public void startScanWithName(String name){
        startWorkers();
        scannerActor.tell(new UserDTO(0, name), ActorRef.noSender());
    }

    public void stopWorkers() {

    }

    public void submitNewFakeUserWorker(FakeUser fakeUser) {
        scannerActor.tell(new FakeUserDTO(fakeUser.getUserName(), fakeUser.getPassword()), ActorRef.noSender());
    }

    public boolean isScannerWork() {
        return scannerWork;
    }

    public void setScannerWork(boolean scannerWork) {
        this.scannerWork = scannerWork;
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
