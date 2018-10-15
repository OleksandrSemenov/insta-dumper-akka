package scanner.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.routing.ActorRefRoutee;
import akka.routing.BalancingRoutingLogic;
import akka.routing.Router;
import org.apache.log4j.Logger;
import org.brunocvcunha.instagram4j.Instagram4j;
import scanner.dto.UserDTO;
import scanner.repository.FollowerRepository;
import scanner.repository.UserRepository;

public class ScannerActor extends AbstractActor {
    private Router workerRouter = new Router(new BalancingRoutingLogic());
    private UserRepository userRepository;
    protected FollowerRepository followerRepository;
    private final Logger logger = Logger.getLogger(ScannerActor.class);

    public ScannerActor(){}

    public ScannerActor(UserRepository userRepository, FollowerRepository followerRepository){
        this.userRepository = userRepository;
        this.followerRepository = followerRepository;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(Instagram4j.class, instagram -> {
            workerRouter = workerRouter.addRoutee(ActorRefRoutee.apply(getContext().actorOf(Props.create(WorkerActor.class, instagram, userRepository, followerRepository), "worker")));
        }).match(UserDTO.class, scanUser -> {
            logger.error("scan mailbox " + scanUser.getUserName());
            workerRouter.route(scanUser, self());
        }).build();
    }
}
