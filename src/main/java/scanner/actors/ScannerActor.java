package scanner.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.ActorRefRoutee;
import akka.routing.BalancingRoutingLogic;
import akka.routing.BroadcastRoutingLogic;
import akka.routing.Router;
import org.apache.log4j.Logger;
import org.brunocvcunha.instagram4j.Instagram4j;
import scanner.actors.messages.*;
import scanner.dto.FakeUserDTO;
import scanner.dto.UserDTO;

import static scanner.config.SpringExtension.SPRING_EXTENSION_PROVIDER;

public class ScannerActor extends AbstractActor {
    private final Logger logger = Logger.getLogger(ScannerActor.class);
    private Router workerRouter = new Router(new BalancingRoutingLogic());
    private Router followerRouter = new Router(new BalancingRoutingLogic());
    private Router workerRouterBroadcast = new Router(new BroadcastRoutingLogic()); // Need update ref to follower router in all workers, i think because router immutable
    private ActorRef fakeUserManagerActor;

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
            follower.send(new TransferFakeUserManagerActorMsg(fakeUserManagerActor), self());
            follower.send(new TransferScannerActorMsg(getSelf()), getSelf());
            logger.info("added new workerActor " + fakeUserMsg.getUserName());
            worker.send(fakeUserMsg, self());
            worker.send(new TransferFakeUserManagerActorMsg(fakeUserManagerActor), self());
            workerRouterBroadcast.route(new TransferFollowersRouterMsg(followerRouter), self());
        }).match(ScanUserMsg.class, scanUserMsg -> {
            workerRouter.route(scanUserMsg, self());
        }).match(SimpleMessages.LOGIN_FAILED.getClass(), message -> {
            workerRouter = workerRouter.removeRoutee(getSender());
            logger.error("removing routee");
        }).match(LoginSuccessfulMsg.class, loginSuccessfulMsg -> {
            fakeUserManagerActor.tell(loginSuccessfulMsg, getSelf());
        }).build();
    }
}
