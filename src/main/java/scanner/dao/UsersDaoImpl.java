package scanner.dao;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import scanner.dao.interfaces.UsersDao;
import scanner.entities.User;

import javax.persistence.EntityManager;
import java.sql.SQLException;

@Transactional
public class UsersDaoImpl implements UsersDao {
    @Autowired
    private EntityManager entityManager;

    @Override
    public void add(User user) {
        System.out.println(user.getAvatarUrl() + " URL " + user.getAvatarUrl().length());
        System.out.println(user.getBiography() + " BIOGRAPHY " + user.getBiography().length());
        System.out.println(user.getLocation() + "LOCATION " + user.getLocation());
        System.out.println(user.getStreet() + "STREET " + user.getStreet());
        getSession().save(user);
    }

    @Override
    public boolean isExist(String userName) {
        User user = (User) getSession().createQuery("SELECT u FROM User u WHERE u.userName = :userName")
                .setParameter("userName", userName).uniqueResult();

        return user != null ? true : false;
    }

    private Session getSession() {
        return entityManager.unwrap(Session.class);
    }
}
