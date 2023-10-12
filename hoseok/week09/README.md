# 📌 17장: 리액티브 프로그래밍

리액티브 프로그래밍 패러다임의 중요성은 다음과 같은 이유로 증가하고 있습니다.

- 빅데이터: 페타바이트 단위의 빅데이터
- 다양한 환경: 모바일부터 수천 개의 멀티 코어 프로세서
- 사용 패턴: 항상 서비스를 사용

리액티브 프로그래밍에서는 다양한 시스템과 소스에서 들어오는 데이터 항목 스트림을 비동기적으로 처리하고 합쳐서 이런 문제를 해결할 수 있습니다.

<br><br>

## ✅ 17.1 리액티브 매니패스토

리액티브 매니패스토는 리액티브 애플리케이션과 시스템 개발의 핵심 원칙을 공식적으로 정의합니다.

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/90a1db16-640e-470f-8f45-3bcf886a1764)


- 반응성
    - 리액티브 시스템은 빠르지만, 일정하고 예상할 수 있는 반응 시간을 제공합니다.
- 회복성
    - 장애가 발생해도 시스템은 반응해야 한다.
    - 컴포넌트 실행 복제, 여러 컴포넌트의 시간과 공간 분리, 컴포넌트가 비동기적으로 작업을 다른 컴포넌트에게 위임 등 여러 회복성을 달성할 수 있는 기법 제공
- 탄력성
    - 애플리케이션 생명주기 동안 다양한 작업 부하를 받게 될때 자동으로 관련 컴포넌트에 할당된 자원수를 늘림
- 메시지 주도
    - 회복성, 탄력성을 지원하기 위해 약한 결합, 고립, 위치 투명성 등을 지원할 수 있도록 시스템을 구성하는 컴포넌트의 경계를 명확하게 정의해야 함
    - 이후 비동기 메시지를 컴포넌트끼리 전달해 통신을 함
    - 메시지를 통해 회복성(장애를 메시지로 처리), 탄력성(메시지의 수를 이용해 적절한 리소스 할당)을 얻을 수 있음

<br>

### 애플리케이션 수준의 리액티브

- **애플리케이션 수준 컴포넌트의 리액티브 프로그래밍의 주요 기능은 비동기로 작업을 수행할 수 있다는 점입니다.**
- 이를 위해 자바에선 리액티브 프레임워크와 라이브러리를 제공합니다.
- **이는 스레드보다 가볍고 동시성, 비동기 애플리케이션 구현의 추상화를 높여주어 블록, 경쟁 조건, 데드락같은 저수준의 멀티스레드 문제를 직접 처리할 필요가 없습니다.**
- 스레드를 쪼개는 기술을 이용할땐 메인 이벤트 루프 안에서는 절대 동작을 블록하지 않아야 한다는 전제 조건이 있습니다. → 블록 기간동안 다른 스트림이 처리되지 못함
    - 이러한 문제를 해결하기 위해 RxJava, Akka같은 리액티브 프레임워크는 별도로 지정된 스레드 풀에서 블록 동작을 실행 시켜 문제를 해결함
    - 따라서 메인 풀의 모든 스레드는 방해받지 않고 실행되므로 모든 CPU 코어가 가장 최적의 상황에서 동작할 수 있음
- 리액티브 시스템을 만들기 위해서는 훌륭하게 설계된 리액티브 애플리케이션 집합이 서로 잘 조화를 이루게 해야 함

<br>

### 시스템 수준의 리액티브

리액티브 시스템은 여러 애플리케이션이 한 개의 일관적인 플랫폼을 구성하고, 그 중 하나가 실패하더라도 전체 시스템이 계속 운영되는 소프트웨어 아키텍처입니다

`리액티브 애플리케이션은` 데이터 스트림에 기반한 연산을 수행하고 `이벤트 주도`로 분류 됩니다.

`리액티브 시스템의` 주요 속성으로는 `메시지 주도`가 있습니다.

- **이벤트, 메시지의 차이**
    - 메시지: 정의된 목적지 하나를 향함
    - 이벤트: 관련 이벤트를 관찰하도록 등록한 컴포넌트가 수신함

