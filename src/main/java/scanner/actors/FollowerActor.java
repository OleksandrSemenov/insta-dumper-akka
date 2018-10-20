package scanner.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import org.apache.log4j.Logger;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramGetUserFollowersRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramGetUserFollowersResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUserSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import scanner.actors.messages.*;
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
public class FollowerActor extends AbstractActor {
    private final Logger logger = Logger.getLogger(FollowerActor.class);
    @Autowired
    private FollowerRepository followerRepository;
    @Autowired
    private UserRepository userRepository;
    private ActorRef fakeUserManagerActor;
    private Instagram4j instagram;
    private final String intagramProfileUrl = "https://www.instagram.com/";
    private ActorRef scannerActor;

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(TransferFakeUserManagerActorMsg.class, transferFakeUserManagerActorMsg -> {
            fakeUserManagerActor = transferFakeUserManagerActorMsg.getFakeUserManagerActor();
            logger.info("have ref to fakeUserManagerActor");
        }).match(ScanUserFollowerMsg.class, scanUserMsg -> {
            getUserFollowers(scanUserMsg);
        }).match(TransferScannerActorMsg.class, transferScannerActorMsg -> {
            scannerActor = transferScannerActorMsg.getScannerActor();
        }).build();
    }

    private void getUserFollowers(ScanUserFollowerMsg scanUserFollowerMsg){
        logger.info("start get followers user " + scanUserFollowerMsg.getEntityUser().getUserName());

        instagram = FakeUserManagerActor.getFreeFakeUser(fakeUserManagerActor);
        List<InstagramUserSummary> instagramFollowers = getFollowers(scanUserFollowerMsg.getIntagramId());

        List<User> users = new ArrayList<>();
            Set<Follower> followers = new HashSet<>();

            for (InstagramUserSummary instagramUserSummary : instagramFollowers) {
                if (!userRepository.existsByUserName(instagramUserSummary.getUsername())) {
                    users.add(new User(instagramUserSummary.getUsername(), false));
                }

                followers.add(new Follower(scanUserFollowerMsg.getEntityUser(), intagramProfileUrl + instagramUserSummary.getUsername()));
            }

            userRepository.saveAll(users);
            followerRepository.saveAll(followers);
            userRepository.save(scanUserFollowerMsg.getEntityUser());
            fakeUserManagerActor.tell(new SetFreeFakeUserMsg(instagram), getSelf());

            for (User applyScanUser : users) {
                scannerActor.tell(new ScanUserMsg(applyScanUser.getId(), applyScanUser.getUserName()), ActorRef.noSender());
            }
    }

    private List<InstagramUserSummary> getFollowers(long id) {
        List<InstagramUserSummary> followers = new ArrayList<>();
        String nextMaxId = null;
        final int SLEEP_ONE_SECOND = 1000;

        while (true) {
            try {
                InstagramGetUserFollowersResult followersResult = instagram.sendRequest(new InstagramGetUserFollowersRequest(id, nextMaxId));
                logger.info("GET FOLLOWERS FROM ==================================== " + id);

                if (followersResult == null || followersResult.getUsers() == null) {
                    logger.info("for this id = " + id + " 0 followers");
                    break;
                }

                followers.addAll(followersResult.getUsers());
                nextMaxId = followersResult.getNext_max_id();

                if (nextMaxId == null) {
                    break;
                }

                try {
                    Thread.sleep(SLEEP_ONE_SECOND);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                logger.error("get followers ", e);
            }
        }

        return followers;
    }
}
