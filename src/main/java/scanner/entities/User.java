package scanner.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "userName")
    private String userName;
    @Column(name = "fullName")
    private String fullName;
    @Column(name = "email")
    private String email;
    @Column(name = "phoneNumber")
    private String phoneNumber;
    @Column(name = "avatarUrl")
    private String avatarUrl;
    @Column(name = "biography", length = 999)
    private String biography;
    @Column(name = "location")
    private String location;
    @Column(name = "street", length = 999)
    private String street;
    @Column(name = "phoneCountryCode")
    private String phoneCountryCode;

    public User() {}

    public User(String userName, String fullName, String email, String phoneNumber, String avatarUrl, String biography, String location, String street, String phoneCountryCode) {
        this.userName = userName;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.avatarUrl = avatarUrl;
        this.biography = biography;
        this.location = location;
        this.street = street;
        this.phoneCountryCode = phoneCountryCode;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPhoneCountryCode() {
        return phoneCountryCode;
    }

    public void setPhoneCountryCode(String phoneCountryCode) {
        this.phoneCountryCode = phoneCountryCode;
    }
}
