package com.luban.akka.vip.高级.扩展机制;

import akka.actor.ActorSystem;
import akka.actor.TypedActor;
import akka.actor.TypedActorExtension;
import akka.actor.TypedProps;
import akka.dispatch.Futures;
import akka.dispatch.OnSuccess;
import akka.japi.Option;
import scala.concurrent.Future;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * *************书山有路勤为径***************
 * 鲁班学院
 * 往期资料加木兰老师  QQ: 2746251334
 * VIP课程加安其拉老师 QQ: 3164703201
 * 讲师：周瑜老师
 * *************学海无涯苦作舟***************
 */
public class TypedActorDemo {

    public interface UserService {

        public void saveUser(String id, String user);

        public Future<String> findUserForFuture(String id);

        public Option<String> findUserForOpt(String id);

        public String findUser(String id);

    }

    static class UserServiceImpl implements UserService {

        private static Map<String, String> map = new ConcurrentHashMap<String, String>();

        // 当方法无返回值（即 void）的时候，底层会采用 ActorRef.tell 的方式来调用，执行方式为异步；
        @Override
        public void saveUser(String id, String user) {
            map.put(id, user);
        }

        // 当方法返回 scala.concurrent.Future 时，会以 Patterns.ask 的方式来调用，然后将结果值包装到 Future 里并返回，它的执行方式也是异步
        @Override
        public Future<String> findUserForFuture(String id) {
            return Futures.successful(map.get(id));
        }

        // 当方法返回 akka.japi.Option 时，会以 Patterns.ask 的方式来调用，但是程序会一直阻塞，直到有返回值。假如返回值为 null，它会被包装成 Option.None 类型；
        @Override
        public Option<String> findUserForOpt(String id) {
            return Option.some(map.get(id));
        }

        // 当方法返回其他类型时，就和普通方法一样，程序会一直阻塞，直到有返回值。
        @Override
        public String findUser(String id) {
            return map.get(id);
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        TypedActorExtension extension = TypedActor.get(system);
        UserService userService = extension.typedActorOf(new TypedProps<UserServiceImpl>(UserService.class, UserServiceImpl.class));

        System.out.println("userService: " + userService);
        //无返回值，异步执行
        userService.saveUser("1", "afei");

        //有返回值，阻塞执行
        Option<String> opt = userService.findUserForOpt("1");
        System.out.println("The Opt user is:" + opt.getClass());

        //有返回值，阻塞执行
        String user = userService.findUser("1");
        System.out.println("The user is: " + user);

        //有返回值，异步执行
        Future<String> fu = userService.findUserForFuture("1");
        fu.onSuccess(new OnSuccess() {
            @Override
            public void onSuccess(Object result) throws Throwable {
                System.out.println("The future user is:" + result);
            }
        }, system.dispatcher());
    }
}
