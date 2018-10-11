package scanner.repository;

import org.hibernate.Session;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import scanner.dto.UserDTO;
import scanner.entities.User;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUserName(String userName);
    User findByUserName(String userName);
    User findByEmail(String email);
    User findByPhoneCountryCodeAndPhoneNumber(String code, String phone);
    @Query("select u.userName from User u")
    Set<String> getFoundUsers();
    @Query("select new scanner.dto.UserDTO(u.id, u.userName) from User u where u.isScanned = false")
    List<UserDTO> findByIsScannedFalse();
    @Query("select u from User u where fts('simple', u.fullName, :name) = true")
    List<User> findbyFullName(@Param("name")String name);
    @Query(value = "SELECT * FROM users WHERE is_scanned = FALSE ORDER BY id LIMIT 500", nativeQuery = true)
    List<User> getSearchUsers();
}
