package com.siha.study;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.*;

@Slf4j
public class CompletableFutureEx {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newCachedThreadPool();

        CompletableFuture<String> receiver = new CompletableFuture<>();
        log.info("Start...");

        es.submit(() -> {
            log.info("complete");
            receiver.complete("Request Async");
        });

        receiver.thenApplyAsync(s -> {
            log.info("ApplyAsync-1: {}", s);
            return s;
        })
                .thenApplyAsync(s -> s + " -> Combine Apply")
                .thenAcceptAsync(s -> log.info("After apply, {}", s));
        log.info("End...");

        es.shutdown();
        es.awaitTermination(10, TimeUnit.SECONDS);
        ForkJoinPool.commonPool().shutdown();
        ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);
    }

    <T> CompletableFuture<T> toCompletableFuture(ListenableFuture<T> lf) {
        CompletableFuture<T> cf = new CompletableFuture<T>();
        lf.addCallback(t -> cf.complete(t), e -> cf.completeExceptionally(e));
        return cf;
    }
}
