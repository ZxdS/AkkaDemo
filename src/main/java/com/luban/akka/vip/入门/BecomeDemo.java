package com.luban.akka.vip.入门;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.function.Procedure;

/**
 * *************书山有路勤为径***************
 * 鲁班学院
 * 往期资料加木兰老师  QQ: 2746251334
 * VIP课程加安其拉老师 QQ: 3164703201
 * 讲师：周瑜老师
 * *************学海无涯苦作舟***************
 */
public class BecomeDemo {

    static class BecomeActor extends AbstractActor {

        Receive receive = receiveBuilder()
                .matchAny(s -> {
                    System.out.println("receive " + s);
                    if (s.equals("unbecome")) {
                        getContext().unbecome();
                    }
                })
                .build();

        @Override
        public Receive createReceive() {
            return receiveBuilder()
                    .matchAny((s) -> {
                        System.out.println("接收消息 " + s);
                        if (s.equals("become")) {
                            getContext().become(receive);
                        }
                    })
                    .build();
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef actorRef = system.actorOf(Props.create(BecomeActor.class), "becomeActor");
        actorRef.tell("test", ActorRef.noSender());
        actorRef.tell("become", ActorRef.noSender());
        actorRef.tell("test", ActorRef.noSender());
        actorRef.tell("unbecome", ActorRef.noSender());
        actorRef.tell("test", ActorRef.noSender());
    }
}
