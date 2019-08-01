package com.luban.akka.vip.入门;

import akka.actor.*;
import akka.japi.Function;
import scala.Option;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;

/**
 * *************书山有路勤为径***************
 * 鲁班学院
 * 往期资料加木兰老师  QQ: 2746251334
 * VIP课程加安其拉老师 QQ: 3164703201
 * 讲师：周瑜老师
 * *************学海无涯苦作舟***************
 */
public class SupervisorDemo {

    static class SupervisorActor extends AbstractActor {
        //定义监督策略
        // maxNrOfRetries、withinTimeRange表示：在指定时间内的最大重启次数，超过这个次数就 stop 掉，在我们的代码中设定了：在 1 分钟内重启超过 3 次则停止该 Actor
        private SupervisorStrategy strategy = new OneForOneStrategy(3, Duration.ofMinutes(1), t -> {
            if (t instanceof IOException) {
                System.out.println("==========IOException=========");
                return SupervisorStrategy.resume(); // 恢复
            } else if (t instanceof IndexOutOfBoundsException) {
                System.out.println("=========IndexOutOfBoundsException==========");
                return SupervisorStrategy.restart(); // 重启
            } else if (t instanceof SQLException) {
                System.out.println("==========SQLException=========");
                return SupervisorStrategy.stop();  // 停止
            } else {
                System.out.println("==========escalate=========");
                return SupervisorStrategy.escalate(); // 上溯
            }
        });

        @Override
        public SupervisorStrategy supervisorStrategy() {
            return strategy;
        }

        @Override
        public void preStart() throws Exception {
            ActorRef workerActor = getContext().actorOf(Props.create(WorkerActor.class), "workerActor");
            getContext().watch(workerActor);
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder().matchAny(msg -> {
                if (msg instanceof Terminated) {
                    Terminated ter = (Terminated) msg;
                    System.out.println(ter.getActor() + "已经终止");
                } else {
                    System.out.println("stateCount=" + msg);
                }
            }).build();
        }
    }


    static class WorkerActor extends AbstractActor {
        //状态数据
        private int stateCount = 1;

        public void preStart() throws Exception {
            System.out.println("worker actor preStart");
            super.preStart();
        }

        @Override
        public void postStop() throws Exception {
            System.out.println("worker actor postStop");
            super.postStop();
        }

        @Override
        public void preRestart(Throwable reason, Option<Object> message)
                throws Exception {
            System.out.println("worker actor preRestart");
            super.preRestart(reason, message);
        }

        @Override
        public void postRestart(Throwable reason) throws Exception {
            System.out.println("worker actor postRestart");
            super.postRestart(reason);
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder().matchAny(msg -> {
                //模拟计算任务
                this.stateCount++;
                System.out.println(stateCount);
                if (msg instanceof Exception) {
                    throw (Exception) msg;
                } else {
                    unhandled(msg);
                }
            }).build();
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef supervisorActor = system.actorOf(Props.create(SupervisorActor.class), "supervisorActor");
        ActorSelection workerActor = system.actorSelection("akka://sys/user/supervisorActor/workerActor");

        workerActor.tell(new IOException(), ActorRef.noSender());
//        workerActor.tell(new SQLException("SQL异常"), ActorRef.noSender());
//        workerActor.tell(new IndexOutOfBoundsException(), ActorRef.noSender());
        workerActor.tell("123", ActorRef.noSender());
    }
}
