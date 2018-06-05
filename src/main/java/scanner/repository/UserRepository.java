package scanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import scanner.entities.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUserName(String userName);
    User findByUserName(String userName);
    User findByEmail(String email);
    User findByPhoneCountryCodeAndPhoneNumber(String code, String phone);
    List<User> findByIsScannedFalse();
    @Query("select u from User u where fts('simple', u.fullName, :name) = true")
    List<User> findbyFullName(@Param("name")String name);
}
