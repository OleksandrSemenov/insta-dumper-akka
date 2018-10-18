package scanner.actors;

import akka.actor.AbstractActor;

public class FollowerActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder().build();
    }
}
