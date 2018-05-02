package scanner.dao;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import scanner.dao.interfaces.FakeUsersDao;
import scanner.entities.FakeUser;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@Transactional
public class FakeUsersDaoImpl implements FakeUsersDao {
    @Autowired
    private EntityManager entityManager;

    @Override
    public FakeUser getFirstAvailable() {
        return (FakeUser)getSession().createQuery("SELECT f FROM FakeUser f WHERE f.isActive = false").setMaxResults(1).uniqueResult();
    }

    @Override
    public void update(FakeUser fakeUser) {
        getSession().update(fakeUser);
    }

    @Override
    public List<FakeUser> getAll() {
        return getSession().createQuery("SELECT f from FakeUser f").list();
    }

    private Session getSession() {
        return entityManager.unwrap(Session.class);
    }
}
