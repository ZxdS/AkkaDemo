package com.luban.akka.vip.高级.集群;

import akka.actor.*;
import akka.cluster.Cluster;
import akka.cluster.client.ClusterClientReceptionist;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * *************书山有路勤为径***************
 * 鲁班学院
 * 往期资料加木兰老师  QQ: 2746251334
 * VIP课程加安其拉老师 QQ: 3164703201
 * 讲师：周瑜老师
 * *************学海无涯苦作舟***************
 */
public class UserClusterActor extends AbstractActor {
    private Cluster cluster = Cluster.get(getContext().system());

    @Override
    public Receive createReceive() {
        return receiveBuilder().matchAny(msg -> {
            getSender().tell(cluster.selfAddress() + "=====获取用户信息" + msg + getSelf(), getSelf());
        }).build();
    }

    public static void main(String[] args) {
        String port = args[0];
        Config config = ConfigFactory
                .parseString("akka.remote.netty.tcp.port=" + port)
                .withFallback(ConfigFactory.load("cluster.conf"));

        ActorSystem system = ActorSystem.create("sys", ConfigFactory.load(config));


        Cluster cluster = Cluster.get(system);
        Address address = new Address("akka.tcp", "sys", "127.0.0.1", 2551);
        cluster.join(address);


//        ActorRef userActor = system.actorOf(Props.create(UserClusterActor.class), "userActor");
        ActorRef userActor = system.actorOf(Props.create(ClusterRouterActor.class), "userActor");

        System.out.println(userActor);
        ClusterClientReceptionist.get(system).registerService(userActor);


    }


}
