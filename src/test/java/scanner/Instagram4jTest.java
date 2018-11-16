package scanner;

import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchUsernameResult;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class Instagram4jTest {
    @Test
    public void scanUserTest() throws IOException {
        Instagram4j instagram4j = Instagram4j.builder().username("zibrovka@mail.ru").password("zibrovkinVasya").build();
        instagram4j.setup();
        instagram4j.login();

        InstagramSearchUsernameResult resutUser = instagram4j.sendRequest(new InstagramSearchUsernameRequest("ukraine"));
        Assert.assertEquals("ukraine", resutUser.getUser().getUsername());
    }

}
