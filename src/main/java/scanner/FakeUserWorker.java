package scanner;

import org.apache.log4j.Logger;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramGetUserFollowersRequest;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramGetUserFollowersResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchUsernameResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUser;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUserSummary;
import org.bytedeco.javacv.FrameFilter;
import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import scanner.dto.UserDTO;
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
import java.util.stream.Collectors;

public class FakeUserWorker implements Runnable {
    private Instagram4j instagram;
    @Autowired
    private UserRepository userRepository;
    private final Logger logger = Logger.getLogger(FakeUserWorker.class);
    private BlockingQueue<UserDTO> searchUsers;
    private Set<String> foundUsers;
    @Autowired
    private FollowerRepository followerRepository;
    private boolean stop = false;
    private final String intagramProfileUrl = "https://www.instagram.com/";
    private UserDTO searchUser;

    public FakeUserWorker() {}

    public FakeUserWorker(Instagram4j instagram4j, BlockingQueue<UserDTO> searchUsers, Set<String> foundUsers) {
        this.instagram = instagram4j;
        this.searchUsers = searchUsers;
        this.foundUsers = foundUsers;
    }

    @Override
    public void run() {
        if (!doLogin()) return;

        while (!Thread.currentThread().isInterrupted()) {
            try {
                searchUser = searchUsers.take();
                InstagramUser instagramUser = getUser(searchUser.getUserName());

                if(instagramUser == null) {
                    continue;
                }

                List<InstagramUserSummary> instagramFollowers = getFollowers(instagramUser.getPk());

                if(Thread.currentThread().isInterrupted()){
                    searchUsers.add(searchUser);
                    break;
                }

                User user = User.instagramUserToUserEntity(instagramUser);
                user.setId(searchUser.getId());
                user.setScanned(true);

                try {
                    userRepository.save(user);
                } catch (DataException e) {
                    logger.error("can't update", e);
                    searchUsers.add(new UserDTO(user.getId(), user.getUserName()));
                    continue;
                }

                foundUsers.add(user.getUserName());

                List<User> users = new ArrayList<>();
                Set<Follower> followers = new HashSet<>();

                for(InstagramUserSummary instagramUserSummary : instagramFollowers) {
                    if(!foundUsers.contains(instagramUserSummary.getUsername())) {
                        foundUsers.add(instagramUserSummary.getUsername());
                        users.add(new User(instagramUserSummary.getUsername() ,false));
                    }

                    followers.add(new Follower(user, intagramProfileUrl + instagramUserSummary.getUsername()));
                }

                userRepository.saveAll(users);
                searchUsers.addAll(getNewSearchUsers(users));
                followerRepository.saveAll(followers);
                logger.error("WORKER NAME = " + instagram.getUsername());
            } catch (InterruptedException e) {
                logger.error("close worker", e);
                searchUsers.add(searchUser);
                Thread.currentThread().interrupt();
            }
            catch (Exception e) {
                logger.error("socket exception", e);
            }
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

    private List<UserDTO> getNewSearchUsers(List<User> users) {
        return users.stream().map(u -> new UserDTO(u.getId(), u.getUserName()))
                .collect(Collectors.toList());
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

                    for(int i = 0; i < 1000000;i++) {
                        System.out.println(i + " i ");
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
