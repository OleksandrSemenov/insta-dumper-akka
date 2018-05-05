package scanner;

import org.apache.log4j.Logger;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramGetUserFollowersRequest;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramGetUserFollowersResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchUsernameResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUserSummary;
import org.springframework.beans.factory.annotation.Autowired;
import scanner.dao.interfaces.SearchStatesDao;
import scanner.entities.SearchState;
import scanner.repository.SearchStateRepository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

public class ManageSearchState {
    private SearchState searchState;
    @Autowired
    private SearchStateRepository searchStateRepository;
    private final Logger logger = Logger.getLogger(ManageSearchState.class);

    public ManageSearchState() { }

    @PostConstruct
    private void init() {
        searchState = searchStateRepository.findFirstByIsScannedFalseOrderByIdAsc();
        System.out.println(searchState.getUserName() + " " + searchState.getId());
    }

    public synchronized List<InstagramUserSummary> getUsers(Instagram4j instagram) {
        logger.info("   --- START ---");
        InstagramSearchUsernameResult scanUser = getUser(searchState.getUserName(), instagram);

        if(scanUser.getUser() == null) {
            logger.error(searchState.getUserName());
            searchStateRepository.delete(searchState);
            searchState = searchStateRepository.findFirstByIsScannedFalseOrderByIdAsc();
            return null;
        }

        InstagramGetUserFollowersResult githubFollowersResult = null;
        githubFollowersResult = getFollowers(scanUser.getUser().pk, searchState.getNextMaxId(), instagram);

        if(githubFollowersResult == null || githubFollowersResult.getUsers() == null) {
            return null;
        }

        List<InstagramUserSummary> users = githubFollowersResult.getUsers();
        searchState.setNextMaxId(githubFollowersResult.getNext_max_id());

        if(searchState.getNextMaxId() == null) {
            searchState.setScanned(true);
            searchStateRepository.save(searchState);
            searchState = searchStateRepository.findFirstByIsScannedFalseOrderByIdAsc();
        } else {
            searchStateRepository.save(searchState);
        }
        logger.info("   --- FINISH ---");
        return users;
    }

    private InstagramSearchUsernameResult getUser(String userName, Instagram4j instagram) {
        InstagramSearchUsernameResult resutUser = null;
        try {
            resutUser = instagram.sendRequest(new InstagramSearchUsernameRequest(userName));
        } catch (IOException e) {
            logger.error("", e);
        }

        return resutUser;
    }

    private InstagramGetUserFollowersResult getFollowers(long pk, String nextMaxId, Instagram4j instagram) {
        InstagramGetUserFollowersResult followersResult = null;
        try {
            followersResult = instagram.sendRequest(new InstagramGetUserFollowersRequest(pk, searchState.getNextMaxId()));
        } catch (IOException e) {
            logger.error("", e);
        }

        return followersResult;
    }
}
