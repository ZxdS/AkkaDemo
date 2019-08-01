package com.luban.akka.vip.高级.集群;

import akka.actor.*;
import akka.cluster.Cluster;
import akka.cluster.client.ClusterClient;
import akka.cluster.client.ClusterClientReceptionist;
import akka.cluster.routing.ClusterRouterGroup;
import akka.cluster.routing.ClusterRouterGroupSettings;
import akka.cluster.routing.ClusterRouterPool;
import akka.cluster.routing.ClusterRouterPoolSettings;
import akka.dispatch.OnSuccess;
import akka.pattern.Patterns;
import akka.routing.BroadcastGroup;
import akka.routing.RoundRobinPool;
import akka.routing.Router;
import akka.util.Timeout;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * *************书山有路勤为径***************
 * 鲁班学院
 * 往期资料加木兰老师  QQ: 2746251334
 * VIP课程加安其拉老师 QQ: 3164703201
 * 讲师：周瑜老师
 * *************学海无涯苦作舟***************
 */
public class ClusterRouterActor extends AbstractActor {

    private ActorRef router;

    @Override
    public void preStart() throws Exception {
        int totalInstances = 100;
        int maxInstancesPerNode = 5;
        boolean allowLocalRoutees = false;
        String useRole = null;
        router = getContext().actorOf(new ClusterRouterPool(new RoundRobinPool(2),
                new ClusterRouterPoolSettings(totalInstances, maxInstancesPerNode, allowLocalRoutees, useRole)).props(Props.create(UserClusterActor.class)), "poolRouter");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().matchAny(msg -> {
            router.tell(msg, getSender());
        }).build();
    }

    public static void main(String[] args) {
        Config config = ConfigFactory
                .parseString("akka.remote.netty.tcp.port=" + 2553)
                .withFallback(ConfigFactory.load("cluster.conf"));
        ActorSystem system = ActorSystem.create("sys", config);

        ActorRef actorRef = system.actorOf(Props.create(ClusterRouterActor.class), "router");

        Cluster cluster = Cluster.get(system);
        Address address = new Address("akka.tcp", "sys", "127.0.0.1", 2551);
        cluster.join(address);

        ClusterClientReceptionist.get(system).registerService(actorRef);

//        Timeout timeout = new Timeout(Duration.create(2, TimeUnit.SECONDS));
//        for (int i=0; i<100; i++) {
//            Future<Object> f = Patterns.ask(actorRef, "hello", timeout);
//            f.onSuccess(new OnSuccess<Object>() {
//                @Override
//                public void onSuccess(Object result) throws Throwable {
//                    System.out.println( result);
//                }
//            }, system.getDispatcher());
//
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
