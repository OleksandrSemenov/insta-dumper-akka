package scanner;

import org.apache.log4j.Logger;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramGetUserFollowersRequest;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramGetUserFollowersResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchUsernameResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUser;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUserSummary;
import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import scanner.entities.Follower;
import scanner.entities.User;
import scanner.repository.FollowerRepository;
import scanner.repository.UserRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class FakeUserWorker implements Runnable {
    private Instagram4j instagram;
    @Autowired
    private UserRepository userRepository;
    private final Logger logger = Logger.getLogger(FakeUserWorker.class);
    private BlockingQueue<User> searchUsers;
    private Set<String> foundUsers;
    @Autowired
    private FollowerRepository followerRepository;
    private boolean stop = false;

    public FakeUserWorker() {}

    public FakeUserWorker(Instagram4j instagram4j, BlockingQueue<User> searchUsers, Set<String> foundUsers) {
        this.instagram = instagram4j;
        this.searchUsers = searchUsers;
        this.foundUsers = foundUsers;
    }

    @Override
    public void run() {
        try {
            login();
        } catch (IOException e) {
            logger.error("login failed " + instagram.getUsername(), e);
            return;
        } catch (Exception e) {
            logger.error("login failed " + instagram.getUsername(), e);
            return;
        }

        while (!Thread.currentThread().isInterrupted()) {
            try {
                if(stop) {
                    Thread.currentThread().interrupt();
                }

                User user = searchUsers.take();
                InstagramUser instagramUser = getUser(user.getUserName());

                if(instagramUser == null) {
                    continue;
                }

                int id = user.getId();
                user = User.instagramUserToUserEntity(instagramUser);
                user.setScanned(true);
                user.setId(id);

                try {
                    userRepository.save(user);
                } catch (DataException e) {
                    logger.error("can't update", e);
                    searchUsers.add(new User(user.getUserName(), false));
                    continue;
                }

                foundUsers.add(user.getUserName());

                List<User> users = new ArrayList<>();
                Set<Follower> followers = new HashSet<>();
                String intagramProfileUrl = "https://www.instagram.com/";

                for(InstagramUserSummary instagramUserSummary : getFollowers(user.getPk())) {
                    if(!foundUsers.contains(instagramUserSummary.getUsername())) {
                        foundUsers.add(instagramUserSummary.getUsername());
                        users.add(new User(instagramUserSummary.getUsername() ,false));
                    }

                    followers.add(new Follower(user, intagramProfileUrl + instagramUserSummary.getUsername()));
                }

                userRepository.saveAll(users);
                searchUsers.addAll(users);
                followerRepository.saveAll(followers);
                logger.error("WORKER NAME = " + instagram.getUsername());
            } catch (Exception e) {
                logger.error("socket exception", e);
            }
        }
    }

    public void stop() {
        stop = true;
        //Thread.currentThread().interrupt();
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

    private List<InstagramUserSummary> getFollowers(long id) {
        List<InstagramUserSummary> followers = new ArrayList<>();
        String nextMaxId = null;
        boolean isConnectionReset = false;
        final int SLEEP_ONE_SECOND = 1000;

        do {
            try {
                while (true) {
                    InstagramGetUserFollowersResult followersResult = instagram.sendRequest(new InstagramGetUserFollowersRequest(id, nextMaxId));
                    if (followersResult == null || followersResult.getUsers() == null) {
                        break;
                    }

                    followers.addAll(followersResult.getUsers());
                    nextMaxId = followersResult.getNext_max_id();

                    if (nextMaxId == null) {
                        isConnectionReset = false;
                        break;
                    }

                    sleep(SLEEP_ONE_SECOND);
                }
            } catch (IOException e) {
                logger.error("followers ;( + " + id, e);
                isConnectionReset = true;
                nextMaxId = null;
                followers.clear();
            }
        }
        while (isConnectionReset);

        return followers;
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
