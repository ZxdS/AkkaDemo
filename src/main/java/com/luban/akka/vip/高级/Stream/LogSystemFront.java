package com.luban.akka.vip.高级.Stream;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.dispatch.OnSuccess;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * *************书山有路勤为径***************
 * 鲁班学院
 * 往期资料加木兰老师  QQ: 2746251334
 * VIP课程加安其拉老师 QQ: 3164703201
 * 讲师：周瑜老师
 * *************学海无涯苦作舟***************
 */
public class LogSystemFront {

    public static void main(String[] args) {
        String port = args[0];
        Config config = ConfigFactory
                .parseString("akka.remote.netty.tcp.port=" + port)
                .withFallback(ConfigFactory.parseString("akka.cluster.roles = [wordFrontend]"))
                .withFallback(ConfigFactory.load("logsystem.conf"));

        ActorSystem system = ActorSystem.create("sys", config);
        ActorRef ref = system.actorOf(Props.create(LogSystemFrontend.WordFrontService.class), "wordFrontService");

        Cluster cluster = Cluster.get(system);
        Address address = new Address("akka.tcp", "sys", "127.0.0.1", 2551);
        cluster.join(address);


        String result = "";
        while (true) {
            Future<Object> fu = Patterns.ask(ref, "isready", 1000);
            try {
                result = (String) Await.result(fu,
                        Duration.create(1000, "seconds"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (result.equals("ready")) {
                System.out.println("===========ready==============");
                break;
            }
        }

        List<LogSystemFrontend.Article> arts = new ArrayList<LogSystemFrontend.Article>();

        LogSystemFrontend.Article article = new LogSystemFrontend.Article();
        article.setContent("i am a person");
        article.setId("1");
        arts.add(article);

        Timeout timeout = new Timeout(Duration.create(3, TimeUnit.SECONDS));
        for (LogSystemFrontend.Article art : arts) {
            Patterns.ask(ref, art, timeout).onSuccess(new OnSuccess<Object>() {
                @Override
                public void onSuccess(Object res) throws Throwable {
                    LogSystemFrontend.CountResult cr = (LogSystemFrontend.CountResult) res;
                    System.out.println("文件" + cr.getId() + ",单词数:" + cr.getCount());
                }

            }, system.dispatcher());
        }
    }
}
