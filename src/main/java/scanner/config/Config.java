package scanner.config;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.routing.ClusterRouterGroup;
import akka.cluster.routing.ClusterRouterGroupSettings;
import akka.routing.RandomGroup;
import com.typesafe.config.ConfigFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import scanner.Scanner;
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

    @Bean
    public ActorSystem actorSystem(){
        final String[] ports = new String[] { "2551", "2552", };
        ActorSystem system = null;

        for(String port : ports){
            com.typesafe.config.Config config = ConfigFactory.parseString(
                    "akka.remote.netty.tcp.port=" + port + "\n" +
                            "akka.remote.artery.canonical.port=" + port)
                    .withFallback(ConfigFactory.load());

            system = ActorSystem.create("dump-system", config);
            system.actorOf(Props.create(ScannerActor.class), "scanner");
            System.out.println("SYSTEM " + system.provider().getDefaultAddress().toString());
        }

        SPRING_EXTENSION_PROVIDER.get(system)
                .initialize(applicationContext);
        return system;
    }

    @Bean(name = "scannerRouter")
    public ActorRef scannerRouter(){
        final int totalInstances = 100;
        Iterable<String> routeesPaths = Collections.singletonList("/user/scanner");
        boolean allowLocalRoutees = true;
        Set<String> useRoles = new HashSet<>();

        return system.actorOf(
                new ClusterRouterGroup(new RandomGroup(routeesPaths),
                        new ClusterRouterGroupSettings(totalInstances, routeesPaths,
                                allowLocalRoutees, useRoles)).props(), "scannerRouter");
    }

    @Bean
    public Scanner getScanner() {
        return new Scanner();
    }
}
