package scanner.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Followers")
public class Follower implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name ="userId", nullable = false)
    private User user;
    @Column(nullable = false)
    private String userName;

    public Follower() {}

    public Follower(User user, String userName) {
        this.user = user;
        this.userName = userName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
