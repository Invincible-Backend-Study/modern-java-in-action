package org.example;

import static java.util.stream.Collectors.toList;

import io.reactivex.Observable;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class TempObservable {

    public static Observable<TempInfo> getTemperature(String town) {
        return Observable.create(emitter -> Observable.interval(1, TimeUnit.SECONDS).subscribe(i -> {
            if (!emitter.isDisposed()) {
                if (i >= 5) {
                    emitter.onComplete();
                } else {
                    try {
                        emitter.onNext(TempInfo.fetch(town));
                    } catch (Exception e) {
                        emitter.onError(e);
                    }
                }
            }
        }));
    }

    public static Observable<TempInfo> getCelsiusTemperature(String town) {
        return getTemperature(town)
                .map(temp -> new TempInfo(temp.town, (temp.temp - 32) * 5 / 9));
    }

    public static Observable<TempInfo> getNegativeTemperature(String town) {
        return getCelsiusTemperature(town)
                .filter(temp -> temp.temp < 0);
    }

    public static Observable<TempInfo> getCelsiusTemperatures(String... towns) {
        return Observable.merge(Arrays.stream(towns)
                .map(TempObservable::getCelsiusTemperature)
                .collect(toList()));
    }

}
