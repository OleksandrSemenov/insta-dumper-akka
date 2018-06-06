package scanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scanner.entities.Follower;

public interface FollowerRepository extends JpaRepository<Follower, Integer> {
}
