package com.siha.study;

import io.reactivex.Completable;
import io.reactivex.Observable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ObservableEx {
    public static void main(String[] args) {
        Observable<String> a = Observable.create(s -> {
            s.onNext("test");
//            s.onError(new Exception());
            s.onComplete();
        });

        log.info("maybe thrown exception");
        a.subscribe(log::info);

        Completable.create(s -> {
            log.info("completable");
            s.onComplete();
        }).blockingAwait();
    }
}
