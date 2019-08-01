package com.luban.akka.vip.入门;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;

/**
 * *************书山有路勤为径***************
 * 鲁班学院
 * 往期资料加木兰老师  QQ: 2746251334
 * VIP课程加安其拉老师 QQ: 3164703201
 * 讲师：周瑜老师
 * *************学海无涯苦作舟***************
 */
public class HelloWorldDemo extends AbstractActor {

//    // 可以通过工厂方式创建actor, 一次模板，到处创建
    public static Props createProps() {
        return Props.create(new Creator<Actor>() {
            @Override
            public Actor create() throws Exception {
                return new HelloWorldDemo();
            }
        });
    }

    private LoggingAdapter log = Logging.getLogger(this.getContext().getSystem(), this);


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchAny(o -> {
                    log.info("any" + o.toString());
                })
                .matchEquals("hello", s -> log.info("equals"+s))
                .match(
                        String.class,
                        s -> log.info(s))

                .build();
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef actorRef = system.actorOf(HelloWorldDemo.createProps(), "actorDemo");
        actorRef.tell("hello", ActorRef.noSender()); // ActorRef.noSender()实际上就是叫做deadLetters的actor
    }
}
