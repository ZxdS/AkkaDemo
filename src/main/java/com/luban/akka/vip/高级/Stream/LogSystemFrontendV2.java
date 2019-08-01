package com.luban.akka.vip.高级.Stream;

import akka.actor.*;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import akka.cluster.routing.ClusterRouterPool;
import akka.cluster.routing.ClusterRouterPoolSettings;
import akka.routing.RoundRobinPool;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
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
public class LogSystemFrontendV2 {

    static class WordFrontService extends AbstractActor {

        private List<ActorRef> wordCountServices = new ArrayList<ActorRef>();

        private ActorRef masterRouter;

        @Override
        public void preStart() throws Exception {
            int totalInstances = 100;
            int maxInstancesPerNode = 5;
            boolean allowLocalRoutees = false;
            String useRole = null;
            masterRouter = getContext().actorOf(new ClusterRouterPool(new RoundRobinPool(2),
                    new ClusterRouterPoolSettings(totalInstances, maxInstancesPerNode, allowLocalRoutees, useRole)).props(Props.create(WordCountService.class)), "poolRouter");
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder().matchAny(msg -> {
                if (msg instanceof Article) {
                    Article art = (Article) msg;
                    masterRouter.tell(art, getSender());
                } else if (msg instanceof String) {
                    String cmd = (String) msg;
                    if (cmd.equals("serviceIsOK")) {
                        // 后端Service已就绪
                        ActorRef backendSender = getSender();
                        System.out.println(backendSender + "可用");
                        wordCountServices.add(backendSender);
                        getContext().watch(backendSender);
                    } else if (cmd.equals("isready")) {
                        if (!wordCountServices.isEmpty()) {
                            getSender().tell("ready", getSelf());
                        } else {
                            getSender().tell("notready", getSelf());
                        }
                    }
                } else if (msg instanceof Terminated) {
                    // 当后端Service终止时，将其从后端列表中移除
                    Terminated ter = (Terminated) msg;
                    System.out.println("移除了" + ter.getActor());
                    wordCountServices.remove(ter.getActor());
                } else {
                    unhandled(msg);
                }
            }).build();
        }
    }

    /**
     * 文章
     */
    static class Article implements Serializable {

        private String id;
        private String content;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    /**
     * 单词统计结果
     */
    static class CountResult implements Serializable {
        private String id;
        private int count;

        public CountResult() {
        }

        public CountResult(String id, int count) {
            this.id = id;
            this.count = count;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }


    static class WordCountService extends AbstractActor {

        Cluster cluster = Cluster.get(getContext().system());

        @Override
        public void preStart() {
            cluster.subscribe(getSelf(), ClusterEvent.MemberUp.class);
        }

        @Override
        public void postStop() {
            cluster.unsubscribe(getSelf());
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder().matchAny(msg -> {
                if (msg instanceof Article) {
                    System.out.println("当前节点:" + cluster.selfAddress() + ",self=" + context().self() + "正在处理……");
                    Article art = (Article) msg;
                    String content = art.getContent();
                    int word_count = content.split("").length;
                    getSender().tell(new CountResult(art.getId(), word_count), getSelf());
                } else if (msg instanceof ClusterEvent.MemberUp) {
                    ClusterEvent.MemberUp mu = (ClusterEvent.MemberUp) msg;
                    Member m = mu.member();
                    if (m.hasRole("wordFrontend")) {
                        getContext().actorSelection(m.address() + "/user/wordFrontService").tell("serviceIsOK", getSelf());
                    }
                    System.out.println(m + " is up");
                } else {
                    unhandled(msg);
                }
            }).build();
        }
    }

    public static void main(String[] args) {
        String port = args[0];
        Config config = ConfigFactory
                .parseString("akka.remote.netty.tcp.port=" + port)
                .withFallback(ConfigFactory.load("logsystem.conf"));
        ActorSystem system = ActorSystem.create("sys", config);
        ActorRef ref = system.actorOf(Props.create(WordCountService.class), "wordCount-Service");
    }


}
