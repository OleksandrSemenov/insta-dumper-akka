package scanner.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.singleton.ClusterSingletonProxy;
import akka.cluster.singleton.ClusterSingletonProxySettings;
import akka.routing.Router;
import org.apache.log4j.Logger;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchUsernameResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import scanner.MyInstagram4j;
import scanner.actors.messages.*;
import scanner.entities.ScanStatus;
import scanner.entities.User;
import scanner.repository.UserRepository;

import java.io.IOException;
import java.util.UUID;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WorkerActor extends AbstractActor{
    private MyInstagram4j instagram;
    @Autowired
    private UserRepository userRepository;
    private final Logger logger = Logger.getLogger(WorkerActor.class);
    private User user;
    private ActorRef fakeUserManagerActor;
    @Autowired
    @Qualifier("followerRouter")
    private ActorRef followerRouter;

    public WorkerActor(){
        ClusterSingletonProxySettings proxySettings =
                ClusterSingletonProxySettings.create(getContext().getSystem());
        fakeUserManagerActor =
                getContext().getSystem().actorOf(ClusterSingletonProxy.props("/user/fakeUserManager", proxySettings),
                        "fakeUserManagerProxy" + UUID.randomUUID());
    }

    public WorkerActor(MyInstagram4j instagram){
        this.instagram = instagram;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(ScanUserProfileMsg.class, scanUser -> {
            logger.info("start scan user " + scanUser.getUserName());
            scanUser(scanUser);
        }).match(AddFakeUserMsg.class, fakeUserMsg -> {
            instagram = new MyInstagram4j(fakeUserMsg.getUserName(), fakeUserMsg.getPassword());

            if (!doLogin()){
                getSender().tell(SimpleMessages.LOGIN_FAILED, self());
                logger.error("failed start fake user " + fakeUserMsg.getUserName());
            }

            getSender().tell(new LoginSuccessfulMsg(instagram.getDto()), getSelf());
            instagram = null;
            logger.info("fake user started ok" + fakeUserMsg.getUserName());
        }).build();
    }

    public void scanUser(ScanUserProfileMsg scanUser) {
        try {
            if (scanUser == null) {
                return;
            }

            instagram = MyInstagram4j.fromDto(FakeUserManagerActor.getFreeFakeUser(fakeUserManagerActor));
            InstagramUser instagramUser = getUser(scanUser.getUserName());

            if (instagramUser == null) {
                fakeUserManagerActor.tell(new SetFreeFakeUserMsg(instagram.getDto()), getSelf());
                return;
            }

            user = User.instagramUserToUserEntity(instagramUser);
            
            if(scanUser.getId() != 0){
                user.setId(scanUser.getId());
            }

            user.setScanStatus(ScanStatus.CompleteProfile);
            userRepository.save(user);
            fakeUserManagerActor.tell(new SetFreeFakeUserMsg(instagram.getDto()), getSelf());
            followerRouter.tell(new ScanUserFollowerMsg(user), getSelf());
        } catch (Exception e) {
            logger.error("socket exception", e);
            user.setScanStatus(ScanStatus.NotScanned);
            userRepository.save(user);
            fakeUserManagerActor.tell(new SetFreeFakeUserMsg(instagram.getDto()), getSelf());
        }
    }

    private boolean doLogin() {
        try {
            login();
        } catch (Exception e) {
            logger.error("login failed " + instagram.getUsername(), e);
            return false;
        }
        return true;
    }

    private InstagramUser getUser(String userName) {
        InstagramSearchUsernameResult resutUser = null;
        try {
            resutUser = instagram.sendRequest(new InstagramSearchUsernameRequest(userName));
        } catch (IOException e) {
            logger.error("", e);
        }

        return resutUser.getUser();
    }

    private void login() throws IOException {
        instagram.setup();
        instagram.login();
    }
}
