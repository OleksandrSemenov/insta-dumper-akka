package scanner.actors.messages;

import java.io.Serializable;

public class ScanUserProfileMsg implements Serializable{
    private int id;
    private String userName;

    public ScanUserProfileMsg(int id, String userName) {
        this.id = id;
        this.userName = userName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