- **회복성의 핵심은 고립과 비결합**
    
    리액티브 아키텍처는 컴포넌트에서 발생한 장애를 고립시켜 다른 컴포넌트로 문제가 전파되지 않도록 하여 전체 시스템 장애가 발생하지 않도록 합니다. → 회복성 제공
    
    이런 맥락에서 `회복성 == 결함 허용 능력` 이라고 볼 수 있습니다.
    
    - 장애 발생 → 문제 자체를 격리하여 장애에서 완전하게 복구하여 정상 시스템으로 복구함

- **탄력성의 핵심은 위치 투명성**
    
    리액티브 시스템의 모든 컴포넌트가 수신자의 위치에 상관없이 다른 모든 서비스와 통신할 수 있음을 의미합니다.
    
    이러한 투명성 덕분에 시스템을 복제할 수 있고, 작업 부하에 따른 애플리케이션 확장을 쉽게 할 수 있습니다.
    
<br><br>

## ✅ 17.2 리액티브 스트림과 플로 API

리액티브 프로그래밍은 리액티브 스트림을 사용하는 프로그래밍 입니다.

리액티브 스트림은 잠재적으로 무한의 비동기 데이터를 non-blocking한 역압력에 전재해 순서대로 처리하는 표준 기술입니다.

- 역압력은 발행자가 이벤트를 제공하는 속도보다 느린 속도로 이벤트를 소비하여 문제가 발생하지 않도록 보장하는 장치

<br>

### Flow 클래스

발행-구독 모델을 지원할 수 있게 4개의 표준 인터페이스를 제공합니다.

- `Publisher`, `Subscriber`, `Subscription`, `Processor`

Publisher가 항목을 발행 → Subscriber가 한 번에 한개씩 혹은 여러개씩을 소비함, 이때 Subscription이 해당 과정을 관리할 수 있도록 Flow 클래스는 관련된 인터페이스 및 정적 메서드를 제공합니다.

- `Publisher`
    
    ```java
    public static interface Publisher<T> {
        public void subscribe(Subscriber<? super T> subscriber);
    }
    ```
    
    수 많은 일련의 이벤트를 제공할 수 있지만 Subscriber의 요구사항에 따라 역압력 기법에 의해 이벤트 제공 속도가 제한됨 (함수형 인터페이스임)
    
- `Subscriber`
    
    ```java
    public static interface Subscriber<T> {
       
        public void onSubscribe(Subscription subscription);
    
        public void onNext(T item);
    
        public void onError(Throwable throwable);
    
        public void onComplete();
    }
    ```
    
    Publisher가 발행한 이벤트의 리스너로 자신을 등록할 수 있음
    
- `Subscription`
    
    ```java
    public interface Subscription {
    		void request(long n);
    		
    		void cancel();
    }
    ```
    
    Publisher와 Subscriber 사이의 제어 흐름, 역압력을 관리함
    

- 프로토콜에서 정의한 순서로 지정된 메서드를 호출해야 함
    
    `onSubscribe onNext* (onError | onComplete)?`
    
    onSubscribe 메서드가 항상 처음에 호출되어야 하고, onNext는 여러번 호출할 수 있음, 이벤트 스트림은 영원히 지속되거나 onComplete 콜백을 통해 종료됨을 알릴 수 있음
    
    또한 onError를 통해 장애발생을 알릴 수 있습니다.
    

**Java 9의 Flow 명세서에서 언급한 인터페이스 구현의 협력 규칙**

- Publisher는 반드시 Subscription의 request 메서드에 정의된 개수 이하의 요소만 Subscriber에 전달해야 합니다.
- Subscriber는 요소를 받아 처리할 수 있음을 Publisher에 알려야 합니다.
    - onCompelete나 onError 신호를 처리하는 상황에서 Subscriber는 Publisher, Subscription의 어떤 메서드도 호출할 수 없음 → 구독이 취소됐다고 가정
    - Subscriber는 Subscription.request() 메서드 호출 없이도 언제든 종료 시그널을 받을 준비가 되어 있어야 합니다. 또한 cancel()이 호출된 이후에라도 한 개 이상의 onNext를 받을 준비가 되어 있어야 합니다.
- Publisher와 Subscriber는 정확하게 Subscription을 공유해야 하며 각각이 고유한 역할을 수행해야 합니다.
    - onSubscribe와 onNext메서드에서 Subscriber는 request 메서드를 동기적으로 호출할 수 있어야 합니다.
    - 표준에선 반복된 cancel호출은 한 번 호출한것과 같은 효과를 가져야 한다고 말합니다.

