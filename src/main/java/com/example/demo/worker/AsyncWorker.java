package com.example.demo.worker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.concurrent.Callable;

public class AsyncWorker
{
    private static final Log LOG = LogFactory.getLog(AsyncWorker.class);

    public static <V> void startTask(TopicBuilder.BaseTopic topic, Callable<V> operation)
    {
        TopicMessager.open(topic);
        Observable.fromCallable(() ->{
           operation.call();
           return TopicMessage.Builder.success(topic).build();
        }).retry((count, e) -> (e instanceof IOException) && count < 3).onErrorReturn(e -> {
            return TopicMessage.Builder.failure(topic).build();
        }).subscribeOn(Schedulers.io()).subscribe(v -> {
            TopicMessager.publish(v);
        }, e -> {
            TopicMessager.close(topic);
        }, () -> {
            TopicMessager.close(topic);
        });
    }
}
