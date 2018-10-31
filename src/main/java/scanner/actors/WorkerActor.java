package scanner.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.routing.Router;
import org.apache.log4j.Logger;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchUsernameResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import scanner.actors.messages.*;
import scanner.entities.ScanStatus;
import scanner.entities.User;
import scanner.repository.UserRepository;

import java.io.IOException;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WorkerActor extends AbstractActor{
    private Instagram4j instagram;
    @Autowired
    private UserRepository userRepository;
    private final Logger logger = Logger.getLogger(WorkerActor.class);
    private User user;
    private ActorRef fakeUserManagerActor;
    private Router followerRouter;

    public WorkerActor(){
        fakeUserManagerActor = ScannerActor.getActor(getContext().getSystem(), ScannerActor.FAKE_USER_MANAGER_ACTOR_PATH);
    }

    public WorkerActor(Instagram4j instagram){
        this.instagram = instagram;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(ScanUserProfileMsg.class, scanUser -> {
            logger.info("start scan user " + scanUser.getUserName());
            scanUser(scanUser);
        }).match(AddFakeUserMsg.class, fakeUserMsg -> {
            instagram = Instagram4j.builder().username(fakeUserMsg.getUserName()).password(fakeUserMsg.getPassword()).build();

            if (!doLogin()){
                getSender().tell(SimpleMessages.LOGIN_FAILED, self());
                logger.error("failed start fake user " + fakeUserMsg.getUserName());
            }

            getSender().tell(new LoginSuccessfulMsg(instagram), getSelf());
            instagram = null;
            logger.info("fake user started ok" + fakeUserMsg.getUserName());
        }).match(TransferFollowersRouterMsg.class, transferFollowersRouterMsg -> {
            followerRouter = transferFollowersRouterMsg.getFollowersRouter();
            logger.info("have ref to followerRouter");
        }).build();
    }

    public void scanUser(ScanUserProfileMsg scanUser) {
        try {
            if (scanUser == null) {
                return;
            }

            instagram = FakeUserManagerActor.getFreeFakeUser(fakeUserManagerActor);
            InstagramUser instagramUser = getUser(scanUser.getUserName());

            if (instagramUser == null) {
                fakeUserManagerActor.tell(new SetFreeFakeUserMsg(instagram), getSelf());
                return;
            }

            user = User.instagramUserToUserEntity(instagramUser);
            
            if(scanUser.getId() != 0){
                user.setId(scanUser.getId());
            }

            user.setScanStatus(ScanStatus.CompleteProfile);
            userRepository.save(user);
            fakeUserManagerActor.tell(new SetFreeFakeUserMsg(instagram), getSelf());
            followerRouter.route(new ScanUserFollowerMsg(user), getSelf());
        } catch (Exception e) {
            logger.error("socket exception", e);
            user.setScanStatus(ScanStatus.NotScanned);
            userRepository.save(user);
            fakeUserManagerActor.tell(new SetFreeFakeUserMsg(instagram), getSelf());
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
