package scanner.dto;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Instagram4jDTO implements Serializable{
    private String deviceId;
    private String uuid;
    private String advertisingId;
    private String username;
    private String password;
    private HttpHost proxy;
    private long userId;
    private String rankToken;
    private boolean isLoggedIn;
    private boolean debug;
    private Map<String, String> cookies;
    private String identifier;
    private String verificationCode;
    private String challengeUrl;

    public Instagram4jDTO(String deviceId, String uuid, String advertisingId, String username, String password,
                          HttpHost proxy, long userId, String rankToken, boolean isLoggedIn,
                          boolean debug, Map<String, String> cookies, String identifier, String verificationCode, String challengeUrl) {
        this.deviceId = deviceId;
        this.uuid = uuid;
        this.advertisingId = advertisingId;
        this.username = username;
        this.password = password;
        this.proxy = proxy;
        this.userId = userId;
        this.rankToken = rankToken;
        this.isLoggedIn = isLoggedIn;
        this.debug = debug;
        this.identifier = identifier;
        this.verificationCode = verificationCode;
        this.challengeUrl = challengeUrl;
        this.cookies = cookies;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getUuid() {
        return uuid;
    }

    public String getAdvertisingId() {
        return advertisingId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public HttpHost getProxy() {
        return proxy;
    }

    public long getUserId() {
        return userId;
    }

    public String getRankToken() {
        return rankToken;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public boolean isDebug() {
        return debug;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public String getChallengeUrl() {
        return challengeUrl;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }
}