Flow API를 사용하는 리액티브 애플리케이션의 생명주기

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/cd944018-06a8-446d-9dfd-df27b9feedcd)


Processor 인터페이스는 Publisher와 Subscriber를 상속받은 인터페이스이며 리액티브 스트림에서 처리하는 이벤트의 변환 단계를 나타냅니다.

Subscriber 메서드 구현은 Publisher를 블록하지 않도록 강제하지만, 동기나 비동기중 어떤것으로 처리할지에 대해선 강제하지 않습니다.

모든 메서드는 void가 반환 타입이므로 온전히 비동기 방식으로 구현할 수 도 있습니다.

<br>

### 리액티브 애플리케이션 만들기

```java
// 구독자
public class TempSubscriber implements Subscriber<TempInfo> {

    private Subscription subscription;

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(TempInfo tempInfo) {
        System.out.println(tempInfo);
        subscription.request(1);
    }

    @Override
    public void onError(Throwable t) {
        System.err.println(t.getMessage());
    }

    @Override
    public void onComplete() {
        System.out.println("Done!");
    }

}

// 발행자와 구독자 사이를 이어주는 구독 중계자
public class TempSubscription implements Subscription {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final Subscriber<? super TempInfo> subscriber;
    private final String town;

    public TempSubscription(Subscriber<? super TempInfo> subscriber, String town) {
        this.subscriber = subscriber;
        this.town = town;
    }

    @Override
    public void request(long n) {
				// request의 호출 양상을 보면 재귀적으로 호출됨
				// 별도의 작업스레드에 맡기지 않는다면 StackOverflow발생함
        executor.submit(() -> {
            for (long i = 0L; i < n; i++) {
                try {
                    subscriber.onNext(TempInfo.fetch(town));
                } catch (Exception e) {
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

// 실행파트
public class Main {

    public static void main(String[] args) {
        getTemperatures("New York").subscribe(new TempSubscriber());
    }

    private static Publisher<TempInfo> getTemperatures(String town) {
        return subscriber -> subscriber.onSubscribe(new TempSubscription(subscriber, town));
    }

}
```

<br>

### Processor를 추가해 Publisher의 발행 데이터를 가공해 Subscriber에게 전달

```java
public class TempProcessor implements Processor<TempInfo, TempInfo> {

    private Subscriber<? super TempInfo> subscriber;

    @Override
    public void subscribe(Subscriber<? super TempInfo> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void onNext(TempInfo temp) {
        subscriber.onNext(new TempInfo(temp.getTown(), (temp.getTemp() - 32) * 5 / 9));
    }

    @Override
    public void onSubscribe(Subscription subscription) {
				// 구독자의 onSubscribe호출
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

public class MainCelsius {

    public static void main(String[] args) {
        getCelsiusTemperatures("New York").subscribe(new TempSubscriber());
    }

    public static Publisher<TempInfo> getCelsiusTemperatures(String town) {
        return subscriber -> {
            TempProcessor processor = new TempProcessor();
            processor.subscribe(subscriber);
						// 내부적으로 구독자의 onSubscribe를 호출함
						// 구독자는 processor의 onNext를 호출하므로, 섭씨 온도를 받아옴
            processor.onSubscribe(new TempSubscription(processor, town));
        };
    }

}
```

- Java는 예외적으로 Flow의 인터페이스만 제공하고 구현체는 제공하지 않습니다.
- 위 API가 만들어질 당시 이미 Akka, RxJava 같은 리액티브 스트림의 자바 코드 라이브러리가 이미 존재했기 때문
- 표준화를 위해 공식적으로 인터페이스를 만들어 리액티브 개념을 구현하도록 진화함

<br><br>

## ✅ 17.3 리액티브 라이브러리 RxJava 사용하기

RxJava는 넷플릿지의 Reactive Extension 프로젝트의 산물

- RxJava는 역압력을 제공하는 클래스가 별도로 분리되어 있습니다. → `io.reactivex.Flowable`
- 일반적인 Publisher 역할은 `io.reactivex.Observable` 클래스 (역압력만 분리되어 있음)
- Subscriber 역할은 `Observer` 인터페이스

