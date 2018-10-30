package scanner.actors.messages;

import org.brunocvcunha.instagram4j.Instagram4j;

public class LoginSuccessfulMsg {
    private Instagram4j instagram;

    public LoginSuccessfulMsg(Instagram4j instagram) {
        this.instagram = instagram;
    }

    public Instagram4j getInstagram() {
        return instagram;
    }

    public void setInstagram(Instagram4j instagram) {
        this.instagram = instagram;
    }
}
