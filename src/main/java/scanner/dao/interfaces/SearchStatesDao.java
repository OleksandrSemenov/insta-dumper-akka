package scanner.dao.interfaces;

import scanner.entities.SearchState;

public interface SearchStatesDao {
    SearchState getFirstAvailable();
    void update(SearchState searchState);
    void add(SearchState searchState);
    boolean isExist(String userName);
    void delete(SearchState id);
}
