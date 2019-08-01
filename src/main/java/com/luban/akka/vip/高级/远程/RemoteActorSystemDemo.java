package com.luban.akka.vip.高级.远程;

import akka.actor.*;
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
public class RemoteActorSystemDemo {

    static class SimpleActor extends AbstractActor {
        @Override
        public Receive createReceive() {
            return receiveBuilder().matchAny(msg -> {
                System.out.println(msg);
            }).build();
        }
    }

    public static void main(String[] args) {
        ActorSystem sys = ActorSystem.create("sys", ConfigFactory.load("remote.conf"));
        ActorRef ref = sys.actorOf(Props.create(SimpleActor.class), "simpleActor");

        ActorSelection actorSelection = sys.actorSelection("akka.tcp://sys@127.0.0.1:2552/user/simpleActor");
        actorSelection.tell("tttt", ActorRef.noSender());
    }
}
