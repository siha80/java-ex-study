package com.siha.study;

import rx.Observable;
import rx.Observer;
import rx.observables.SyncOnSubscribe;

public class ObservableExWithRx {
    public static void main(String[] args) {
        Observable<String> o = Observable.create(new SyncOnSubscribe() {
            @Override
            protected Object generateState() {
                return null;
            }

            @Override
            protected Object next(Object state, Observer observer) {
                return null;
            }
        });
    }
}
