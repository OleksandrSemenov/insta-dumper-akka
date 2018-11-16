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
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import scanner.ClusterService;
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
    private ActorSystem system;
    private final int TOTAL_INSTANCES = 100;
    private final boolean ALLOW_LOCAL_ROUTEES = true;
    private ClusterService clusterService;

    public Config(ClusterService clusterService){
        this.clusterService = clusterService;
    }

    @Bean
    public ActorSystem actorSystem(@Value("${ports}") String[] ports){
        ActorSystem system = null;

        for(String port : ports){
            system = clusterService.startNode(port);
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
