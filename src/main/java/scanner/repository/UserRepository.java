package scanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scanner.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUserName(String userName);
}
