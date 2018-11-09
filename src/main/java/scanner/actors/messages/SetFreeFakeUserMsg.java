package scanner.actors.messages;

import org.brunocvcunha.instagram4j.Instagram4j;
import scanner.MyInstagram4j;
import scanner.dto.Instagram4jDTO;

import java.io.Serializable;

public class SetFreeFakeUserMsg implements Serializable{
    private Instagram4jDTO instagram;

    public SetFreeFakeUserMsg(Instagram4jDTO instagram) {
        this.instagram = instagram;
    }

    public Instagram4jDTO getInstagram() {
        return instagram;
    }

    public void setInstagram(Instagram4jDTO instagram) {
        this.instagram = instagram;
    }
}
