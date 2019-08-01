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
public class BecomeDemo2 {


    static class BecomeActor extends AbstractActor {

        Receive level1 = receiveBuilder().match(String.class, s -> {
            if (s.equals("end")) {
                getContext().unbecome();
            } else {
                System.out.println("level1" + s + "员工的年终奖等于工资乘以1.5");
            }
        }).build();

        Receive level2 = receiveBuilder().match(String.class, s -> {
            if (s.equals("end")) {
                getContext().unbecome();
            } else {
                System.out.println("level2" + s + "员工的年终奖等于工资乘以2");
            }
        }).build();

        Receive level3 = receiveBuilder().match(String.class, s -> {
            if (s.equals("end")) {
                getContext().unbecome();
            } else if (s.equals("gotoLevel2")) {
                getContext().become(level2, false);
            } else {
                System.out.println("level3" + s + "员工的年终奖等于工资乘以3");
            }
        }).build();


        @Override
        public Receive createReceive() {
            return receiveBuilder()
                    .matchEquals("1", s -> {
                        getContext().become(level1);
                    })
                    .matchEquals("2", s -> {
                        getContext().become(level2);
                    })
                    .matchEquals("3", s -> {
                        getContext().become(level3);
                    })
                    .build();
        }
    }


    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef actorRef = system.actorOf(Props.create(BecomeActor.class), "becomeActor");
        actorRef.tell("1", ActorRef.noSender());
        actorRef.tell("张三", ActorRef.noSender());
        actorRef.tell("end", ActorRef.noSender());
        actorRef.tell("2", ActorRef.noSender());
        actorRef.tell("李四", ActorRef.noSender());
        actorRef.tell("end", ActorRef.noSender());
        actorRef.tell("3", ActorRef.noSender());
        actorRef.tell("王五", ActorRef.noSender());
//        actorRef.tell("gotoLevel2", ActorRef.noSender());
//        actorRef.tell("赵六", ActorRef.noSender());
        actorRef.tell("end", ActorRef.noSender());

        actorRef.tell("赵七", ActorRef.noSender());
    }
}
