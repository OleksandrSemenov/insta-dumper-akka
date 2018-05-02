package scanner.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "SearchStates")
public class SearchState implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "userName", nullable = false)
    private String userName;
    @Column(name = "nextMaxId")
    private String nextMaxId;
    @Column(name = "isScanned")
    private boolean isScanned;

    public SearchState() {}

    public SearchState(String userName, String nextMaxId, boolean isScanned) {
        this.userName = userName;
        this.nextMaxId = nextMaxId;
        this.isScanned = isScanned;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNextMaxId() {
        return nextMaxId;
    }

    public void setNextMaxId(String nextMaxId) {
        this.nextMaxId = nextMaxId;
    }

    public boolean isScanned() {
        return isScanned;
    }

    public void setScanned(boolean scanned) {
        isScanned = scanned;
    }
}
