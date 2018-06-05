package scanner.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String userName;
    @Column
    private String fullName;
    @Column
    private String email;
    @Column
    private String phoneNumber;
    @Column
    private String avatarUrl;
    @Column(length = 999)
    private String biography;
    @Column
    private String location;
    @Column(length = 999)
    private String street;
    @Column
    private String phoneCountryCode;
    @Column
    private String businessContactMethod;
    @Column
    private String directMessaging;
    @Column
    private String externalLynxUrl;
    @Column
    private String externalUrl;
    @Column
    private int followerCount;
    @Column
    private int followingCount;
    @Column
    private int geoMediaCount;
    @Column
    private boolean hasAnonymousProfilePicture;
    @Column
    private boolean hasBiographyTranslation;
    @Column
    private boolean hasChaining;
    @Column
    private String hdProfilePicUrl;
    @Column(length = 1000)
    private String hdProfilePicVersions;
    @Column
    private boolean isBusiness;
    @Column
    private boolean isPrivate;
    @Column
    private boolean isVerified;
    @Column
    private float latitude;
    @Column
    private float longitude;
    @Column
    private int mediaCount;
    @Column
    private long pk;
    @Column
    private String profilePicId;
    @Column
    private int userTagsCount;
    @Column
    private String zip;

    public User() {}

    public User(String userName, String fullName, String email, String phoneNumber, String avatarUrl, String biography, String location, String street,
                String phoneCountryCode, String businessContactMethod, String directMessaging, String externalLynxUrl, String externalUrl, int followerCount,
                int followingCount, int geoMediaCount, boolean hasAnonymousProfilePicture, boolean hasBiographyTranslation, boolean hasChaining,
                String hdProfilePicUrlInfo, String hdProfilePicVersions, boolean isBusiness, boolean isPrivate, boolean isVerified, float latitude, float longitude,
                int mediaCount, long pk, String profilePicId, int userTagsCount, String zip) {
        this.userName = userName;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.avatarUrl = avatarUrl;
        this.biography = biography;
        this.location = location;
        this.street = street;
        this.phoneCountryCode = phoneCountryCode;
        this.businessContactMethod = businessContactMethod;
        this.directMessaging = directMessaging;
        this.externalLynxUrl = externalLynxUrl;
        this.externalUrl = externalUrl;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.geoMediaCount = geoMediaCount;
        this.hasAnonymousProfilePicture = hasAnonymousProfilePicture;
        this.hasBiographyTranslation = hasBiographyTranslation;
        this.hasChaining = hasChaining;
        this.hdProfilePicUrl = hdProfilePicUrlInfo;
        this.hdProfilePicVersions = hdProfilePicVersions;
        this.isBusiness = isBusiness;
        this.isPrivate = isPrivate;
        this.isVerified = isVerified;
        this.latitude = latitude;
        this.longitude = longitude;
        this.mediaCount = mediaCount;
        this.pk = pk;
        this.profilePicId = profilePicId;
        this.userTagsCount = userTagsCount;
        this.zip = zip;
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

    public String getBusinessContactMethod() {
        return businessContactMethod;
    }

    public void setBusinessContactMethod(String businessContactMethod) {
        this.businessContactMethod = businessContactMethod;
    }

    public String getDirectMessaging() {
        return directMessaging;
    }

    public void setDirectMessaging(String directMessaging) {
        this.directMessaging = directMessaging;
    }

    public String getExternalLynxUrl() {
        return externalLynxUrl;
    }

    public void setExternalLynxUrl(String externalLynxUrl) {
        this.externalLynxUrl = externalLynxUrl;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public int getGeoMediaCount() {
        return geoMediaCount;
    }

    public void setGeoMediaCount(int geoMediaCount) {
        this.geoMediaCount = geoMediaCount;
    }

    public boolean isHasAnonymousProfilePicture() {
        return hasAnonymousProfilePicture;
    }

    public void setHasAnonymousProfilePicture(boolean hasAnonymousProfilePicture) {
        this.hasAnonymousProfilePicture = hasAnonymousProfilePicture;
    }

    public boolean isHasBiographyTranslation() {
        return hasBiographyTranslation;
    }

    public void setHasBiographyTranslation(boolean hasBiographyTranslation) {
        this.hasBiographyTranslation = hasBiographyTranslation;
    }

    public boolean isHasChaining() {
        return hasChaining;
    }

    public void setHasChaining(boolean hasChaining) {
        this.hasChaining = hasChaining;
    }

    public String getHdProfilePicUrl() {
        return hdProfilePicUrl;
    }

    public void setHdProfilePicUrl(String hdProfilePicUrl) {
        this.hdProfilePicUrl = hdProfilePicUrl;
    }

    public String getHdProfilePicVersions() {
        return hdProfilePicVersions;
    }

    public void setHdProfilePicVersions(String hdProfilePicVersions) {
        this.hdProfilePicVersions = hdProfilePicVersions;
    }

    public boolean isBusiness() {
        return isBusiness;
    }

    public void setBusiness(boolean business) {
        isBusiness = business;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public int getMediaCount() {
        return mediaCount;
    }

    public void setMediaCount(int mediaCount) {
        this.mediaCount = mediaCount;
    }

    public long getPk() {
        return pk;
    }

    public void setPk(long pk) {
        this.pk = pk;
    }

    public String getProfilePicId() {
        return profilePicId;
    }

    public void setProfilePicId(String profilePicId) {
        this.profilePicId = profilePicId;
    }

    public int getUserTagsCount() {
        return userTagsCount;
    }

    public void setUserTagsCount(int userTagsCount) {
        this.userTagsCount = userTagsCount;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}
