package scanner.dao;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import scanner.dao.interfaces.SearchStatesDao;
import scanner.entities.SearchState;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

@Transactional
public class SearchStatesDaoImpl implements SearchStatesDao {
    @Autowired
    private EntityManager entityManager;
    private final Logger logger = Logger.getLogger(SearchStatesDaoImpl.class);

    @Override
    public SearchState getFirstAvailable() {
        try {
            return (SearchState) getSession().createQuery("SELECT s FROM SearchState s WHERE s.isScanned = false").setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            logger.error(e);
        }

        return null;
    }

    @Override
    public void update(SearchState searchState) {
        getSession().update(searchState);
    }

    @Override
    public void add(SearchState searchState) {
        getSession().save(searchState);
    }

    @Override
    public boolean isExist(String userName) {
        SearchState searchState = (SearchState) getSession().createQuery("SELECT s FROM SearchState s WHERE s.userName = :userName")
                .setParameter("userName", userName).setMaxResults(1).uniqueResult();

        return searchState != null ? true : false;
    }

    @Override
    public void delete(SearchState searchState) {
        Session session = getSession();
        session.delete(session.contains(searchState) ? searchState : session.merge(searchState));
    }

    private Session getSession() {
        return entityManager.unwrap(Session.class);
    }
}
