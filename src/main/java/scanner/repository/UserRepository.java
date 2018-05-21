package scanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scanner.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUserName(String userName);
    User findByUserName(String userName);
    User findByEmail(String email);
    User findByPhoneCountryCodeAndPhoneNumber(String code, String phone);
}
