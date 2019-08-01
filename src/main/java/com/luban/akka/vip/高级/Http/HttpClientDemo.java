package com.luban.akka.vip.高级.Http;

import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;

import java.util.concurrent.CompletionStage;

/**
 * *************书山有路勤为径***************
 * 鲁班学院
 * 往期资料加木兰老师  QQ: 2746251334
 * VIP课程加安其拉老师 QQ: 3164703201
 * 讲师：周瑜老师
 * *************学海无涯苦作舟***************
 */
public class HttpClientDemo {

    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create();

        final CompletionStage<HttpResponse> responseFuture = Http.get(system).singleRequest(HttpRequest.create("https://akka.io"));

        final Materializer materializer = ActorMaterializer.create(system);
        responseFuture.thenAccept(response->{
            response.entity().getDataBytes().runWith(Sink.foreach(content->{
                System.out.println(content.utf8String());
            }), materializer);
        });

    }
}

