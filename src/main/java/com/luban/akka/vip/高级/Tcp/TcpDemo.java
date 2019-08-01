package com.luban.akka.vip.高级.Tcp;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.io.Tcp;
import akka.io.TcpMessage;
import akka.util.ByteString;

import java.net.InetSocketAddress;

/**
 * *************书山有路勤为径***************
 * 鲁班学院
 * 往期资料加木兰老师  QQ: 2746251334
 * VIP课程加安其拉老师 QQ: 3164703201
 * 讲师：周瑜老师
 * *************学海无涯苦作舟***************
 */
public class TcpDemo {

    static class TcpServerActor extends AbstractActor {
        @Override
        public void preStart() throws Exception {
            super.preStart();
            ActorRef tcpManager = Tcp.get(getContext().system()).manager();
            // 发送bind指令给TcpManager,bind成功后会给发送者响应Bound消息
            tcpManager.tell(TcpMessage.bind(getSelf(), new InetSocketAddress("127.0.0.1", 1234), 100), getSelf());
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder().matchAny(msg -> {
                if (msg instanceof Tcp.Bound) {
                    // 绑定成功
                    Tcp.Bound bound = (Tcp.Bound) msg;
                    System.out.println("bound:" + bound);
                    /*..其他处理略..*/
                } else if (msg instanceof Tcp.Connected) {
                    // 有客户端与服务端连接成功后，会接收到一个Connected消息
                    // 该消息表示连接对象
                    Tcp.Connected conn = (Tcp.Connected) msg;
                    System.out.println("conn:" + conn);
                    ActorRef handler = getContext().actorOf(Props.create(ServerHandlerActor.class));
                    // 在服务端注册一个请求处理器
                    getSender().tell(TcpMessage.register(handler), getSelf());
                }
            }).build();
        }
    }

    // 请求处理器
    static class ServerHandlerActor extends AbstractActor {
        @Override
        public Receive createReceive() {
            return receiveBuilder().matchAny(msg -> {
                if (msg instanceof Tcp.Received) {
                    // 接收到了客户端的数据
                    Tcp.Received re = (Tcp.Received) msg;
                    ByteString b = re.data();
                    String content = b.utf8String();
                    System.out.println("server:" + content);

                    // 获取连接对象
                    ActorRef conn = getSender();
                    conn.tell(TcpMessage.write(ByteString.fromString("Thanks")), getSelf());
                } else if (msg instanceof Tcp.ConnectionClosed) {
                    System.out.println("Connection is closed " + msg);
                    getContext().stop(getSelf());
                } else {
                    System.out.println("Other Tcp Server: " + msg);
                }
            }).build();
        }
    }

    static class TcpClientActor extends AbstractActor {
        @Override
        public void preStart() throws Exception {
            super.preStart();
            ActorRef tcpManager = Tcp.get(getContext().system()).manager();
            // 发送connect指令给TcpManager，连接某个服务端
            tcpManager.tell(TcpMessage.connect(new InetSocketAddress("127.0.0.1", 1234)), getSelf());
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder().matchAny(msg -> {
                if (msg instanceof Tcp.Connected) {
                    // 连接成功
                    // 获取连接对象
                    ActorRef conn = getSender();
                    ActorRef clientHandler = getContext().actorOf(Props.create(ClientHandlerActor.class), "clientHandler");

                    // 向该连接注册一个client响应处理器
                    conn.tell(TcpMessage.register(clientHandler), getSelf());
                    conn.tell(TcpMessage.write(ByteString.fromString("HelloAkka")), getSelf());
                }
            }).build();
        }
    }

    static class ClientHandlerActor extends AbstractActor {
        @Override
        public Receive createReceive() {
            return receiveBuilder().matchAny(msg -> {
                if (msg instanceof Tcp.Received) {
                    Tcp.Received re = (Tcp.Received) msg;
                    ByteString b = re.data();
                    String content = b.utf8String();
                    System.out.println("client:" + content);
                } else {
                    System.out.println("Other Tcp Client: " + msg);
                }
            }).build();
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        system.actorOf(Props.create(TcpServerActor.class));
        system.actorOf(Props.create(TcpClientActor.class));
    }
}
