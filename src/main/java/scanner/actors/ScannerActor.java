package scanner.actors;

import akka.actor.*;
import akka.cluster.singleton.ClusterSingletonProxy;
import akka.cluster.singleton.ClusterSingletonProxySettings;
import akka.remote.RemoteActorRefProvider;
import akka.routing.ActorRefRoutee;
import akka.routing.BalancingRoutingLogic;
import akka.routing.BroadcastRoutingLogic;
import akka.routing.Router;
import akka.util.Timeout;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scanner.actors.messages.*;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static scanner.config.SpringExtension.SPRING_EXTENSION_PROVIDER;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ScannerActor extends AbstractActor {
    private final Logger logger = Logger.getLogger(ScannerActor.class);
    @Autowired
    @Qualifier("workerRouter")
    private ActorRef workerRouter;
    @Autowired
    @Qualifier("followerRouter")
    private ActorRef followerRouter;
    private ActorRef fakeUserManagerActor;
    public ScannerActor(){
        ClusterSingletonProxySettings proxySettings =
                ClusterSingletonProxySettings.create(getContext().getSystem());
         fakeUserManagerActor =
                getContext().getSystem().actorOf(ClusterSingletonProxy.props("/user/fakeUserManager", proxySettings),
                        "fakeUserManagerProxy" + UUID.randomUUID());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(AddFakeUserMsg.class, fakeUserMsg -> {
            ActorRef worker = getContext().actorOf(SPRING_EXTENSION_PROVIDER.get(getContext().system())
                    .props("workerActor"), "worker");
            getContext().actorOf(SPRING_EXTENSION_PROVIDER.get(getContext().system())
                    .props("followerActor"), "follower");
            logger.info("added new workerActor " + fakeUserMsg.getUserName());
            worker.tell(fakeUserMsg, self());
        }).match(ScanUserProfileMsg.class, scanUserProfileMsg -> {
            workerRouter.tell(scanUserProfileMsg, self());
        }).match(SimpleMessages.LOGIN_FAILED.getClass(), message -> {
            getSender().tell(PoisonPill.getInstance(), ActorRef.noSender());
            logger.error("removing routee");
        }).match(LoginSuccessfulMsg.class, loginSuccessfulMsg -> {
            fakeUserManagerActor.tell(loginSuccessfulMsg, getSelf());
        }).match(ScanUserFollowerMsg.class, scanUserFollowerMsg -> {
            followerRouter.tell(scanUserFollowerMsg, self());
        }).build();
    }

    public static ActorRef getActor(ActorSystem system, String path){
        ActorSelection sel = system.actorSelection(path);
        Timeout timeout = new Timeout(1, TimeUnit.SECONDS);
        Future<ActorRef> fut = sel.resolveOne(timeout);
        ActorRef actor = null;

        try {
            actor = Await.result(fut, timeout.duration());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return actor;
    }
}
