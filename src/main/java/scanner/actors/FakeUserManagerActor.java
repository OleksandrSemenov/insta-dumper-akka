package scanner.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import org.apache.log4j.Logger;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scanner.actors.messages.LoginSuccessfulMsg;
import scanner.actors.messages.ResultGetFreeFakeUserMsg;
import scanner.actors.messages.SetFreeFakeUserMsg;
import scanner.actors.messages.SimpleMessages;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FakeUserManagerActor extends AbstractActor {
    private final Logger logger = Logger.getLogger(FakeUserManagerActor.class);
    private ConcurrentHashMap<Instagram4j, Boolean> fakeUsers = new ConcurrentHashMap<>();

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(LoginSuccessfulMsg.class, loginSuccessfulMsg -> {
            fakeUsers.put(loginSuccessfulMsg.getInstagram(), true);
            logger.info("Set new fake instagram " + loginSuccessfulMsg.getInstagram().getUsername());
        }).match(SimpleMessages.GET_FREE_FAKE_USER.getClass(), msg -> {
            if(!fakeUsers.containsValue(true)){
                getSender().tell(new ResultGetFreeFakeUserMsg(null), getSelf());
            }

            for(Map.Entry<Instagram4j, Boolean> fakeUser : fakeUsers.entrySet()){
                if(fakeUser.getValue() == true){
                    fakeUser.setValue(false);
                    getSender().tell(fakeUser.getKey(), getSelf());
                    break;
                }
            }
        }).match(SetFreeFakeUserMsg.class, setFreeFakeUserMsg -> {
            fakeUsers.put(setFreeFakeUserMsg.getInstagram(), true);
        }).build();
    }

    public static Instagram4j getFreeFakeUser(ActorRef fakeUserManagerActor){
        ResultGetFreeFakeUserMsg result = null;

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
