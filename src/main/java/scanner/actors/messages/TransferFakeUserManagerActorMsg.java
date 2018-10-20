package scanner.actors.messages;

import akka.actor.ActorRef;

public class TransferFakeUserManagerActorMsg {
    private ActorRef fakeUserManagerActor;

    public TransferFakeUserManagerActorMsg(ActorRef fakeUserManagerActor) {
        this.fakeUserManagerActor = fakeUserManagerActor;
    }

    public ActorRef getFakeUserManagerActor() {
        return fakeUserManagerActor;
    }

    public void setFakeUserManagerActor(ActorRef fakeUserManagerActor) {
        this.fakeUserManagerActor = fakeUserManagerActor;
    }
}
