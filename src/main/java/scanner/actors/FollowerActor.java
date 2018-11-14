package scanner.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.singleton.ClusterSingletonProxy;
import akka.cluster.singleton.ClusterSingletonProxySettings;
import org.apache.log4j.Logger;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramGetUserFollowersRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramGetUserFollowersResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUserSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import scanner.MyInstagram4j;
import scanner.actors.messages.*;
import scanner.entities.Follower;
import scanner.entities.ScanStatus;
import scanner.entities.User;
import scanner.repository.FollowerRepository;
import scanner.repository.UserRepository;

import java.util.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FollowerActor extends AbstractActor {
    private final Logger logger = Logger.getLogger(FollowerActor.class);
    @Autowired
    private FollowerRepository followerRepository;
    @Autowired
    private UserRepository userRepository;
    private ActorRef fakeUserManagerActor;
    @Autowired
    @Qualifier("scannerRouter")
    private ActorRef scannerRouter;

    public FollowerActor(){
        ClusterSingletonProxySettings proxySettings =
                ClusterSingletonProxySettings.create(getContext().getSystem());
        fakeUserManagerActor =
                getContext().getSystem().actorOf(ClusterSingletonProxy.props("/user/fakeUserManager", proxySettings),
                        "fakeUserManagerProxy" + UUID.randomUUID());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(ScanUserFollowerMsg.class, scanUserMsg -> {
            getUserFollowers(scanUserMsg);
        }).build();
    }

    private void getUserFollowers(ScanUserFollowerMsg scanUserFollowerMsg){
        logger.info("start get followers user " + scanUserFollowerMsg.getEntityUser().getUserName());

        List<InstagramUserSummary> instagramFollowers = getFollowers(scanUserFollowerMsg.getEntityUser().getPk());

        List<User> users = new ArrayList<>();
            Set<Follower> followers = new HashSet<>();

            for (InstagramUserSummary instagramUserSummary : instagramFollowers) {
                if (!userRepository.existsByUserName(instagramUserSummary.getUsername())) {
                    users.add(new User(instagramUserSummary.getUsername(), ScanStatus.NotScanned));
                }

                followers.add(new Follower(scanUserFollowerMsg.getEntityUser(), instagramUserSummary.getUsername()));
            }

            userRepository.saveAll(users);
            followerRepository.saveAll(followers);
            scanUserFollowerMsg.getEntityUser().setScanStatus(ScanStatus.CompleteFollowers);
            userRepository.save(scanUserFollowerMsg.getEntityUser());

            for (User applyScanUser : users) {
                scannerRouter.tell(new ScanUserProfileMsg(applyScanUser.getId(), applyScanUser.getUserName()), ActorRef.noSender());
            }
    }

    private List<InstagramUserSummary> getFollowers(long id) {
        List<InstagramUserSummary> followers = new ArrayList<>();
        String nextMaxId = null;
        final int SLEEP_ONE_SECOND = 1000;
        MyInstagram4j instagram = null;

        while (true) {
            try {
                instagram = MyInstagram4j.fromDto(FakeUserManagerActor.getFreeFakeUser(fakeUserManagerActor));
                InstagramGetUserFollowersResult followersResult = instagram.sendRequest(new InstagramGetUserFollowersRequest(id, nextMaxId));
                fakeUserManagerActor.tell(new SetFreeFakeUserMsg(instagram.getDto()), getSelf());
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
            } catch (Exception e) {
                fakeUserManagerActor.tell(new SetFreeFakeUserMsg(instagram.getDto()), getSelf());
                logger.error("get followers ", e);
            }
        }

        return followers;
    }
}
