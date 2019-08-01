package com.luban.akka.vip.高级.持久化;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.Procedure;
import akka.persistence.AbstractPersistentActor;
import akka.persistence.RecoveryCompleted;
import akka.persistence.SaveSnapshotSuccess;
import akka.persistence.SnapshotOffer;
import scala.Serializable;

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
public class PersistDemo {

    static class UserService extends AbstractPersistentActor {
        /**
         * 状态列表
         */
        private List<Action> states = new ArrayList<Action>();

        @Override
        public String persistenceId() {
            return "userservice-1";
        }

        @Override
        public Receive createReceiveRecover() {
            return receiveBuilder().matchAny(msg -> {
                if (msg instanceof Action) {
                    Action evt = (Action) msg;
                    System.out.println(evt);
                    states.add(evt);
                } else if (msg instanceof SnapshotOffer) {
                    SnapshotOffer snapOffer = (SnapshotOffer) msg;
                    states = (List<Action>) snapOffer.snapshot();
                    System.out.println("recover: " + states);
                } else if (msg instanceof RecoveryCompleted) {
                    System.out.println("replay has been finished");
                }
                System.out.println("onreceiveRecover:" + msg + "," + msg.getClass());
            }).build();
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder().matchAny(message -> {
                if (message instanceof Action) {
                    Action action = (Action) message;
                    if (action.getCmd().equals("save")) {

                        /**
                         * 持久化事件，异步执行
                         */
                        persist(action, new Procedure<Action>() {
                            @Override
                            public void apply(Action act) throws Exception {
                                // 通常会在这里更新Actor的状态，或者发布事件通知订阅者
                                states.add(act);
                            }
                        });
                    } else if (action.getCmd().equals("saveAll")) {
                        /**
                         * 保存快照
                         */
                        saveSnapshot(states);
                    } else if (action.getCmd().equals("get")) {
                        System.out.println("state:" + states);
                    }
                } else if (message instanceof SaveSnapshotSuccess) {
                    SaveSnapshotSuccess saveSnapSucc = (SaveSnapshotSuccess) message;
                    System.out.println("save snap success:" + saveSnapSucc.metadata());
                } else {
                    System.out.println("other message " + message);
                }
            }).build();
        }

    }

    static class Action implements Serializable {
        private String cmd;
        private String data;

        public Action(String cmd, String data) {
            this.cmd = cmd;
            this.data = data;
        }

        public String getCmd() {
            return cmd;
        }

        public void setCmd(String cmd) {
            this.cmd = cmd;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return this.cmd + "--->" + this.data;
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef ref = system.actorOf(Props.create(UserService.class));

//        ref.tell(new Action("save", "123"), ActorRef.noSender());
//        ref.tell(new Action("save", "456"), ActorRef.noSender());


    }
}
