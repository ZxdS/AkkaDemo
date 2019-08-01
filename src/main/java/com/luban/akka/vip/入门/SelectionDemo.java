package com.luban.akka.vip.入门;

import akka.actor.*;
import akka.japi.pf.FI;

/**
 * *************书山有路勤为径***************
 * 鲁班学院
 * 往期资料加木兰老师  QQ: 2746251334
 * VIP课程加安其拉老师 QQ: 3164703201
 * 讲师：周瑜老师
 * *************学海无涯苦作舟***************
 */
public class SelectionDemo {

    static class TargetActor extends AbstractActor {
        @Override
        public Receive createReceive() {
            return receiveBuilder().matchAny((message) -> System.out.println("target receive" + message)).build();
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef targetActor1 = system.actorOf(Props.create(TargetActor.class), "targetActor1");
        ActorRef targetActor2 = system.actorOf(Props.create(TargetActor.class), "targetActor2");

        ActorSelection actorSelection = system.actorSelection("user/targetActor*");
        actorSelection.tell("hello", ActorRef.noSender());
    }
}
