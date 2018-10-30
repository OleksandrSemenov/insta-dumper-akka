package scanner.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUser;
import scala.Int;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

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
    @Lob
    private String biography;
    @Column
    private String location;
    @Lob
    private String street;
    @Column
    private String phoneCountryCode;
    @Column
    private String businessContactMethod;
    @Column
    private String directMessaging;
    @Lob
    private String externalLynxUrl;
    @Lob
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
    @Lob
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

    @Enumerated(EnumType.ORDINAL)
    private ScanStatus scanStatus;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Follower> followers;

    public User() {}

    public User(Integer id, String userName){
        this.id = id;
        this.userName = userName;
    }

    public User(String userName, ScanStatus scanStatus) {
        this.userName = userName;
        this.scanStatus = scanStatus;
    }

    public User(String userName, String fullName, String email, String phoneNumber, String avatarUrl, String biography, String location, String street,
                String phoneCountryCode, String businessContactMethod, String directMessaging, String externalLynxUrl, String externalUrl, int followerCount,
                int followingCount, int geoMediaCount, boolean hasAnonymousProfilePicture, boolean hasBiographyTranslation, boolean hasChaining,
                String hdProfilePicUrlInfo, String hdProfilePicVersions, boolean isBusiness, boolean isPrivate, boolean isVerified, float latitude, float longitude,
                int mediaCount, long pk, String profilePicId, int userTagsCount, String zip, ScanStatus scanStatus) {
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
        this.scanStatus = scanStatus;
    }

    public static User instagramUserToUserEntity(InstagramUser instagramUser) {
        String hdAvatarUrl = null;
        String avatarVersions = null;

        if (instagramUser.hd_profile_pic_url_info != null) {
            hdAvatarUrl = instagramUser.hd_profile_pic_url_info.url;
        }

        if (instagramUser.hd_profile_pic_versions != null && !instagramUser.hd_profile_pic_versions.isEmpty()) {
            for (int i = 0; i < instagramUser.hd_profile_pic_versions.size(); i++) {
                avatarVersions += instagramUser.hd_profile_pic_versions.get(i).url;

                if (i != instagramUser.hd_profile_pic_versions.size()) {
                    avatarVersions += ", ";
                }
            }
        }

        return new User(instagramUser.username, instagramUser.full_name, instagramUser.public_email, instagramUser.public_phone_number, instagramUser.profile_pic_url,
                instagramUser.biography, instagramUser.city_name, instagramUser.address_street, instagramUser.public_phone_country_code, instagramUser.business_contact_method,
                instagramUser.direct_messaging, instagramUser.external_lynx_url, instagramUser.external_url, instagramUser.follower_count, instagramUser.following_count,
                instagramUser.geo_media_count, instagramUser.has_anonymous_profile_picture, instagramUser.has_biography_translation, instagramUser.has_chaining,
                hdAvatarUrl, avatarVersions, instagramUser.is_business, instagramUser.is_private,
                instagramUser.is_verified, instagramUser.latitude, instagramUser.longitude, instagramUser.media_count, instagramUser.pk, instagramUser.profile_pic_id,
                instagramUser.usertags_count, instagramUser.zip, ScanStatus.CompleteProfile);
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

    public Set<Follower> getFollowers() {
        return followers;
    }

    public ScanStatus getScanStatus() {
        return scanStatus;
    }

    public void setScanStatus(ScanStatus scanStatus) {
        this.scanStatus = scanStatus;
    }

    public void setFollowers(Set<Follower> followers) {
        this.followers = followers;
    }
}
