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
                System.out.println("========自动恢复=======");
                return SupervisorStrategy.resume(); // 恢复
            } else if (t instanceof IndexOutOfBoundsException) {
                System.out.println("=========重启==========");
                return SupervisorStrategy.restart(); // 重启
            } else if (t instanceof SQLException) {
                System.out.println("==========停止=========");
                return SupervisorStrategy.stop();  // 停止
            } else {
                System.out.println("==========上报=========");
                return SupervisorStrategy.escalate(); // 上报
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
        private int stateCount = 0;

        public void preStart() throws Exception {
            System.out.println("启动前preStart");
            super.preStart();
        }

        @Override
        public void postStop() throws Exception {
            System.out.println("停止后postStop");
            super.postStop();
        }

        @Override
        public void preRestart(Throwable reason, Option<Object> message)
                throws Exception {
            System.out.println("重启前preRestart");
            super.preRestart(reason, message);
        }

        @Override
        public void postRestart(Throwable reason) throws Exception {
            super.postRestart(reason);
            System.out.println("重启后postRestart");
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder().matchAny(msg -> {
                //模拟计算任务
                if (msg.equals("add")) {
                    stateCount++;
                    System.out.println(stateCount);
                } else if (msg.equals("get")) {
                    System.out.println(stateCount);
                } else if (msg instanceof Exception) {
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

        System.out.println("发送消息");

        workerActor.tell("add", ActorRef.noSender());
        workerActor.tell(new IndexOutOfBoundsException(), ActorRef.noSender());
        workerActor.tell(new IndexOutOfBoundsException(), ActorRef.noSender());
        workerActor.tell(new IndexOutOfBoundsException(), ActorRef.noSender());
        workerActor.tell(new IndexOutOfBoundsException(), ActorRef.noSender());
//        workerActor.tell(new SQLException("SQL异常"), ActorRef.noSender());
//        workerActor.tell(new IOException(), ActorRef.noSender());


        workerActor.tell("get", ActorRef.noSender());
    }
}
