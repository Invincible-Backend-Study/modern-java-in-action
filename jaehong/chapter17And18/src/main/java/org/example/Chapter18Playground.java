package org.example;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow.Processor;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Chapter18Playground {

    public static void main(String... args) {
        //getTemperatures("new York").subscribe(new TempSubscriber());

        getCelsiusTemperatures("New York")
                .subscribe(new TempSubscriber());

    }

    private static Publisher<TempInfo> getTemperatures(String town) {
        return subscriber -> subscriber.onSubscribe(new TempSubscription(subscriber, town));
    }

    private static Publisher<TempInfo> getCelsiusTemperatures(String town) {
        return subscriber -> {
            TempProcessor processor = new TempProcessor();
            processor.subscribe(subscriber);
            processor.onSubscribe(new TempSubscription(processor, town));
        };
    }
}


class TempInfo {
    public static final Random RANDOM = new Random();

    final String town;
    final int temp;

    public TempInfo(String town, int temp) {
        this.town = town;
        this.temp = temp;
    }

    // 정적 팩토리 메서드를 이용해 해당 도시의 TempInfo 인스턴스를 만듦
    public static TempInfo fetch(String town) {

        // 10분의 1확률로 온도 가져오기 작업이 실패한다.
//        if (RANDOM.nextInt(10) == 0) {
//            throw new RuntimeException("Errror!");
//        }
        return new TempInfo(town, RANDOM.nextInt(100)); // 0 ~ 99 사이에서 임의의 화씨 온도를 반환
    }


    @Override
    public String toString() {
        return "TempInfo{" +
                "town='" + town + '\'' +
                ", temp=" + temp +
                '}';
    }
}


class TempSubscription implements Subscription {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final Subscriber<? super TempInfo> subscriber;
    private final String town;


    public TempSubscription(Subscriber<? super TempInfo> subscriber, String town) {
        this.subscriber = subscriber;
        this.town = town;
    }

    @Override
    public void request(long n) {
        executor.submit(() -> {
            for (long i = 0L; i < n; i++) {
                try {
                    // 현재 온도를 subscriber로 전달
                    subscriber.onNext(TempInfo.fetch(town));
                } catch (Exception e) {
                    // 온도 가져오기를 실패하면 Subscriber로 에러를 전달
                    subscriber.onError(e);
                    break;
                }
            }
        });
    }

    @Override
    public void cancel() {
        subscriber.onComplete();
    }
}

class TempSubscriber implements Subscriber<TempInfo> {
    private static final Logger log = LoggerFactory.getLogger(TempSubscriber.class);

    private Subscription subscription;

    @Override
    public void onSubscribe(Subscription subscription) {
        // 구독을 저장하고 첫 번째 요청을 전달
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(TempInfo item) {
        // 수신한 온도를 출력하고 다음 정보를 요청
        log.info("{}", item);
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        log.error(throwable.getMessage());
    }

    @Override
    public void onComplete() {
        log.info("Done!");
    }
}

class TempProcessor implements Processor<TempInfo, TempInfo> {

    private Subscriber<? super TempInfo> subscriber;

    @Override
    public void subscribe(Subscriber<? super TempInfo> subscriber) {
        this.subscriber = subscriber;
    }


    @Override
    public void onNext(TempInfo item) {
        // 섭씨로 변환해서 다음 TempInfo를 전송
        subscriber.onNext(new TempInfo(item.town, (item.temp - 32) * 5 / 9));

    }


    //********************************
    // 아래 존재하는 모든 메서드는 업스트림 구독자에 전달
    //********************************
    @Override
    public void onSubscribe(Subscription subscription) {
        subscriber.onSubscribe(subscription);
    }

    @Override
    public void onError(Throwable throwable) {
        subscriber.onError(throwable);
    }

    @Override
    public void onComplete() {
        subscriber.onComplete();
    }
}