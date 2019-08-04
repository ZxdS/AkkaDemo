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
        private Integer count = 0;


        Receive receiveC = receiveBuilder()
                .matchAny(s -> {
                    if (s.equals("unbecome")) {
                        getContext().unbecome();
                    } else {
                        System.out.println("优惠100");
                    }
                })
                .build();

        Receive receiveB = receiveBuilder()
                .matchAny(s -> {
                    if (s.equals("unbecome")) {
                        getContext().unbecome();
                    } if (s.equals("become")) {
                        getContext().become(receiveC, false);
                    } else {
                        System.out.println("优惠500");
                    }
                })
                .build();

        @Override
        public Receive createReceive() {
            return receiveBuilder()
                    .matchAny((s) -> {
                        count++;
                        System.out.println("优惠1000");
                        if (count == 3) {
                            getContext().become(receiveB);
                        }
                    })
                    .build();
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef actorRef = system.actorOf(Props.create(BecomeActor.class), "becomeActor");
        actorRef.tell("1", ActorRef.noSender());
        actorRef.tell("2", ActorRef.noSender());
        actorRef.tell("3", ActorRef.noSender());
        actorRef.tell("4", ActorRef.noSender());
        actorRef.tell("5", ActorRef.noSender());
//        actorRef.tell("unbecome", ActorRef.noSender());
        actorRef.tell("become", ActorRef.noSender());
        actorRef.tell("6", ActorRef.noSender());
        actorRef.tell("unbecome", ActorRef.noSender());
        actorRef.tell("7", ActorRef.noSender());
    }
}
