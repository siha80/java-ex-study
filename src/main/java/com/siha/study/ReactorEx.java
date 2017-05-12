package com.siha.study;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public class ReactorEx {
    public static void main(String[] args) {
        Flux.create(e -> {
            e.next(1);
            e.next(2);
            e.complete();
        })
        .log()
        .subscribe(System.out::println);
    }
}
