package com.luban.akka.vip.高级.路由;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;

import java.util.ArrayList;
import java.util.List;

/**
 * *************书山有路勤为径***************
 * 鲁班学院
 * 往期资料加木兰老师  QQ: 2746251334
 * VIP课程加安其拉老师 QQ: 3164703201
 * 讲师：周瑜老师
 * *************学海无涯苦作舟***************
 */
public class RouteDemo {

    // 定义一个Actor作为Routee
    static class RouteeActor extends AbstractActor {
        @Override
        public Receive createReceive() {
            return receiveBuilder().matchAny(msg -> {
                System.out.println(getSelf() + "-->" + msg);
            }).build();
        }
    }

    // 定义一个Actor做为Router的载体
    static class RouterActor extends AbstractActor {
        private Router router;

        @Override
        public void preStart() throws Exception {
            List<Routee> listRoutee = new ArrayList<Routee>();
            for (int i = 0; i < 2; i++) {
                ActorRef ref = getContext().actorOf(Props.create(RouteeActor.class), "routeeActor" + i);
                listRoutee.add(new ActorRefRoutee(ref));
            }
            router = new Router(new RoundRobinRoutingLogic(), listRoutee);
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder().matchAny(msg -> {
                router.route(msg, getSender());
            }).build();
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        // 运行后的打印顺序并不一定是ABC，因为Actor消费消息是异步的，只能确定A是由Actor0处理，B是由Actor1处理，C是由Actor0处理。
        ActorRef routerActor = system.actorOf(Props.create(RouterActor.class), "routerActor");
        routerActor.tell("helloA", ActorRef.noSender());
        routerActor.tell("helloB", ActorRef.noSender());
        routerActor.tell("helloC", ActorRef.noSender());
    }
}
