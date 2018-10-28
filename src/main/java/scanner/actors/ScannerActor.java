package scanner.actors;

import akka.actor.*;
import akka.routing.ActorRefRoutee;
import akka.routing.BalancingRoutingLogic;
import akka.routing.BroadcastRoutingLogic;
import akka.routing.Router;
import akka.util.Timeout;
import org.apache.log4j.Logger;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scanner.actors.messages.*;

import java.util.concurrent.TimeUnit;

import static scanner.config.SpringExtension.SPRING_EXTENSION_PROVIDER;

public class ScannerActor extends AbstractActor {
    private final Logger logger = Logger.getLogger(ScannerActor.class);
    private Router workerRouter = new Router(new BalancingRoutingLogic());
    private Router followerRouter = new Router(new BalancingRoutingLogic());
    private Router workerRouterBroadcast = new Router(new BroadcastRoutingLogic()); // Need update ref to follower router in all workers, i think because router immutable
    private ActorRef fakeUserManagerActor;
    public static final String FAKE_USER_MANAGER_ACTOR_PATH = "user/scanner/fakeUserManager";
    public static final String SCANNER_ACTOR_PATH = "user/scanner";

    public ScannerActor(){
        fakeUserManagerActor = getContext().actorOf(Props.create(FakeUserManagerActor.class), "fakeUserManager");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(AddFakeUserMsg.class, fakeUserMsg -> {
            ActorRefRoutee worker = ActorRefRoutee.apply(getContext().actorOf(SPRING_EXTENSION_PROVIDER.get(getContext().system())
                    .props("workerActor"), "worker"));
            workerRouter = workerRouter.addRoutee(worker);
            workerRouterBroadcast = workerRouterBroadcast.addRoutee(worker);
            ActorRefRoutee follower = ActorRefRoutee.apply(getContext().actorOf(SPRING_EXTENSION_PROVIDER.get(getContext().system())
                    .props("followerActor"), "follower"));
            followerRouter = followerRouter.addRoutee(follower);
            logger.info("added new workerActor " + fakeUserMsg.getUserName());
            worker.send(fakeUserMsg, self());
            workerRouterBroadcast.route(new TransferFollowersRouterMsg(followerRouter), self());
        }).match(ScanUserProfileMsg.class, scanUserProfileMsg -> {
            workerRouter.route(scanUserProfileMsg, self());
        }).match(SimpleMessages.LOGIN_FAILED.getClass(), message -> {
            workerRouter = workerRouter.removeRoutee(getSender());
            logger.error("removing routee");
        }).match(LoginSuccessfulMsg.class, loginSuccessfulMsg -> {
            fakeUserManagerActor.tell(loginSuccessfulMsg, getSelf());
        }).match(ScanUserFollowerMsg.class, scanUserFollowerMsg -> {
            followerRouter.route(scanUserFollowerMsg, self());
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
