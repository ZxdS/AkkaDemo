package com.luban.akka.vip.高级.事件总线;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.japi.LookupEventBus;

/**
 * *************书山有路勤为径***************
 * 鲁班学院
 * 往期资料加木兰老师  QQ: 2746251334
 * VIP课程加安其拉老师 QQ: 3164703201
 * 讲师：周瑜老师
 * *************学海无涯苦作舟***************
 */
public class EventBusDemo {

    static class Event {
        private String type;
        private String message;

        public Event(String type, String message) {
            this.type = type;
            this.message = message;
        }

        public String getType() {
            return type;
        }

        public String getMessage() {
            return message;
        }
    }

    static class MyEventBus extends LookupEventBus<Event, ActorRef, String> {

        @Override
        public String classify(Event event) {
            return event.getType();
        }

        @Override
        public int compareSubscribers(ActorRef ref1, ActorRef ref2) {
            return ref1.compareTo(ref2);
        }

        @Override
        public void publish(Event event, ActorRef ref) {
            ref.tell(event.getMessage(), ActorRef.noSender());
        }

        /**
         * 期望的classify数,一般设置为2的n次幂
         */
        @Override
        public int mapSize() {
            return 8;
        }

    }

    static class EventSubActor extends AbstractActor {
        @Override
        public Receive createReceive() {
            return receiveBuilder().matchAny(msg -> {
                System.out.println(msg);
            }).build();
        }
    }

    public static void main(String[] args) {

        ActorSystem system = ActorSystem.create("sys");
        MyEventBus bus = new MyEventBus();

        ActorRef eventSubActor = system.actorOf(Props.create(EventSubActor.class));

        //订阅info与warn事件
        bus.subscribe(eventSubActor, "info");
        bus.subscribe(eventSubActor, "warn");

        //发布info事件
        bus.publish(new Event("info", "Hello EventBus"));

        //取消订阅
        bus.unsubscribe(eventSubActor, "warn");

        //发布warn事件
        bus.publish(new Event("warn", "Oh No"));
    }

}
