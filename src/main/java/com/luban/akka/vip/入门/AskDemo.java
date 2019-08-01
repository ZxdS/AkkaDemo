package com.luban.akka.vip.入门;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.OnSuccess;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * *************书山有路勤为径***************
 * 鲁班学院
 * 往期资料加木兰老师  QQ: 2746251334
 * VIP课程加安其拉老师 QQ: 3164703201
 * 讲师：周瑜老师
 * *************学海无涯苦作舟***************
 */
public class AskDemo extends AbstractActor {

    @Override
    public Receive createReceive() {
        return receiveBuilder().matchAny(o -> {
            System.out.println("发送者是" + getSender());
            Thread.sleep(1000);
            getSender().tell("hello " + o, getSelf());
        }).build();
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef askActor = system.actorOf(Props.create(AskDemo.class), "askActorDemo");

        Timeout timeout = new Timeout(Duration.create(2, TimeUnit.SECONDS));
        Future<Object> f = Patterns.ask(askActor, "Akka Ask", timeout);
        System.out.println("ask ...");

        f.onSuccess(new OnSuccess<Object>() {
            @Override
            public void onSuccess(Object result) throws Throwable {
                System.out.println("收到消息：" + result);
            }
        }, system.getDispatcher());
    }
}
