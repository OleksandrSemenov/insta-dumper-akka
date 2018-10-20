package scanner.actors.messages;

import org.brunocvcunha.instagram4j.Instagram4j;

public class SetFreeFakeUserMsg {
    private Instagram4j instagram;

    public SetFreeFakeUserMsg(Instagram4j instagram) {
        this.instagram = instagram;
    }

    public Instagram4j getInstagram() {
        return instagram;
    }

    public void setInstagram(Instagram4j instagram) {
        this.instagram = instagram;
    }
}
