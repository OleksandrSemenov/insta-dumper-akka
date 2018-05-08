package scanner;

import org.apache.log4j.Logger;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchUsernameResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUser;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUserSummary;
import org.springframework.beans.factory.annotation.Autowired;
import scanner.entities.SearchState;
import scanner.entities.User;
import scanner.repository.SearchStateRepository;
import scanner.repository.UserRepository;

import java.io.IOException;
import java.util.List;

public class FakeUserWorker extends Thread {
    private Instagram4j instagram;
    @Autowired
    private SearchStateRepository searchStateRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SearchStateManager manageSearchState;
    private final int SLEEP_ONE_SECOND = 1000;
    private List<InstagramUserSummary> users;
    private final Logger logger = Logger.getLogger(FakeUserWorker.class);

    public FakeUserWorker() {}

    public FakeUserWorker(Instagram4j instagram4j) {
        this.instagram = instagram4j;
    }

    @Override
    public void run() {
        try {
            login();
        } catch (IOException e) {
            logger.error("login failed", e);
            return;
        }

        while (true) {
            try {
                do {
                    users = manageSearchState.getUsers(instagram);
                } while (users == null);
                for (InstagramUserSummary user : users) {
                    InstagramSearchUsernameResult follower = getUser(user.getUsername());
                    InstagramUser instagramUser = follower.getUser();
                    addSearchState(instagramUser);
                    addUser(instagramUser);
                    wait(SLEEP_ONE_SECOND);
                }

                logger.error("   ---Work done --- " + users.size());
            } catch (Exception e) {
                logger.error("socket exception", e);
            }
        }
    }

    private InstagramSearchUsernameResult getUser(String userName) {
        InstagramSearchUsernameResult resutUser = null;
        try {
            resutUser = instagram.sendRequest(new InstagramSearchUsernameRequest(userName));
        } catch (IOException e) {
            logger.error("", e);
        }

        return resutUser;
    }

    private void addUser(InstagramUser instagramUser) {
        if (!userRepository.existsByUserName(instagramUser.username)) {
            userRepository.save(new User(instagramUser.username,
                    instagramUser.full_name,
                    instagramUser.public_email,
                    instagramUser.public_phone_number,
                    instagramUser.profile_pic_url,
                    instagramUser.biography,
                    instagramUser.city_name,
                    instagramUser.address_street,
                    instagramUser.public_phone_country_code));
        }
    }

    private void addSearchState(InstagramUser instagramUser) {
        if (!searchStateRepository.existsByUserName(instagramUser.username)) {
            searchStateRepository.save(new SearchState(instagramUser.username, null, false));
        }
    }

    private void wait(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            logger.error("wait failed", e);
        }
    }

    private void login() throws IOException {
        instagram.setup();
        instagram.login();
    }
}
