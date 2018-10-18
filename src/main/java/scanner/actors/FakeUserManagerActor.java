package scanner.actors;

import akka.actor.AbstractActor;

public class FakeUserManagerActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder().build();
    }
}
