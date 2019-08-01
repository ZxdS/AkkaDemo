package com.luban.akka.vip.高级.路由;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.FromConfig;
import com.typesafe.config.ConfigFactory;

/**
 * *************书山有路勤为径***************
 * 鲁班学院
 * 往期资料加木兰老师  QQ: 2746251334
 * VIP课程加安其拉老师 QQ: 3164703201
 * 讲师：周瑜老师
 * *************学海无涯苦作舟***************
 */
public class RoundRobinPoolDemo {

    static class RouteeActor extends AbstractActor {
        @Override
        public Receive createReceive() {
            return receiveBuilder().matchAny(msg -> {
                System.out.println(getSelf() + "->" + msg + "-->" + getContext().parent());
            }).build();
        }
    }

    // 一般不直接用路由器来发送消息，而是先经过一层中间转发的过程，这样有利于构建更加清晰的管理结构。
    static class RouterActor extends AbstractActor {

        ActorRef router = null;

        @Override
        public void preStart() throws Exception {
//            router = getContext().actorOf(new RoundRobinPool(3).props(Props.create(RouteeActor.class)), "routerActor");
            router = getContext().actorOf(FromConfig.getInstance().props(Props.create(RouteeActor.class)),"routerActor");
            System.out.println("router:" + router);
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder().matchAny(msg -> {
                router.tell(msg, getSender());
            }).build();
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys", ConfigFactory.load("router.conf"));
        ActorRef routerActor = system.actorOf(Props.create(RouterActor.class), "roundRobinPoolRouterActor");
        routerActor.tell("helloA", ActorRef.noSender());
        routerActor.tell("helloC", ActorRef.noSender());
        routerActor.tell("helloB", ActorRef.noSender());
    }
}
