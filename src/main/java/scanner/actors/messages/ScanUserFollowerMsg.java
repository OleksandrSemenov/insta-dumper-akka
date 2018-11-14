package scanner.actors.messages;

import scanner.entities.User;

import java.io.Serializable;

public class ScanUserFollowerMsg implements Serializable{
    private User entityUser;

    public ScanUserFollowerMsg(User entityUser) {
        this.entityUser = entityUser;
    }

    public User getEntityUser() {
        return entityUser;
    }

    public void setEntityUser(User entityUser) {
        this.entityUser = entityUser;
    }
}