```java
// Observable을 발행함
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

		// Stream API와 같이 map 연산을 제공 섭씨 제공 API
    public static Observable<TempInfo> getCelsiusTemperature(String town) {
        return getTemperature(town)
                .map(temp -> new TempInfo(temp.getTown(), (temp.getTemp() - 32) * 5 / 9));
    }

		// Stream API와 같이 filter 연산을 제공
    public static Observable<TempInfo> getNegativeTemperature(String town) {
        return getCelsiusTemperature(town)
                .filter(temp -> temp.getTemp() < 0);
    }

		// merge를 하게 되면 Iterable 타입의 데이터를 하나의 Observable로 합칠 수 있습니다.
    public static Observable<TempInfo> getCelsiusTemperatures(String... towns) {
        return Observable.merge(Arrays.stream(towns)
                .map(TempObservable::getCelsiusTemperature)
                .collect(toList()));
    }

}

// Subscriber 역할
public class TempObserver implements Observer<TempInfo> {

    @Override
    public void onComplete() {
        System.out.println("Done!");
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("Got problem: " + throwable.getMessage());
    }

    @Override
    public void onSubscribe(Disposable disposable) {
    }

    @Override
    public void onNext(TempInfo tempInfo) {
        System.out.println(tempInfo);
    }

}

public class Main {

    public static void main(String[] args) {
        Observable<TempInfo> observable = getTemperature("New York");
        observable.subscribe(new TempObserver());
				
				// RxJava의 연산이 데몬 스레드에서 실행되므로 충분한 시간을 기다리게해 main 스레드가 죽지 않도록 함
        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

// Processor 사용시 적용했던 섭씨 온도 변환 및 여러 발행자 생성
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
```

<br>

### Observable 변환하고 합치기

리액티브 스트림 커뮤니티는 마블 다이어그램이라는 시각적인 방법을 사용해 리액티브 API의 동작을 설명합니다. (말로는 복잡함)

아래 그림은 map, merge API에 대한 동작을 도식화한 `마블 다이어그램`

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/76a73a1f-10f9-4648-b4d4-0ce24e7217a9)


설명

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/e7df295a-0c7b-4521-b09b-da13f596c5e8)

<br><br>

# 📌 18장: 함수형 관점으로 생각하기


## ✅ 18.1 시스템 구현과 유지보수

많은 프로그래머가 시스템 유지보수를 할때 코드 크래시 디버깅 문제를 가장 많이 겪습니다. 이는 예상치 못한 변수값 때문에 발생할 수 있는 문제입니다.

함수형 프로그래밍의 `no side-effec`t와 `immutability`의 개념은 이 문제를 해결하는데 도움을 줄 수 있습니다.

<br>

### 공유된 가변 데이터

변수가 예상하지 못하는 값을 갖는 이유는 결국 시스템의 여러 메서드에서 공유된 가변 데이터 구조를 읽고 갱신하기 때문입니다.

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/197d2f79-b193-43fc-baa2-5975fa47ece5)


이를 방지하기 위해선 자신을 포함하는 클래스의 상태 그리고 다른 객체의 상태를 바꾸지 않으며 return 문을 통해서만 자신의 결과를 반환하도록 메서드를 작성해야 하며 이를 `순수 메서드` 또는 `부작용 없는 메서드`라고 부릅니다.

부작용은 다음과 같은 예가 있습니다.

- 자료구조를 고치거나, setter사용
- 예외 발생
- 파일에 쓰기 등의 I/O 동작 수행

혹은 불변 객체를 통해 부작용을 없앨 수 있습니다. → 객체의 상태를 바꾸지 않으므로 스레드에서 안전성을 제공함

위와 같이 부작용 없는 시스템의 개념은 함수형 프로그래밍에서 유래됨

<br>

### 선언형 프로그래밍

선언형 프로그래밍 이전에 어떻게 작업을 수행할지에 집중하는 방식인 명령형 프로그래밍이 있습니다.

이는 어떻게에 집중하는 프로그래밍 형식을 말합니다.

```java
// 어떻게 트랜잭션에서 데이터를 뽑아내는지에 집중
Transaction mostExpensive = transactions.get(0);
if(mostExpensive == null)
		throw new IllegalArgumentException("Empty list of transactions");
for(Transaction t: transactions.subList(1, transactions.size())) {
		if(t.getValue() > mostExpensive.getValue()) {
				mostExpensive = t;
		}
}
```

