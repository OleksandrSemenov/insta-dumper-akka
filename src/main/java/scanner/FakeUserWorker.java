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

public class FakeUserWorker implements Runnable {
    private Instagram4j instagram;
    @Autowired
    private SearchStateRepository searchStateRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SearchStateManager searchStateManager;
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
        } catch (Exception e) {
            logger.error("login failed", e);
            return;
        }

        while (true) {
            try {
                do {
                    users = searchStateManager.getUsers(instagram);
                } while (users == null);
                int count = 0;
                for (InstagramUserSummary user : users) {
                    if(searchStateManager.getFoundUsers().containsKey(user.getUsername())){
                       continue;
                    }
                    count++;
                    InstagramSearchUsernameResult follower = getUser(user.getUsername());
                    InstagramUser instagramUser = follower.getUser();
                    User saveUser = instagramUserToUserEntity(instagramUser);
                    searchStateManager.getFoundUsers().put(user.getUsername(), saveUser);
                    addSearchState(instagramUser);
                    userRepository.save(saveUser);
                    System.out.println(count);
                    wait(SLEEP_ONE_SECOND);
                }

                logger.error("   ---Work done --- " + users.size() + " Added " + count);
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

    private User instagramUserToUserEntity(InstagramUser instagramUser) {
        String hdAvatarUrl = null;
        String avatarVersions = null;

        if(instagramUser.hd_profile_pic_url_info != null) {
            hdAvatarUrl = instagramUser.hd_profile_pic_url_info.url;
        }

        if(instagramUser.hd_profile_pic_versions != null && !instagramUser.hd_profile_pic_versions.isEmpty()) {
            for(int i = 0; i < instagramUser.hd_profile_pic_versions.size(); i++) {
                avatarVersions += instagramUser.hd_profile_pic_versions.get(i).url;

                if(i != instagramUser.hd_profile_pic_versions.size()) {
                    avatarVersions += ", ";
                }
            }
        }

        return new User(instagramUser.username, instagramUser.full_name, instagramUser.public_email, instagramUser.public_phone_number, instagramUser.profile_pic_url,
                instagramUser.biography, instagramUser.city_name, instagramUser.address_street, instagramUser.public_phone_country_code, instagramUser.business_contact_method,
                instagramUser.direct_messaging, instagramUser.external_lynx_url, instagramUser.external_url, instagramUser.follower_count , instagramUser.following_count,
                instagramUser.geo_media_count, instagramUser.has_anonymous_profile_picture, instagramUser.has_biography_translation, instagramUser.has_chaining,
                hdAvatarUrl, avatarVersions, instagramUser.is_business, instagramUser.is_private,
                instagramUser.is_verified, instagramUser.latitude, instagramUser.longitude, instagramUser.media_count, instagramUser.pk, instagramUser.profile_pic_id,
                instagramUser.usertags_count, instagramUser.zip);
    }

    private void addSearchState(InstagramUser instagramUser) {
        searchStateRepository.save(new SearchState(instagramUser.username, null, false));
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
