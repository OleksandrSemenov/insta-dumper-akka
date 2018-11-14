package scanner.config;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.routing.ClusterRouterGroup;
import akka.cluster.routing.ClusterRouterGroupSettings;
import akka.cluster.singleton.ClusterSingletonManager;
import akka.cluster.singleton.ClusterSingletonManagerSettings;
import akka.routing.RandomGroup;
import com.esotericsoftware.minlog.Log;
import com.typesafe.config.ConfigFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import scanner.Scanner;
import scanner.actors.FakeUserManagerActor;
import scanner.actors.ScannerActor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static scanner.config.SpringExtension.SPRING_EXTENSION_PROVIDER;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(value = "scanner.repository")
public class Config {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ActorSystem system;

    private final int TOTAL_INSTANCES = 100;
    private final boolean ALLOW_LOCAL_ROUTEES = true;

    @Bean
    public ActorSystem actorSystem(){
        //Log.set(0);
        final String[] ports = new String[] { "2551", "2552", };
        ActorSystem system = null;

        for(String port : ports){
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
        }
        return system;
    }

    @Bean(name = "scannerRouter")
    public ActorRef scannerRouter(){
        Iterable<String> routeesPaths = Collections.singletonList("/user/scanner");
        Set<String> useRoles = new HashSet<>();

        return system.actorOf(
                new ClusterRouterGroup(new RandomGroup(routeesPaths),
                        new ClusterRouterGroupSettings(TOTAL_INSTANCES, routeesPaths,
                                ALLOW_LOCAL_ROUTEES, useRoles)).props(), "scannerRouter");
    }

    @Bean(name = "workerRouter")
    public ActorRef workerRouter(){
        Iterable<String> routeesPaths = Collections.singletonList("/user/scanner/worker");
        Set<String> useRoles = new HashSet<>();

        return system.actorOf(
                new ClusterRouterGroup(new RandomGroup(routeesPaths),
                        new ClusterRouterGroupSettings(TOTAL_INSTANCES, routeesPaths,
                                ALLOW_LOCAL_ROUTEES, useRoles)).props(), "workerRouter");
    }

    @Bean(name = "followerRouter")
    public ActorRef followerRouter(){
        Iterable<String> routeesPaths = Collections.singletonList("/user/scanner/follower");
        Set<String> useRoles = new HashSet<>();

        return system.actorOf(
                new ClusterRouterGroup(new RandomGroup(routeesPaths),
                        new ClusterRouterGroupSettings(TOTAL_INSTANCES, routeesPaths,
                                ALLOW_LOCAL_ROUTEES, useRoles)).props(), "followerRouter");
    }

    @Bean
    public Scanner getScanner() {
        return new Scanner();
    }
}
