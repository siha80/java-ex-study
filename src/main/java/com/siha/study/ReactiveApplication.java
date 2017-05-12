package com.siha.study;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class ReactiveApplication {
	public static void main(String[] args) throws InterruptedException {
		Publisher<Integer> pub = iterPub(Stream.iterate(1, i -> i + 1).limit(10).collect(Collectors.toList()));

		Publisher<String> mapPub = mapPub(pub, s -> "[" + s + "]");
		mapPub.subscribe(logSub());

		System.out.println("END");
//		Publisher<StringBuilder> reducePub = reducePub(pub, new StringBuilder(), (a, b) -> a.append(b + ","));
//		reducePub.subscribe(logSub());
	}

	private static <T, R> Publisher<R> reducePub(Publisher<T> pub, R init, BiFunction<R, T, R> bf) {
		return sub -> {
			pub.subscribe(new DelegateSub<T, R>(sub) {
				R result = init;

				@Override
				public void onNext(T t) {
					result = bf.apply(result, t);
				}

				@Override
				public void onComplete() {
					sub.onNext(result);
					sub.onComplete();
				}
			});
		};
	}

	private static <T, R> Publisher<R> mapPub(Publisher<T> pub, Function<T, R> f) {
		return new Publisher<R>() {
			@Override
			public void subscribe(Subscriber<? super R> sub) {
				pub.subscribe(new DelegateSub<T, R>(sub) {
					@Override
					public void onNext(T i) {
						sub.onNext(f.apply(i));
					}
				});
			}

		};
	}

	private static <T> Subscriber<T> logSub() {
		return new Subscriber<T>() {
			@Override
			public void onSubscribe(Subscription s) {
				log.debug("onSubscribe:");
				s.request(Long.MAX_VALUE);
			}

			@Override
			public void onNext(T i) {
				log.debug("onNext: {}", i);
			}

			@Override
			public void onError(Throwable t) {
				log.error("onError: {}", t);
			}

			@Override
			public void onComplete() {
				log.debug("onComplete:");
			}
		};
	}

	private static Publisher<Integer> iterPub(final List<Integer> iterater) {
		return new Publisher<Integer>() {
			Iterable<Integer> iter = iterater;

			@Override
			public void subscribe(Subscriber<? super Integer> sub) {
				sub.onSubscribe(new Subscription() {
					@Override
					public void request(long n) {
						try {
							iter.forEach(s -> sub.onNext(s));
							sub.onComplete();
						} catch(Throwable e) {
							sub.onError(e);
						}
					}

					@Override
					public void cancel() {

					}
				});
			}
		};
	}

}
