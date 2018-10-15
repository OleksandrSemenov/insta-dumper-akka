package scanner.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.routing.ActorRefRoutee;
import akka.routing.BalancingRoutingLogic;
import akka.routing.Router;
import org.apache.log4j.Logger;
import org.brunocvcunha.instagram4j.Instagram4j;
import scanner.dto.FakeUserDTO;
import scanner.dto.UserDTO;
import scanner.entities.FakeUser;
import scanner.repository.FollowerRepository;
import scanner.repository.UserRepository;

import static scanner.config.SpringExtension.SPRING_EXTENSION_PROVIDER;

public class ScannerActor extends AbstractActor {
    private Router workerRouter = new Router(new BalancingRoutingLogic());
    private final Logger logger = Logger.getLogger(ScannerActor.class);

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(FakeUserDTO.class, fakeUser -> {
            ActorRefRoutee worker = ActorRefRoutee.apply(getContext().actorOf(SPRING_EXTENSION_PROVIDER.get(getContext().system())
                    .props("workerActor"), "worker"));
            workerRouter = workerRouter.addRoutee(worker);
            worker.send(fakeUser, self());
        }).match(UserDTO.class, scanUser -> {
            workerRouter.route(scanUser, self());
        }).match(Messages.LOGIN_FAILED.getClass(), message -> {
            workerRouter = workerRouter.removeRoutee(getSender());
        }).build();
    }
}
