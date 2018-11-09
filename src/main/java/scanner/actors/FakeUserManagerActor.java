package scanner.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import org.apache.log4j.Logger;
import org.brunocvcunha.instagram4j.Instagram4j;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scanner.MyInstagram4j;
import scanner.actors.messages.LoginSuccessfulMsg;
import scanner.actors.messages.ResultGetFreeFakeUserMsg;
import scanner.actors.messages.SetFreeFakeUserMsg;
import scanner.actors.messages.SimpleMessages;
import scanner.dto.Instagram4jDTO;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class FakeUserManagerActor extends AbstractActor {
    private final Logger logger = Logger.getLogger(FakeUserManagerActor.class);
    private ConcurrentHashMap<MyInstagram4j, Boolean> fakeUsers = new ConcurrentHashMap<>();

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(LoginSuccessfulMsg.class, loginSuccessfulMsg -> {
            fakeUsers.put(MyInstagram4j.fromDto(loginSuccessfulMsg.getInstagram()), true);
            logger.info("Set new fake instagram " + loginSuccessfulMsg.getInstagram().getUsername());
        }).match(SimpleMessages.GET_FREE_FAKE_USER.getClass(), msg -> {
            if(!fakeUsers.containsValue(true)){
                getSender().tell(new ResultGetFreeFakeUserMsg(null), getSelf());
            }

            for(Map.Entry<MyInstagram4j, Boolean> fakeUser : fakeUsers.entrySet()){
                if(fakeUser.getValue() == true){
                    fakeUser.setValue(false);
                    getSender().tell(new ResultGetFreeFakeUserMsg(fakeUser.getKey().getDto()), getSelf());
                    break;
                }
            }
        }).match(SetFreeFakeUserMsg.class, setFreeFakeUserMsg -> {
            fakeUsers.put(MyInstagram4j.fromDto(setFreeFakeUserMsg.getInstagram()), true);
        }).match(String.class, str -> {
            System.out.println("STR " + str);
        }).build();
    }

    public static Instagram4jDTO getFreeFakeUser(ActorRef fakeUserManagerActor){
        ResultGetFreeFakeUserMsg result = new ResultGetFreeFakeUserMsg(null);

        do {
            Timeout timeout = new Timeout(5, TimeUnit.SECONDS);
            Future<Object> future = Patterns.ask(fakeUserManagerActor, SimpleMessages.GET_FREE_FAKE_USER, timeout);

            try {
                result = (ResultGetFreeFakeUserMsg) Await.result(future, timeout.duration());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }while (result.getInstagram() == null);

        return result.getInstagram();
    }
}
