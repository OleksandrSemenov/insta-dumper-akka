package scanner;

import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.HttpParams;
import org.brunocvcunha.instagram4j.Instagram4j;
import scanner.dto.Instagram4jDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<String, String> cookies = new HashMap<>();

        for(Cookie cookie : this.cookieStore.getCookies()){
            cookies.put(cookie.getName(), cookie.getValue());
        }

        return new Instagram4jDTO(this.deviceId, this.uuid, this.advertisingId, this.username,
                this.password, this.proxy, this.userId, this.rankToken, this.isLoggedIn, this.debug, cookies, this.identifier, this.verificationCode, this.challengeUrl);
    }

    public static MyInstagram4j fromDto(Instagram4jDTO dto){
        return new MyInstagram4j(dto);
    }

    private void setFields(Instagram4jDTO dto){
        this.advertisingId = dto.getAdvertisingId();
        this.challengeUrl = dto.getChallengeUrl();
        this.debug = dto.isDebug();
        this.deviceId = dto.getDeviceId();
        this.identifier = dto.getIdentifier();
        this.isLoggedIn = dto.isLoggedIn();
        this.proxy = dto.getProxy();
        this.rankToken = dto.getRankToken();
        this.userId = dto.getUserId();
        this.uuid = dto.getUuid();
        this.verificationCode = dto.getVerificationCode();

        List<BasicClientCookie> clientCookies = new ArrayList<>();
        for(Map.Entry<String, String> mycookie : dto.getCookies().entrySet()){
            BasicClientCookie cookie = new BasicClientCookie(mycookie.getKey(), mycookie.getValue());
            clientCookies.add(cookie);
        }

        BasicCookieStore cookieStore = new BasicCookieStore();

        for(BasicClientCookie basicCookie : clientCookies){
            cookieStore.addCookie(basicCookie);
        }

        this.cookieStore = cookieStore;
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
