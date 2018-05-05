package scanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scanner.entities.FakeUser;

public interface FakeUserRepository extends JpaRepository<FakeUser, Integer> {
}
