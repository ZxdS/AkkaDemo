package com.luban.akka.vip.入门;

import akka.actor.*;
import akka.dispatch.OnSuccess;
import akka.japi.pf.FI;
import akka.util.Timeout;
import scala.Function1;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.util.Success;

import java.util.concurrent.TimeUnit;

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
        ActorRef targetActor2 = system.actorOf(Props.create(TargetActor.class), "targetActor2");
        ActorRef targetActor1 = system.actorOf(Props.create(TargetActor.class), "targetActor1");

        ActorSelection actorSelection = system.actorSelection("user/targetActor*");
//        actorSelection.tell("hello", ActorRef.noSender());

        Future<ActorRef> future = actorSelection.resolveOne(new Timeout(Duration.create(3, TimeUnit.SECONDS)));
        future.onSuccess(new OnSuccess<ActorRef>() {
            @Override
            public void onSuccess(ActorRef result) throws Throwable {
                System.out.println(result);
            }
        }, system.getDispatcher());
    }
}
