package com.luban.akka.vip.高级.远程;

import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * *************书山有路勤为径***************
 * 鲁班学院
 * 往期资料加木兰老师  QQ: 2746251334
 * VIP课程加安其拉老师 QQ: 3164703201
 * 讲师：周瑜老师
 * *************学海无涯苦作舟***************
 */
public class RemoteActorSystemDemo {

    public static void main(String[] args) {
        ActorSystem sys = ActorSystem.create("sys", ConfigFactory.load("remote.conf"));

    }
}
