package scanner.dao.interfaces;

import scanner.entities.FakeUser;

import java.util.List;

public interface FakeUsersDao {
    FakeUser getFirstAvailable();
    void update(FakeUser fakeUser);
    List<FakeUser> getAll();
}
