package scanner.dao.interfaces;

import scanner.entities.User;

public interface UsersDao {
    void add(User user);
    boolean isExist(String userName);
}
