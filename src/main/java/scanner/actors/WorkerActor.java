package scanner.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import org.apache.log4j.Logger;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramGetUserFollowersRequest;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramGetUserFollowersResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchUsernameResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUser;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUserSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import scanner.dto.FakeUserDTO;
import scanner.dto.UserDTO;
import scanner.entities.FakeUser;
import scanner.entities.Follower;
import scanner.entities.User;
import scanner.repository.FollowerRepository;
import scanner.repository.UserRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WorkerActor extends AbstractActor{
    private Instagram4j instagram;
    @Autowired
    private UserRepository userRepository;
    private final Logger logger = Logger.getLogger(WorkerActor.class);
    @Autowired
    private FollowerRepository followerRepository;
    private final String intagramProfileUrl = "https://www.instagram.com/";
    private User user;

    public WorkerActor(){}

    public WorkerActor(Instagram4j instagram){
        this.instagram = instagram;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(UserDTO.class, scanUser -> {
            logger.info("start scan user " + scanUser.getUserName());
            scanUser(scanUser);
        }).match(FakeUserDTO.class, fakeUser -> {
            instagram = Instagram4j.builder().username(fakeUser.getUserName()).password(fakeUser.getPassword()).build();

            if (!doLogin()){
                getSender().tell(Messages.LOGIN_FAILED, self());
                logger.error("failed start fake user " + fakeUser.getUserName());
            }

            logger.info("fake user started ok" + fakeUser.getUserName());
        }).build();
    }

    public void scanUser(UserDTO scanUser) {
        try {
            if (scanUser == null) {
                return;
            }

            InstagramUser instagramUser = getUser(scanUser.getUserName());

            if (instagramUser == null) {
                return;
            }

            List<InstagramUserSummary> instagramFollowers = getFollowers(instagramUser.getPk());

            user = User.instagramUserToUserEntity(instagramUser);
            user.setId(scanUser.getId());
            user.setScanned(true);

            List<User> users = new ArrayList<>();
            Set<Follower> followers = new HashSet<>();

            for (InstagramUserSummary instagramUserSummary : instagramFollowers) {
                if (!userRepository.existsByUserName(instagramUserSummary.getUsername())) {
                    users.add(new User(instagramUserSummary.getUsername(), false));
                }

                followers.add(new Follower(user, intagramProfileUrl + instagramUserSummary.getUsername()));
            }

            userRepository.saveAll(users);
            followerRepository.saveAll(followers);
            logger.error("WORKER NAME = " + instagram.getUsername());
            userRepository.save(user);

            for (User applyScanUser : users) {
                getSender().tell(new UserDTO(applyScanUser.getId(), applyScanUser.getUserName()), ActorRef.noSender());
            }
        } catch (Exception e) {
            logger.error("socket exception", e);
            user.setScanned(false);
            userRepository.save(user);
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

    private List<InstagramUserSummary> getFollowers(long id) throws InterruptedException {
        List<InstagramUserSummary> followers = new ArrayList<>();
        String nextMaxId = null;
        final int SLEEP_ONE_SECOND = 1000;

        while (!Thread.currentThread().isInterrupted()) {
            try {
                InstagramGetUserFollowersResult followersResult = instagram.sendRequest(new InstagramGetUserFollowersRequest(id, nextMaxId));

                if (followersResult == null || followersResult.getUsers() == null) {
                    logger.info("for this id = " + id + " 0 followers");
                    break;
                }

                followers.addAll(followersResult.getUsers());
                nextMaxId = followersResult.getNext_max_id();

                if (nextMaxId == null) {
                    break;
                }

                Thread.sleep(SLEEP_ONE_SECOND);
            } catch (IOException e) {
                logger.error("get followers ", e);
            }
        }

        return followers;
    }
}
