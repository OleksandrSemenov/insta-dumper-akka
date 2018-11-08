package scanner;

import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.brunocvcunha.instagram4j.Instagram4j;
import scanner.dto.Instagram4jDTO;

import java.io.Serializable;

public class MyInstagram4j extends Instagram4j implements Serializable{
    public MyInstagram4j(Instagram4jDTO dto){
        super(dto.getUsername(), dto.getPassword());
        setFields(dto);
    }

    public MyInstagram4j(String username, String password) {
        super(username, password);
    }

    public MyInstagram4j(String username, String password, long userId, String uuid, CookieStore cookieStore, HttpHost proxy) {
        super(username, password, userId, uuid, cookieStore, proxy);
    }

    public Instagram4jDTO getDto(){
        return new Instagram4jDTO(this.deviceId, this.uuid, this.advertisingId, this.username,
                this.password, this.proxy, this.userId, this.rankToken, this.isLoggedIn
                , this.debug, this.cookieStore, this.identifier, this.verificationCode, this.challengeUrl);
    }

    public static MyInstagram4j fromDto(Instagram4jDTO dto){
        return new MyInstagram4j(dto);
    }

    private void setFields(Instagram4jDTO dto){
        this.advertisingId = dto.getAdvertisingId();
        this.challengeUrl = dto.getChallengeUrl();
        this.cookieStore = dto.getCookieStore();
        this.debug = dto.isDebug();
        this.deviceId = dto.getDeviceId();
        this.identifier = dto.getIdentifier();
        this.isLoggedIn = dto.isLoggedIn();
        this.proxy = dto.getProxy();
        this.rankToken = dto.getRankToken();
        this.userId = dto.getUserId();
        this.uuid = dto.getUuid();
        this.verificationCode = dto.getVerificationCode();

        initDefaultHttpClient();
    }

    private void initDefaultHttpClient(){
        this.client = new DefaultHttpClient();
        HttpParams params = this.client.getParams();
        params.setParameter("http.protocol.cookie-policy", "compatibility");
        if (this.proxy != null) {
            params.setParameter("http.route.default-proxy", this.proxy);
        }

        this.client.setCookieStore(this.cookieStore);
    }
}
