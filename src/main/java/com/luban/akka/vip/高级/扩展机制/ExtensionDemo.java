package com.luban.akka.vip.高级.扩展机制;

import akka.actor.AbstractExtensionId;
import akka.actor.ActorSystem;
import akka.actor.ExtendedActorSystem;
import akka.actor.Extension;
import com.typesafe.config.Config;

/**
 * *************书山有路勤为径***************
 * 鲁班学院
 * 往期资料加木兰老师  QQ: 2746251334
 * VIP课程加安其拉老师 QQ: 3164703201
 * 讲师：周瑜老师
 * *************学海无涯苦作舟***************
 */
public class ExtensionDemo {

    static class RPCExtension implements Extension {
        private String server;
        private int port;

        public RPCExtension(String server, int port) {
            this.server = server;
            this.port = port;
        }

        //模拟RPC调用
        public void rpcCall(String cmd) {
            System.out.println("call " + cmd + "-->" + server + ":" + port);
        }
    }

    static class RPCExtProvider extends AbstractExtensionId<RPCExtension> {

        private static RPCExtProvider provider = new RPCExtProvider();

        @Override
        public RPCExtension createExtension(ExtendedActorSystem system) {
            Config config = system.settings().config();
            String server = config.getString("akkademo.server");
            int port = config.getInt("akkademo.port");
            return new RPCExtension(server, port);
        }

        public static RPCExtProvider getInstance() {
            return provider;
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        RPCExtension rpcExt = RPCExtProvider.getInstance().get(system);
        rpcExt.rpcCall("hello");
    }

}