반대로 선언형 프로그래밍은 Stream API를 예를 들 수 있습니다.

내부 반복을 사용해 `무엇을`에 집중하며 시스템은 그 목표를 어떻게 달성할 것인지에 대한 규칙을 정의합니다. 문제 자체가 코드로 명확하게 드러난다는 장점이 있습니다.

```java
Optional<Transaction> mostExpensive =
    transactions.stream()
                .max(comparing(Transaction::getValue));
```

<br><br>

## ✅ 18.2 함수형 프로그래밍이란 무엇인가?

함수형 프로그래밍은 함수를 이용하는 프로그래밍이 입니다.

함수란 수학적인 함수를 의미하며, 0개 이상의 인수를 가지고 한 개 이상의 결과를 반환하지만 부작용이 없어야 합니다.

**부작용이 없기 위해서는 인수가 같았을때, 함수 호출의 결과는 항상 같은 결과가 반환 되어야 합니다.**

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/67a3e3a1-18ea-4ca8-9ed0-fc61779df0ca)


- **순수 함수형 프로그래밍**
    
    if-then-else 등의 수학적 표현만 사용하는 방식
    
- **함수형 프로그래밍**
    
    시스템의 다른 부분에 영향을 미치지 않는다면 내부적으로는 함수형이 아닌 기능도 사용
    
<br>

### 함수형 자바

자바는 순수 함수형 프로그래밍을 구현하기 어렵습니다.(Scanner와 같이 I/O모델 자체에 부작용 메서드 포함됨 → 억지 아님?)

따라서 자바에선 순수 함수형이 아닌 `함수형 프로그램`을 구현합니다. 실제 부작용이 있지만 아무도 이를 보지 못하게 함으로써 함수형을 달성할 수 있습니다.

**함수형의 특징들**

- **함수나 메서드는 지역 변수만을 변경해야 함수형**
- **함수나 메서드에서 참조하는 객체가 있다면 해당 객체는 불변이어야 함**
    - 객체의 모든 필드가 final, 모든 참조 필드는 불변 객체를 직접 참조해야 함
    - 메서드 내에서 생성한 객체의 필드는 갱신할 수 있음
    - 새로 생성한 객체의 필드 갱신이 외부에 노출되지 않아야 하고, 다음 메서드를 다시 호출할때 결과에 영향을 주면 안됨
- **함수형이라면 함수나 메서드가 어떤 예외도 일으키지 않아야 합니다.**
    - 비정상적인 입력값이 있을 때 자바에서는 예외를 일으키는 것이 자연스러움
    - 예외를 발생시키지 않는 대안으로 Optional<T>의 사용도 고려해볼 수 있음
- **함수형에서는 비함수형 동작을 감출 수 있는 상황에서만 부작용을 포함하는 라이브러리 함수를 사용해야 합니다.**

<br>

### 참조 투명성

- 같은 인수로 함수를 호출했을 때 항상 같은 결과를 반환한다면 참조적으로 `투명한 함수`라고 표현합니다.
- 함수는 어떤 입력이 주어졌을 때 언제, 어디서 호출하든 같은 결과를 생성해야 합니다.
- 이같은 참조 투명성은 특성 덕분에 memozation 혹은 캐싱을 통해 다시 계산하지 않고 저장하는 최적화 기능도 제공할 수 있습니다.

**참조 투명성과 관련된 작은 문제점**

- List 반환 메서드를 두 번 호출하면, 같은 요소를 가졌지만 서로 다른 메모리 공간에 생성된 2개의 리스트가 반환됨
- 두 개의 List가 가변 객체라면 참조 투명성을 만족할까?

**→ List를 불변의 순수값으로 사용할 것이라면 투명한 것으로 간주할 수 있고, 일반적으로 함수형 코드에서는 이런 함수를 참조적으로 투명한 것으로 간주합니다.**

<br>

### 함수형 실전 연습

