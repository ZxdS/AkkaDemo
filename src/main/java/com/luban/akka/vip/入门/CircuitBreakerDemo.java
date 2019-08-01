package com.luban.akka.vip.入门;

import akka.actor.*;
import akka.pattern.CircuitBreaker;

import java.time.Duration;
import java.util.concurrent.Callable;

/**
 * *************书山有路勤为径***************
 * 鲁班学院
 * 往期资料加木兰老师  QQ: 2746251334
 * VIP课程加安其拉老师 QQ: 3164703201
 * 讲师：周瑜老师
 * *************学海无涯苦作舟***************
 */
public class CircuitBreakerDemo {

    static class CircuitBreakerActor extends AbstractActor {
        private ActorRef workerChild;
        private static SupervisorStrategy strategy = new OneForOneStrategy(20, Duration.ofMinutes(1), param -> SupervisorStrategy.resume());

        @Override
        public SupervisorStrategy supervisorStrategy() {
            return strategy;
        }

        @Override
        public void preStart() throws Exception {
            super.preStart();
            workerChild = getContext().actorOf(Props.create(WorkerActor.class), "workerActor");
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder().matchAny(message -> workerChild.tell(message, getSender())).build();
        }
    }

    static class WorkerActor extends AbstractActor {
        private CircuitBreaker breaker;

        @Override
        public void preStart() throws Exception {
            super.preStart();
            this.breaker = new CircuitBreaker(getContext().dispatcher(), getContext().system().scheduler(), 3,
                    Duration.ofSeconds(1),
                    Duration.ofSeconds(30))
                    .onOpen(new Runnable() {
                        public void run() {
                            System.out.println("---> Actor CircuitBreaker 开启");
                        }
                    }).onHalfOpen(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("---> Actor CircuitBreaker 半开启");
                        }
                    }).onClose(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("--->Actor CircuitBreaker关闭");
                        }
                    });
        }

        public void handlerMsg(String msg) {
            if (msg.equals("sync")) {
                System.out.println("msg:" + msg);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("msg:" + msg);
            }
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder().matchAny(message -> {
                if (message instanceof String) {
                    String msg = (String) message;
                    breaker.callWithSyncCircuitBreaker(new Callable<String>() {
                        @Override
                        public String call() throws Exception {
                            handlerMsg(msg);
                            return msg;
                        }
                    });
                }
            }).build();
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef actorRef = system.actorOf(Props.create(CircuitBreakerActor.class), "circuitBreakerDemo");

        for (int i = 0; i < 20; i++) {
            if (i > 6) {
                actorRef.tell("test", ActorRef.noSender());
            }
            actorRef.tell("sync", ActorRef.noSender());

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
