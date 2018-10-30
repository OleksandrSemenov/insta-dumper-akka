package scanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import scanner.dto.UserDTO;
import scanner.entities.User;

import java.util.List;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUserName(String userName);
    User findByUserName(String userName);
    User findByEmail(String email);
    User findByPhoneCountryCodeAndPhoneNumber(String code, String phone);
    @Query("select u.userName from User u")
    Set<String> getFoundUsers();
    @Query("select new scanner.dto.UserDTO(u.id, u.userName) from User u where u.scanStatus = 0")
    List<UserDTO> findByIsScannedFalse();
    @Query("select u from User u where fts('simple', u.fullName, :name) = true")
    List<User> findbyFullName(@Param("name")String name);
    @Query(value = "SELECT * FROM users WHERE scan_status = 0 ORDER BY id LIMIT 500", nativeQuery = true)
    List<User> getSearchUsers();

    @Query("select new scanner.dto.UserDTO(u.id, u.userName) from User u where u.scanStatus = 0")
    List<UserDTO> getUsersForScanProfile();

    @Query("select new scanner.dto.UserDTO(u.id, u.userName, u.pk) from User u where u.scanStatus = 1")
    List<UserDTO> getUsersForScanFollowers();
}
