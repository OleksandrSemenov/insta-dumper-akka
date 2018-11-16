package scanner;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.apache.http.HttpHost;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchUsernameResult;
import org.junit.Assert;
import org.junit.Test;
import org.objenesis.strategy.StdInstantiatorStrategy;
import scanner.MyInstagram4j;
import scanner.dto.Instagram4jDTO;

import java.io.*;

public class MyInstagram4jTest {
    @Test
    public void testSerialization(){
        MyInstagram4j instagram = new MyInstagram4j("vasyarogov1959", "badalandabadec");
        MyInstagram4j deserialize = null;
        Instagram4jDTO dto = null;
        instagram.setup();

        try {
            instagram.login();
            InstagramSearchUsernameResult resutUser = instagram.sendRequest(new InstagramSearchUsernameRequest("ukraine"));
            System.out.println(resutUser.getUser().username);
            Assert.assertEquals("ukraine", resutUser.getUser().username);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("Serialize");
            FileOutputStream file = new FileOutputStream
                    ("instagram4jdto.txt");
            ObjectOutputStream out = new ObjectOutputStream
                    (file);
            dto = instagram.getDto();
            out.writeObject(dto);
            out.close();
            file.close();

            System.out.println("Deserialize");
            FileInputStream file2 = new FileInputStream
                    ("instagram4jdto.txt");
            ObjectInputStream in = new ObjectInputStream
                    (file2);
            Instagram4jDTO deserializeDTO = (Instagram4jDTO) in.readObject();
            deserialize = MyInstagram4j.fromDto(deserializeDTO);
            in.close();
            file.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        InstagramSearchUsernameResult resutUser = null;

        try {
            resutUser = deserialize.sendRequest(new InstagramSearchUsernameRequest("ukraine"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(resutUser.getUser().username);
        Assert.assertEquals("ukraine", resutUser.getUser().username);
    }
}
