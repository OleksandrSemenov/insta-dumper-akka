package scanner.actors.messages;

import akka.actor.ActorRef;

public class TransferScannerActorMsg {
    private ActorRef scannerActor;

    public TransferScannerActorMsg(ActorRef scannerActor) {
        this.scannerActor = scannerActor;
    }

    public ActorRef getScannerActor() {
        return scannerActor;
    }

    public void setScannerActor(ActorRef scannerActor) {
        this.scannerActor = scannerActor;
    }
}
