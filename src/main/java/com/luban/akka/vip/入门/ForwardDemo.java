package com.luban.akka.vip.入门;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * *************书山有路勤为径***************
 * 鲁班学院
 * 往期资料加木兰老师  QQ: 2746251334
 * VIP课程加安其拉老师 QQ: 3164703201
 * 讲师：周瑜老师
 * *************学海无涯苦作舟***************
 */
public class ForwardDemo {

    static class TargetActor extends AbstractActor {
        @Override
        public Receive createReceive() {
            System.out.println(getSender());
            return receiveBuilder().matchAny(System.out::println).build();
        }
    }

    static class ForwardActor extends AbstractActor {
        private ActorRef target = getContext().actorOf(Props.create(TargetActor.class), "targetActor");

        @Override
        public Receive createReceive() {
            return receiveBuilder().matchAny((message) -> {
                target.forward(message, getContext());
            }).build();
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef actorRef = system.actorOf(Props.create(ForwardActor.class), "forwardDemo");
        actorRef.tell("123", ActorRef.noSender());  // 转发之后sender不会发生变化
    }
}
