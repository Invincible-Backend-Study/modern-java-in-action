package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.function.IntConsumer;

public class Chapter15PlayGround {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public static void main(String... args) throws ExecutionException, InterruptedException {

        final var executorService = Executors.newSingleThreadExecutor();

        var result = new Result();

        int x = 1337;
        f(x, (int y) -> {
            result.right = y;
            System.out.println(result.left + result.right);
        });

        g(x, (int y) -> {
            result.left = y;
            System.out.println(result.left + result.right);
        });

        CompletableFuture<Integer> a = new CompletableFuture<>();
        executorService.submit(() -> a.complete(f1(x)));
        int b = g1(x);

        System.out.println(a.get() + b);
        executorService.shutdown();

        SimpleCell c1 = new SimpleCell("c1");
        SimpleCell c2 = new SimpleCell("c2");


    }

    private static int g1(int x) {
        return x;
    }

    private static Integer f1(int x) {
        return x;
    }

    static void f(int x, IntConsumer dealWithResult) {
    }

    static void g(int x, IntConsumer dealWithResult) {

    }

    static class Result {
        int left;
        int right;
    }

    static class SimpleCell implements Publisher<Integer>, Subscriber<Integer> {
        private final String name;
        private final List<Subscriber> subscribers = new ArrayList<>();
        private int value = 0;

        public SimpleCell(String name) {
            this.name = name;
        }

        @Override
        public void subscribe(Subscriber<? super Integer> subscriber) {
            subscribers.add(subscriber);
        }

        private void notifyAllSubscribers() {
            subscribers.forEach(subscriber -> subscriber.onNext(this.value));
        }

        @Override
        public void onSubscribe(Subscription subscription) {
        }

        @Override
        public void onNext(Integer item) {
            this.value = item;
            System.out.println(item);
            notifyAllSubscribers();
        }

        @Override
        public void onError(Throwable throwable) {

        }

        @Override
        public void onComplete() {

        }
    }
}
