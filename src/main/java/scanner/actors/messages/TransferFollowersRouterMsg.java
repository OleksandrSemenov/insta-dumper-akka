package scanner.actors.messages;

import akka.routing.Router;

import java.io.Serializable;

public class TransferFollowersRouterMsg implements Serializable{
    private Router followersRouter;

    public TransferFollowersRouterMsg(Router followersRouter) {
        this.followersRouter = followersRouter;
    }

    public Router getFollowersRouter() {
        return followersRouter;
    }

    public void setFollowersRouter(Router followersRouter) {
        this.followersRouter = followersRouter;
    }
}
