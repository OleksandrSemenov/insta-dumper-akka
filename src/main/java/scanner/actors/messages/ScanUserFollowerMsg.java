package scanner.actors.messages;

import scanner.entities.User;

public class ScanUserFollowerMsg {
    private long intagramId;
    private User entityUser;

    public ScanUserFollowerMsg(long intagramId, User entityUser) {
        this.intagramId = intagramId;
        this.entityUser = entityUser;
    }

    public long getIntagramId() {
        return intagramId;
    }

    public void setIntagramId(long intagramId) {
        this.intagramId = intagramId;
    }

    public User getEntityUser() {
        return entityUser;
    }

    public void setEntityUser(User entityUser) {
        this.entityUser = entityUser;
    }
}
