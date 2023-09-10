package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.function.Consumer;

public class PubSubPlayGround {

    public static void main(String... args) {

        var c1 = new SimpleCell("c1");
        var c2 = new SimpleCell("c2");
        var c3 = new SimpleCell("c3");

        c1.subscribe(c3);

        c1.onNext(10);
        c2.onNext(20);

        System.out.println("===========");
        ArithmeticCell ac3 = new ArithmeticCell("AC3");
        SimpleCell ac1 = new SimpleCell("C1");
        SimpleCell ac2 = new SimpleCell("C2");

        ac1.subscribe(ac3::setLeft);
        ac2.subscribe(ac3::setRight);

        ac1.onNext(10);
        ac2.onNext(20);
        ac1.onNext(15);

    }

    static class SimpleCell implements Publisher<Integer>, Subscriber<Integer> {
        private final String name;
        private final List<Subscriber<? super Integer>> subscribers = new ArrayList<>();
        private int value = 0;

        public SimpleCell(String name) {
            this.name = name;
        }

        @Override
        public void subscribe(Subscriber<? super Integer> subscriber) {
            subscribers.add(subscriber);
        }

        public void subscribe(Consumer<? super Integer> onNext) {
            subscribers.add(new Subscriber<>() {

                @Override
                public void onComplete() {
                }

                @Override
                public void onError(Throwable t) {
                    t.printStackTrace();
                }

                @Override
                public void onNext(Integer val) {
                    onNext.accept(val);
                }

                @Override
                public void onSubscribe(Subscription s) {
                }

            });
        }

        @Override
        public void onSubscribe(Subscription subscription) {

        }

        @Override
        public void onNext(Integer item) {
            this.value = item;
            System.out.println(this.name + ":" + this.value);
            notifyAllSubscribers();
        }

        @Override
        public void onError(Throwable throwable) {

        }

        @Override
        public void onComplete() {

        }

        private void notifyAllSubscribers() {
            subscribers.forEach(subscriber -> subscriber.onNext(this.value));
        }
    }

    static class ArithmeticCell extends SimpleCell {
        private int left;
        private int right;

        public ArithmeticCell(String name) {
            super(name);
        }

        public void setLeft(int left) {
            this.left = left;
            onNext(left + this.right);
        }

        public void setRight(int right) {
            this.right = right;
            onNext(right + this.left);
        }
    }
}
