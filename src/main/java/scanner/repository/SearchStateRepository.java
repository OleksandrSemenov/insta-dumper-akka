package scanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scanner.entities.SearchState;

public interface SearchStateRepository extends JpaRepository<SearchState, Integer> {
    SearchState findFirstByIsScannedFalseOrderByIdAsc();
    boolean existsByUserName(String userName);
}
