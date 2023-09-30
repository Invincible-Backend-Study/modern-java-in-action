package org.example;

import static org.example.TempObservable.getCelsiusTemperatures;

import io.reactivex.Observable;

public class MainCelsius {

    public static void main(String[] args) {
        Observable<TempInfo> observable = getCelsiusTemperatures("New York", "Chicago", "San Francisco");
        observable.subscribe(new TempObserver());

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
