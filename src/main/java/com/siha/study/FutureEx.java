package com.siha.study;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by 1002375 on 2016. 12. 22..
 */
@Slf4j
public class FutureEx {
    public static void main(String[] args) throws Exception {
        ExecutorService es = Executors.newCachedThreadPool();

        Future<String> f1 = es.submit(() -> {
            Thread.sleep(2000);
            log.info("ASYNC-01");
            return "thread-01";
        });

        Future<String> f2 = es.submit(() -> {
            Thread.sleep(1000);
            log.info("ASYNC-02");
            return "thread-02";
        });

        log.info("data: {}", f1.get());
        log.info("data: {}", f2.get());
    }
}
