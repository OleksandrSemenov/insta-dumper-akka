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
import scanner.entities.User;
import scanner.repository.UserRepository;
import java.io.IOException;
import java.util.ArrayList;
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

        while (true) {
            try {
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

                for(InstagramUserSummary instagramUserSummary : getFollowers(user.getPk())) {
                    if(!foundUsers.contains(instagramUserSummary.getUsername())) {
                        foundUsers.add(instagramUserSummary.getUsername());
                        users.add(new User(instagramUserSummary.getUsername() ,false));
                    }
                }

                userRepository.saveAll(users);
                searchUsers.addAll(users);
            } catch (Exception e) {
                logger.error("socket exception", e);
            }
        }
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

        try {
            while (true) {
                InstagramGetUserFollowersResult followersResult = instagram.sendRequest(new InstagramGetUserFollowersRequest(id, nextMaxId));
                if(followersResult == null || followersResult.getUsers() == null) {
                    break;
                }

                followers.addAll(followersResult.getUsers());
                nextMaxId = followersResult.getNext_max_id();

                if(nextMaxId == null) {
                    break;
                }
            }
        } catch (IOException e) {
            logger.error("", e);
        }

        return followers;
    }
}
