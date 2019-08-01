package com.luban.akka.vip.高级.Future;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.OnComplete;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.pattern.AskTimeoutException;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.Function1;
import scala.PartialFunction;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;


/**
 * *************书山有路勤为径***************
 * 鲁班学院
 * 往期资料加木兰老师  QQ: 2746251334
 * VIP课程加安其拉老师 QQ: 3164703201
 * 讲师：周瑜老师
 * *************学海无涯苦作舟***************
 */
public class FutureDemo {

    static class FutureActor extends AbstractActor {
        @Override
        public Receive createReceive() {
            return receiveBuilder().matchAny(msg -> {

                Thread.sleep(4000);
                getSender().tell("reply", getSelf());
            }).build();
        }
    }


    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef ref = system.actorOf(Props.create(FutureActor.class), "fuActor");

        Timeout timeout = new Timeout(Duration.create(3, "seconds"));
        Future<Object> future = Patterns.ask(ref, "hello future", timeout);
        System.out.println(future);
        try {
            // Await同步获取响应，如果超时了则会抛出java.util.concurrent.TimeoutException
            String replymsg = (String) Await.result(future, timeout.duration());
            System.out.println(replymsg);

//            future.onSuccess(new OnSuccess<Object>() {
//                @Override
//                public void onSuccess(Object msg) throws Throwable {
//                    System.out.println("receive: " + msg);
//                }
//            }, system.dispatcher());
//
//            future.onFailure(new OnFailure() {
//
//                @Override
//                public void onFailure(Throwable ex) throws Throwable {
//                    if (ex instanceof AskTimeoutException) {
//                        System.out.println("超时异常");
//                    } else {
//                        System.out.println("其他异常 " + ex);
//                    }
//                }
//            }, system.dispatcher());
//
//            future.onComplete(new OnComplete<Object>() {
//                @Override
//                public void onComplete(Throwable failure, Object success) throws Throwable {
//                    if (failure != null) {
//                        System.out.println("异常");
//                    } else {
//                        System.out.println(success);
//                    }
//                }
//            }, system.dispatcher());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