```java
public class SubsetsMain {

    public static void main(String[] args) {
        List<List<Integer>> subs = subsets(Arrays.asList(1, 4, 9));
        subs.forEach(System.out::println);
    }

    public static <T> List<List<T>> subsets(List<T> l) {
        if (l.isEmpty()) {
            List<List<T>> ans = new ArrayList<>();
            ans.add(Collections.emptyList());
            return ans;
        }
        T first = l.get(0);
        List<T> rest = l.subList(1, l.size());
        List<List<T>> subans = subsets(rest); // 맨 앞 1개를 제외한 뒷부분을 계속 나눔
        List<List<T>> subans2 = insertAll(first, subans); // 위의 결과에 첫번째 요소를 더하여 새로운 결과 집합 추가
        return concat(subans, subans2); // 두 결과를 하나의 결과로 합침
    }

    public static <T> List<List<T>> insertAll(T first, List<List<T>> lists) {
        List<List<T>> result = new ArrayList<>();
        for (List<T> l : lists) {
						// 인수로 받은 lists에 직접 추가하면 외부 변수를 변경시킴 따라서 새로운 변수를 생성함
            List<T> copyList = new ArrayList<>();
            copyList.add(first);
            copyList.addAll(l);
            result.add(copyList);
        }
        return result;
    }

    static <T> List<List<T>> concat(List<List<T>> a, List<List<T>> b) {
				// 마찬가지임
        List<List<T>> r = new ArrayList<>(a);
        r.addAll(b);
        return r;
    }

}
```

- concat의 경우에는 a인수로 전달한 subans를 다시 참조하지 않으므로 직접 addAll을 해도 되지만, 후에 유지보수 관점에서 봤을때 위험성을 두고 사용하지 않는편이 좋음 (다른곳에서 사용할 수 있으므로)

**인수에 의해 출력이 결정되는 함수형 메서드의 관점에서 프로그램 문제를 생각하자! (무엇을 해야 하는가에 중점을 두자)**

<br><br>

## ✅ 18.3 재귀와 반복

while, for문을 사용하면 반복문 때문에 변화가 자연스럽게 코드에 스며들 수 있기에 순수 함수형 프로그래밍 언어에서는 해당 반복문을 포함하지 않습니다.

```java
// 호출자는 변화를 확인할 수 없으므로 문제 없음
Iterator<Apple> it = apples.iterator();
        while (it.hasNext()) {
           Apple apple = it.next();
// ...
}

// 아래 검색 알고리즘은 외부의 Stats 객체의 상태를 변화시키므로 문제가 됨
public void searchForGold(List<String> l, Stats stats){
		for(String s: l){
				if("gold".equals(s)){
						functionally stats.incrementFor("gold");
				} 
		}
}
```

이론적으로 위와 같은 문제는 재귀를 사용하면 해결할 수 있습니다

```java
// 반복문을 통한 팩토리얼 구하기: 매번 r과 i값이 갱신됨
public static int factorialIterative(int n) {
    int r = 1;
    for (int i = 1; i <= n; i++) {
        r *= i;
    }
    return r;
}

// 재귀함수를 이용한 팩토리얼 구하기: 좀 더 수학적인 형식으로 문제를 해결함
public static long factorialRecursive(long n) {
    return n == 1 ? 1 : n * factorialRecursive(n - 1);
}
```

다만 재귀 함수는 매번 스택프레임을 생성하므로 입력값에 비해 메모리 사용량이 큽니다.

자바에서는 지원하지 않지만 최신 JVM 언어인 스칼라, 그루비에선 지원해주는 `꼬리 호출 최적화 방식`도 존재합니다.

```java
// 일반적인 재귀호출
public static long factorialRecursive(long n) {
    return n == 1 ? 1 : n * factorialRecursive(n - 1);
}

// 꼬리 호출 최적화 방식 -> 하나의 스택 프레임을 재활용 함
public static long factorialTailRecursive(long n) {
    return factorialHelper(1, n);
}

public static long factorialHelper(long acc, long n) {
    return n == 1 ? acc : factorialHelper(acc * n, n - 1);
}
```

후자를 사용하면, 각 재귀 호출의 중간 결과를 별도의 스택 프레임에서 추적할 필요가 없으며, 팩토리얼 헬퍼의 첫 번째 인수로 직접 액세스할 수 있습니다.

자바는 지원하지 않지만 고전적인 재귀보다 여러 컴파일러 최적화 여지를 남겨둘 수 있는 꼬리 재귀를 적용하는것이 좋습니다.
