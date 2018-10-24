package scanner.dto;

public class UserDTO {
    private int id;
    private String userName;
    private long pk;

    public UserDTO(int id, String userName) {
        this.id = id;
        this.userName = userName;
    }

    public UserDTO(int id, String userName, long pk) {
        this.id = id;
        this.userName = userName;
        this.pk = pk;
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

    public long getPk() {
        return pk;
    }

    public void setPk(long pk) {
        this.pk = pk;
    }
}
