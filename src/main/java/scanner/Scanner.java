package scanner;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.singleton.ClusterSingletonManager;
import akka.cluster.singleton.ClusterSingletonManagerSettings;
import com.typesafe.config.ConfigFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import scanner.actors.FakeUserManagerActor;
import scanner.actors.messages.AddFakeUserMsg;
import scanner.actors.messages.ScanUserFollowerMsg;
import scanner.actors.messages.ScanUserProfileMsg;
import scanner.dto.UserDTO;
import scanner.entities.FakeUser;
import scanner.entities.ScanStatus;
import scanner.entities.User;
import scanner.repository.FakeUserRepository;
import scanner.repository.UserRepository;

import javax.annotation.PostConstruct;
import java.util.*;

import static scanner.config.SpringExtension.SPRING_EXTENSION_PROVIDER;

public class Scanner {
    @Autowired
    private FakeUserRepository fakeUserRepository;
    @Autowired
    private UserRepository userRepository;
    private List<FakeUser> fakeUsers;
    private final String INSTAGRAM_USER_UKRAINE = "ukraine";
    public Scanner() {
        fakeUsers = new ArrayList<>();
    }
    private boolean scannerWork;
    private final Logger logger = Logger.getLogger(Scanner.class);
    @Autowired
    @Qualifier("scannerRouter")
    private ActorRef scannerRouter;
    @Autowired
    private ActorSystem system;
    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        fakeUsers = getFakeUsers();
        fillSearchUsers();
        startWorkers();
        sendScanUsers();
        scannerWork = true;
    }

    public void startWorkers() {
        for (FakeUser fakeUser : fakeUsers) {
            scannerRouter.tell(new AddFakeUserMsg(fakeUser.getUserName(), fakeUser.getPassword()), ActorRef.noSender());
        }
    }

    public void startScanWithName(String name){
        startWorkers();
        scannerRouter.tell(new ScanUserProfileMsg(0, name), ActorRef.noSender());
    }

    public void stopWorkers() {

    }

    public void submitNewFakeUserWorker(FakeUser fakeUser) {
        scannerRouter.tell(new AddFakeUserMsg(fakeUser.getUserName(), fakeUser.getPassword()), ActorRef.noSender());
    }

    public boolean isScannerWork() {
        return scannerWork;
    }

    public void setScannerWork(boolean scannerWork) {
        this.scannerWork = scannerWork;
    }

    public void addNode(int port){
        com.typesafe.config.Config config = ConfigFactory.parseString(
                "akka.remote.netty.tcp.port=" + port + "\n" +
                        "akka.remote.artery.canonical.port=" + port)
                .withFallback(ConfigFactory.load());

        ActorSystem newNode = ActorSystem.create("dump-system", config);
        System.out.println("SYSTEM " + newNode.provider().getDefaultAddress().toString());

        SPRING_EXTENSION_PROVIDER.get(newNode)
                .initialize(applicationContext);

        newNode.actorOf(SPRING_EXTENSION_PROVIDER.get(newNode)
                .props("scannerActor"), "scanner");

        final ClusterSingletonManagerSettings settings =
                ClusterSingletonManagerSettings.create(system);

        newNode.actorOf(
                ClusterSingletonManager.props(
                        Props.create(FakeUserManagerActor.class),
                        null,
                        settings),
                "fakeUserManager");
    }

    private void sendScanUsers(){
        List<UserDTO> scanProfile = userRepository.getUsersForScanProfile();
        List<User> scanFollowers = userRepository.getUsersForScanFollowers();

        if(scanProfile.isEmpty() && scanFollowers.isEmpty()){
            scannerRouter.tell(new ScanUserProfileMsg(0, INSTAGRAM_USER_UKRAINE), ActorRef.noSender());
        }

        for(UserDTO scanProf : scanProfile){
            scannerRouter.tell(new ScanUserProfileMsg(scanProf.getId(), scanProf.getUserName()), ActorRef.noSender());
        }

        for(User scanFollow : scanFollowers){
            scannerRouter.tell(new ScanUserFollowerMsg(scanFollow), ActorRef.noSender());
        }
    }

    private void fillSearchUsers() {
        if(userRepository.count() == 0) {
            userRepository.save(new User(INSTAGRAM_USER_UKRAINE, ScanStatus.NotScanned));
        }
    }

    private List<FakeUser> getFakeUsers() {
        List<FakeUser> fakeUsers = fakeUserRepository.findAll();

        if(fakeUsers.isEmpty()) {
            FakeUser fakeUser = new FakeUser("zibrovka@mail.ru", "zibrovkinVasya");
            fakeUserRepository.save(fakeUser);
            fakeUsers.add(fakeUser);
        }

        return fakeUsers;
    }
}
