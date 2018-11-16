package scanner;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.singleton.ClusterSingletonManager;
import akka.cluster.singleton.ClusterSingletonManagerSettings;
import com.typesafe.config.ConfigFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import scanner.actors.FakeUserManagerActor;

import static scanner.config.SpringExtension.SPRING_EXTENSION_PROVIDER;

@Service
public class ClusterService {
    @Autowired
    private ApplicationContext applicationContext;

    public ActorSystem startNode(String port){
        ActorSystem system;
        com.typesafe.config.Config config = ConfigFactory.parseString(
                "akka.remote.netty.tcp.port=" + port + "\n" +
                        "akka.remote.artery.canonical.port=" + port)
                .withFallback(ConfigFactory.load());

        system = ActorSystem.create("dump-system", config);
        System.out.println("SYSTEM " + system.provider().getDefaultAddress().toString());

        SPRING_EXTENSION_PROVIDER.get(system)
                .initialize(applicationContext);

        system.actorOf(SPRING_EXTENSION_PROVIDER.get(system)
                .props("scannerActor"), "scanner");

        final ClusterSingletonManagerSettings settings =
                ClusterSingletonManagerSettings.create(system);

        system.actorOf(
                ClusterSingletonManager.props(
                        Props.create(FakeUserManagerActor.class),
                        null,
                        settings),
                "fakeUserManager");

        return system;
    }
}
