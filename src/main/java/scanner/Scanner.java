package scanner;

import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramGetUserFollowersRequest;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramGetUserFollowersResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchUsernameResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUser;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUserSummary;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import scanner.dao.interfaces.FakeUsersDao;
import scanner.dao.interfaces.SearchStatesDao;
import scanner.dao.interfaces.UsersDao;
import scanner.entities.FakeUser;
import scanner.entities.SearchState;
import scanner.entities.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Scanner {
    @Autowired
    private BeanFactory beanFactory;
    @Autowired
    private FakeUsersDao fakeUsersDao;
    List<FakeUser> fakeUsers;

    public Scanner() {
        fakeUsers = new ArrayList<>();
    }

    public void scan() {
        fakeUsers = fakeUsersDao.getAll();

        for (int i = 0; i < fakeUsers.size(); i++) {
            Instagram4j instagram4j = beanFactory.getBean(Instagram4j.class, fakeUsers.get(i).getUserName(), fakeUsers.get(i).getPassword());
            FakeUserWorker fakeUserWorker = beanFactory.getBean(FakeUserWorker.class, instagram4j);
            fakeUserWorker.start();
        }
    }

}
