# 📌 15장: CompletableFuture와 리액티브 프로그래밍 컨셉의 기초

- 동시성과 병렬성의 차이
    
    ![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/ea3ed808-d392-462f-af5c-0be507252288)

    
<br>

## ✅ 15.1 동시성을 구현하는 자바 지원의 진화

- 초기 자바는 Runnable, Thread, 동기화된 클래스와 메서드를 이용한 잠금 등을 이용해 동시성을 지원했습니다.
- Java 5부터는 ExecutorService 인터페이스, 제네릭을 사용하는 Callable<T>, Future<T>을 지원합니다. 이는 Runnable, Thread의 상위수준의 결과 변형을 생성합니다.

- 자바 버전이 증가하며 개선된 동시성 지원이 추가됨
    - Java 7의 java.util.concurrent.RecursiveTask
    - Java 8의 람다와 스트림(병렬지원), `CompletableFuture`
    - Java 9의 발행 구독 프로토콜(java.util.concurrent.`Flow`) 등
    - CompletableFuture, Flow의 궁극적인 목표는 가능한 동시에 실행할 수 있는 독립 태스크를 늘리고, 쉬운 병렬성 이용입니다.

<br>

### Executor와 스레드 풀

Java5의 Executor 프레임워크와 스레드 풀을 통해 프로그래머가 태스크 제출과 실행을 분리할 수 있는 기능을 제공함

- **스레드의 문제**
    
    자바 스레드는(유저 스레드) 직접 운영체제 스레드(커널 스레드)에 접근하여 하나씩 감쌉니다.
    
    ![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/1e3b32b7-384c-4ee5-852b-6e103f24a30e)

    
    https://findstar.pe.kr/2023/04/17/java-virtual-threads-1/
    
    따라서 운영체제가 지원하는 스레드 수를 초과해선 안되며, 기존 스레드 실행 상황에서 새로운 스레드를 계속해서 생성하지 않도록 주의해야 합니다.
    
    운영체제 및 자바의 스레드 수는 실제 하드웨어 스레드 개수보다 많습니다. 따라서 최적의 유저 스레드의 수는 하드웨어 코어의 수에 따라 달라집니다.
    
    **즉, 자바의 스레드가 아무리 많아도 동시에 실행될 수 있는 스레드의 수는 하드웨어의 코어(정확히는 스레드)에 의해 결정됩니다.**
    

- **스레드 풀, 그리고 스레드 풀이 좋은 이유**
    
    스레드 풀을 사용하면 하드웨어에 맞는 수의 스레드를 미리 생성하고 사용하고, 태스크 또한 수에 맞게 유지할 수 있습니다. 또한 작업 큐의 크기 조정, 거부 정책, 태스크 종류에 따른 우선순위 설정 등 다양한 설정을 할 수 있습니다.
    

- **스레드 풀, 그리고 스레드 풀이 나쁜 이유**
    
    스레드 풀은 크게 2가지 사항을 조심해야 합니다.
    
    1. K 스레드를 가진 스레드 풀은 K개 만큼의 스레드를 동시에 실행할 수 있습니다. 초과로 제출된 태스크는 작업큐에 저장됩니다.
        
        ![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/ff40a240-2332-46ce-9e92-61b1d0b38529)

        
        위 그림은 동시에 5개의 스레드가 실행 가능할때 20개의 작업이 제출된 상황입니다. 
        여기에서 3개의 스레드가 sleep 혹은 I/O를 대기 중이라면 나머지 2개의 스레드만 가지고 남은 태스크를 실행해야 합니다.
        
        위와 같은 상황이 심각해지면 데드락에 걸릴 수 있습니다. 따라서 블록이 될 수 있는 태스크는 스레드 풀에 제출하지 말아야 하지만, 항상 지키기에는 어려움이 있습니다.
        
    
    1. main이 반환하기 전에 모든 스레드가 끝나는 것이 이상적이지만, 스레드는 자신의 태스크가 끝나면 종료됩니다. 반대로 main이 끝났지만 스레드가 아직 작업중인 경우도 있습니다.
        
        이러한 상황을 다루기 위해 `Thread.setDaemon` 메서드를 제공합니다.
        
        ![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/44a9e96d-1d04-4b4c-ba17-83a2de43020d)

        
<br>   

### 스레드의 다른 추상화: 중첩되지 않은 메서드 호출

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/7e908f71-260c-4188-8f1b-7ad183e3fce3)


https://citeseerx.ist.psu.edu/document?repid=rep1&type=pdf&doi=5d29c15ce22a23ee11f09c6eda855596ff965de4

- 7장에서 다루었던 포크/조인 프레임워크와 지금 설명하고자 하는 동시성은 차이점이 있음
- 포크/조인 프레임워크는 `Strict Fork/Join 모델`로 **스레드 생성과 join()이 한 쌍처럼 중첩**되어 있고 이것이 메서드 호출내에 추가되어 있습니다.
- 위 그림처럼 하위 작업들이 반드시 끝나야 상위 작업들이 다음으로 진행할 수 있습니다.

15장에선 사용자의 메서드 호출에 의해 스레드가 생성되고 메서드를 벗어나도 계속 실행되는 동시성 형태를 말하고자 합니다. (여유로운 포크/조인)

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/5c9550b9-9d72-4747-94ee-aa29551caeda)


다만 메서드 반환 이후에도 생성된 태스크가 계속 실행되므로 몇가지 위험성이 존재합니다.

- 스레드 실행은 메서드를 호출한 다음의 코드와 동시에 실행됨 → Race Condition을 주의해야 함
- 실행 중인 스레드가 종료되지 않은 상황에서 main()메서드가 반환되면 다음 두가지 선택지가 있습니다.
    - 모든 스레드가 실행을 끝날때까지 기다림
    - 애플리케이션 종료를 방해하는 스레드를 강제 종료하고 애플리케이션 종료
    
    하지만 두 방법 모두 안전하지 않습니다. 따라서 Thread.setDaemon()을 활용합니다.
    데몬 스레드는 main이 종료되면 강제 종료 할 수 있기에 데이터의 일관성을 파괴하지 않는 작업에 유용합니다.
    
    그 외의 작업은 비데몬 스레드로 두어 main 메서드가 해당 작업이 끝날때까지 기다리도록 합니다.
    
<br>

### 스레드에 무엇을 바랄까?

핵심은 모든 하드웨어 스레드를 활용해 병렬성의 장점을 극대화하도록 프로그램을 만드는 것입니다.
조금더 내부로 들어가면 프로그램을 작은 태스크 단위로 구조화하는 것이 목표입니다.

<br><br>

## ✅ 15.2 동기 API, 비동기 API

$f(x) + g(x) = result$

f(x)와 g(x)의 작업이 긴 시간이 걸린다면 두 작업을 순차적으로 실행하기 보다는 동시에 시작하는것이 둘 중에 더 긴 작업의 시간까지 줄일 수 있는 방법이 됩니다.

하지만 일반적인 Thread를 활용해 구현을 하게 된다면 꽤 번잡한 코드가 됩니다.

```java
class ThreadExample {
    public static void main(String[] args) throws InterruptedException {
        int x = 1337;
        Result result = new Result();
        Thread t1 = new Thread(() -> { result.left = f(x); } );
        Thread t2 = new Thread(() -> { result.right = g(x); });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(result.left + result.right);
		}

    private static class Result {
        private int left;
        private int right;
		}
}
```

Future API를 활용하면 더 단순화 시킬 수 있습니다.

```java
public class ExecutorServiceExample {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int x = 1337;

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<Integer> y = executorService.submit(() -> fo(x));
        Future<Integer> z = executorService.submit(() -> go(x));
        System.out.println(y.get() + z.get());

        executorService.shutdown();
    }

}
```

그럼에도 불필요한 코드로 오염된 부분이 보입니다.

스트림에서 명시적인 반복을 내부 반복으로 바꾼 작업것처럼 변경할 수 있는데 여기서 `비동기 API`를 사용합니다.

크게 두가지 방법이 존재합니다.

1. Java 8에서 `CompletableFuture`가 등장하면서 Future를 조합할 수 있습니다.
2. 발행-구독 프로토콜에 기반한 Java 9의 `java.util.concurrent.Flow`를 사용할 수 있습니다.

<br>

### Future 형식 API

```java
// 대안을 사용했을때 변경되는 시그니처
Future<Integer> f(int x);
Future<Integer> g(int x);

// 호출 변경
Future<Integer> y = f(x); // 비동기
Future<Integer> z = g(x); // 비동기
System.out.println(y.get() + z.get());
```

f, g호출 모두 호출 즉시 태스크를 실행하여 결과 Future를 반환합니다. get()메서드는 해당 태스크가 끝나길 기다리고 결과를 반환하게 됩니다.

- f(x) 호출만 비동기로 동작하도록 해도 되지만 g에도 Future형식이 필요할 수 있으므로 통일 시켜주는 편이 좋습니다.
- 또한 병렬 하드웨어로 속도를 올리기 위해서는 태스크를 합리적인 작은 크기로 나누는것이 좋습니다.

<br>

### 리액티브 형식 API

두 번째 대안으로 f, g의 시그니처 변경을 통해 콜백 형식의 프로그래밍을 이용합니다.

```java
public class CallbackStyleExample {

    public static void main(String[] args) {

        int x = 1337;
        Result result = new Result();

        f(x, (int y) -> {
            result.left = y;
            System.out.println((result.left + result.right));
        });

        g(x, (int z) -> {
            result.right = z;
            System.out.println((result.left + result.right));
        });
    }

    private static class Result {
        private int left;
        private int right;
    }

    private static void f(int x, IntConsumer dealWithResult) {
        dealWithResult.accept(Functions.f(x));
    }

    private static void g(int x, IntConsumer dealWithResult) {
        dealWithResult.accept(Functions.g(x));
    }

}
```

하지만 먼저 결과가 계산되면 다른쪽의 결과를 기다리지 않고 먼저 출력하게 됩니다.

위의 결과에서 좀 더 보완해주는 방법으론 그나마 Future API를 사용하는것이 적절해보입니다.

- **Future API와 리액티브 스타일의 비동기 API 적절한 사용**
    - Future는 한 번만 결과를 생성하게 되고 get()을 통해 결과를 얻습니다. 따라서 일회성의 값을 처리하는것에 적절합니다.
    - 반면 리액티브 스타일의 비동기 API는 자연스럽게 값의 시퀀스를 구해야 하는쪽에 사용하는것이 적절합니다.

- **비동기 API를 사용할만한 곳**
    - 계산이 오래 걸리는 메서드(수 밀리초 이상)
    - 네트워크나 사람의 입력을 기다리는 메서드에 활용

<br>

### 잠자기(그리고 기타 블로킹 동작)는 해로운 것

스레드는 잠들어도 여전히 시스템 자원을 점유합니다. 정말 많은 스레드가 잠을자게 되면 큰 문제가 발생할 수 있습니다.

이런 블록 동작은 크게 어떤 동작을 완료하길 기다리는 동작과 외부 상호작용을 기다리는 두 가지 동작으로 구분됩니다. 

이럴때 태스크 앞과 뒤 두부분으로 나누고 블록되지 않은 상황에서 뒷 부분을 스케줄링하도록 요청하면 스레드가 블록되는 상황을 막을 수 있습니다.

```java
public class ScheduledExecutorServiceExample {

    public static void main(String[] args) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

        work1();
        scheduledExecutorService.schedule(ScheduledExecutorServiceExample::work2, 10, TimeUnit.SECONDS);

        scheduledExecutorService.shutdown();
    }

    public static void work1() {
        System.out.println("Hello from Work1!");
    }

    public static void work2() {
        System.out.println("Hello from Work2!");
    }

}
```

work2는 work1이 끝나고 10초뒤에 실행되도록 스케줄링 되어있습니다.

Thread.sleep(10000)과의 차이점으로는 sleep은 블록되는 동안 스레드의 자원을 점유하지만, 위 코드의 방식은 현재 태스크를 종료하고 10초뒤에 새로운 태스크를 제출하여 블록되는 상황을 막습니다.

아직은 코드가 추상화되지 않아 복잡하지만 이후 새로 등장한 CompletableFuture 인터페이스를 사용하면 라이브러리 내부에 추상화 됩니다.

<br>

### 비동기 API에서의 예외 처리

비동기 작업은 다른 스레드에서 호출되므로 해당 스레드에서 발생한 예외는 호출자와 관계 없는 상황이 됩니다.

**CompletableFuture와 Flow API에선 적절한 예외 처리를 제공해줍니다.**

- 예시로 리액티브 형식에선 여러 콜백 메서드를 감싼 인터페이스를 통해 “번호가 나왔습니다”, “잘못된 형식의 예외가 발생했습니다”, “처리할 데이터가 없습니다”와 같이 이벤트가 발생하고 능동적인 생산자와 같은 알림을 구현합니다. (17장)
    
    또한 이런 리액티브 형식은 이벤트가 발생하는 순서는 개의치 않습니다.
    
<br><br>

## ✅ 15.3 박스와 채널 모델

동시성 모델을 가장 잘 설계하고 개념화 하기 위해 `박스와 채널 모델`이 존재합니다.

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/1af5a0b8-188d-476f-a80d-8f20f13cf35b)


- p함수에 x인자를 전달해 결과를 생성
- 그 결과를 q1, q2에 전달
- q1, q2의 결과를 r함수에 전달하여 최종 출력

위 설계를 기반으로 원하는 작업을 비동기 작업을 구현할 수 있습니다.

```java
int t = p(x); // 먼저 처리해야 함 반드시
Future<Integer> a1 = executorService.submit(() -> q1(t));
Future<Integer> a2 = executorService.submit(() -> q2(t));
System.out.println( r(a1.get(),a2.get())); // 무조건 마지막에 끝내야 함
```

위 Future활용 코드는 첫번째와 마지막 작업이 반드시 첫번째와 마지막에 실행되어야 합니다. 따라서 병렬성을 제대로 활용하지 못하고 있습니다.

이를 보완하기 위해 CompletableFuture에선 콤비네이터를 제공해 문제를 해결합니다. (다음절)

아무튼 박스와 채널 모델을 사용하면 생각과 코드를 구조화할 수 있습니다. 또한 대규모 시스템을 구현할때 이를 통해 추상화 수준을 높일 수 있습니다.

**결국 박스 채널 모델은 외부에서 직접 처리하던 병렬성을 내부작업으로 처리하는 관점으로 바꿔줍니다. with Combinator**

<br><br>

## ✅ 15.4 CompletableFuture와 콤비네이터를 이용한 동시성

`CompletableFuture` 는 Future를 조합할 수 있는 기능을 제공합니다.

하지만, 즉시 결과를 계산하는 Future와 달리 CompletableFuture는 실행할 코드 없이 Future를 만들 수 있습니다.

그리고 후에 complete() 메서드를 이용해 특정 값을 이용해 다른 스레드가 결과를 계산하도록 하고 get()을 통해 값을 얻습니다.

f(x)와 g(x)를 동시에 실행해 합계를 구하는 코드를 다음과 같이 작성합니다.

```java
public class CFComplete {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        int x = 1337;

        CompletableFuture<Integer> a = new CompletableFuture<>();
        executorService.submit(() -> a.complete(f(x))); // 이때 워커 스레드에서 complete연산이 실행됩니다.
        int b = g(x);
				// f(x) 혹은 g(x)의 연산이 끝나지 않은 상황에서 get()을 기다리는 상황이 발생할 수 있음 
        System.out.println(a.get() + b);

        executorService.shutdown();
    }

}
```

CompletableFuture의 조합 동작을 이용해 문제점을 해결하고 다시 구현하면 다음과 같습니다.

```java
public class CFCombine {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        int x = 1337;

        CompletableFuture<Integer> a = new CompletableFuture<>();
        CompletableFuture<Integer> b = new CompletableFuture<>();
        CompletableFuture<Integer> c = a.thenCombine(b, (y, z) -> y + z);
        executorService.submit(() -> a.complete(f(x)));
        executorService.submit(() -> b.complete(g(x)));

        System.out.println(c.get());
        executorService.shutdown();
    }

}
```

위 thenCombine 메서드는 두 개의 CompletableFuture값을 받아서 한 개의 새 값을 만듭니다.

핵심은 결과를 추가하는 c 연산은 a와 b의 작업이 끝나기 전까지 먼저 블록된 상태에 있지 않습니다. (연산이 끝나야 태스크로 추가됨)

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/209e0617-0e9a-4c39-93de-fc6f32094180)

<br><br>

## ✅ 15.5 발행-구독 그리고 리액티브 프로그래밍

리액티브 프로그래밍은 데이터 스트림과 변화에 반응하는 시스템을 구축하기 위한 프로그래밍 패러다임 입니다.

한 번만 결과를 계산하는 Future와 달리 리액티브 프로그램은 변화에 대응한 결과나 나옵니다. (온도계, 네트워크 결과 등)

Java 9에서는 java.util.concurrent.Flow의 인터페이스에 발행-구독자 모델을 적용해 리액티브 프로그래밍을 제공합니다.

또한 3가지로 Flow API를 정리합니다.

1. 구독자가 구독할 수 있는 발행자
2. 이 연결을 구독이라 함
3. 이 연결을 이용해 메시지(또는 이벤트)를 전송함

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/941492f7-f525-4b0b-8b58-ecc909f4042b)

<br>

### 두 Flow를 합치는 예제

```java
public class SimpleCell implements Publisher<Integer>, Subscriber<Integer> {

    private int value = 0;
    private String name;
    private List<Subscriber<? super Integer>> subscribers = new ArrayList<>();

    public static void main(String[] args) {
        SimpleCell c3 = new SimpleCell("C3");
        SimpleCell c2 = new SimpleCell("C2");
        SimpleCell c1 = new SimpleCell("C1");

        c1.subscribe(c3);

        c1.onNext(10); // C1의 값을 10으로 갱신
        c2.onNext(20); // C2의 값을 20으로 갱신
    }

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

    private void notifyAllSubscribers() {
        subscribers.forEach(subscriber -> subscriber.onNext(value));
    }

    @Override
    public void onNext(Integer newValue) {
        value = newValue;
        System.out.println(name + ":" + value);
        notifyAllSubscribers();
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @Override
    public void onSubscribe(Subscription s) {
    }

}
```

데이터가 발행자에서 구독자로 흐르는것에 착안해 이를 업스트림이라고 부르고, 반대의 경우는 다운스트림이라고 부릅니다.

newValue가 onNext()의 인자로 전달되고(업스트림), notifyAllSubscribers()메서드를 통해 구독자에게 데이터를 줍니다.(다운스트림)

onNext외에 onError, onComplete를 통해 데이터 흐름에서 발생한 예외, 흐름의 종료를 알 수 있습니다. 따라서 Flow API는 기존의 옵저버 패턴보다 더 강력해진 API를 제공합니다.

Flow 인터페이스는 압력과 역압력이라는 개념이 존재합니다.

- **압력**: 초당 한~두개씩 보고하던 데이터가 밀리초로 바뀌는 경우 → 단일 시간에 더 많은 이벤트가 발행되어야 함
- **역압력**: 정보의 흐름을 Subscriber → Publisher로 넘김 즉, 요청했을때만 발행자가 아이템을 보낼 수 있도록 하는 역흐름을 말합니다.

이를 위해 `Subscription` 객체의 메서드를 통해 Subscriber와 Publisher간 통신을 합니다.

```java
interface Subscription {
		void cancel();
		void request(long n);
}
```

→ Publisher는 Subscription 객체를 만들어 Subscriber로 전달하면 Subscriber는 이를 이용해 Publisher로 정보를 보냄

<br>

### 역압력의 간단한 형태

한 번의 한 개의 이벤트를 처리하도록 발행-구독 연결을 구성하기 위해선 다음 작업이 필요함

- Subscriber가 onSubscribe로 전달된 Subscription 객체를 필드에 로컬로 저장함
- Subscriber가 수 많은 이벤트를 받지 않도록 onSubscribe, onNext, onError의 마지막 동작에 `channel.request(1)`을 추가해 오직 한 이벤트만 요청함
- 요청을 보낸 채널에만 onNext, onError 이벤트를 보내도록 Publisher의 notifyAllSubscribers 코드를 바꿈(각 구독자가 자신의 속도를 유지하도록 새로운 Subscription들을 각 구독자와 연결함)

역압력은 구현하면서 여러가지 장단점을 생각해야 합니다.

- 여러 구독자가 있을때 이벤트를 가장 느린 속도로 보낼것인가? 아니면 구독자에게 보내지 않은 데이터를 저장할 별도의 큐를 두나?
- 별도의 큐를 두었을때 큐가 너무 커지면?
- 구독자가 준비되지 않았다면 큐의 데이터는 폐기?

위와 같은 질문의 답변은 데이터의 성격에 따라 달라집니다. 위 예시들은 마치 구독자가 발행자로부터 요청을 당겨가는것 같이 보인다 하여 Reactive pull-based라고 불립니다.

<br><br>

## ✅ 15.6 리액티브 시스템 vs 리액티브 프로그래밍

- 리액티브 시스템은 런타임 환경이 변화에 대응하도록 전체 아키텍처가 설계된 프로그램을 가리킴 해당 시스템이 가져야 할 공식적인 속성은 Reactive Manifesto에서 확인할 수 있음 (반응성, 회복성, 탄력성)
- 반응성은 실시간으로 입력에 반응함을 말함 (큰 작업 처리시 간단한 질의의 응답을 무시하지 않고)
- 회복성은 특정 컴포넌트의 실패가 전체 시스템의 실패로 이어지지 않음을 의미
- 탄력성은 각 작업큐가 원활하게 처리될 수 있도록 서비스와 관련된 작업자 스레드를 적절하게 재배치함을 말함

여러 방법으로 위의 속성에 맞게 구현할 수 있지만 java.util.concurrent.Flow 인터페이스에서 제공하는 리액티브 프로그래밍 형식을 이용하는 것도 주요 방법중 하나입니다.

결국 리액티브 프로그래밍을 이용해 리액티브 시스템을 구현할 수 있습니다.

<br><br><br>

# 📌 16: CompletableFuture: 안정적 비동기 프로그래밍

## ✅ 16.2 Future의 제한

Future 인터페이스는 비동기 계산이 끝났는지 확인하거나 계산이 끝나길 기다리기, 결과 회수 메서드와 같은 기능을 제공하지만 간결한 동시 실행 코드를 구현하기에 충분하지 않습니다. 또한 여러 Future 결과가 있을때 이들의 의존성을 표현하는데 제약이 있습니다.

CompletableFuture 클래스를 이용하면 람다 표현식과 파이프라이닝을 이용해 여러 작업들을 구현할 수 있습니다. 이는 Collection과 Stream의 관계로 생각할 수 있습니다.

<br>

### CompletableFuture로 비동기 애플리케이션 만들기

여러 온라인 상점 중 가장 저렴한 가격을 제시하는 상점을 찾는 애플리케이션을 완성해가는 예제는 CompletableFuture를 이용해 만들어갑니다.

다음과 같은 기술을 배우고 적용합니다.

- 고객에게 비동기 API를 제공함
- 동기 API를 사용해야 할 때 코드를 non-blocking하는 방법을 배움
    - 두 개의 비동기 동작을 파이프라이닝 (A → B → C작업을 비동기로 파이프라이닝)
        - 두 개의 동작 결과를 하나의 비동기 계산으로 합치기 (A결과를 B에 input해 최종 결과를 계산할 수 있도록 함)
- 비동기 동작 완료에 대응하는 방법 배우기 (계산이 완료될때마다 즉시 결과를 보여줄 수 있도록)

<br>

### 동기 API와 비동기 API

동기, 비동기는 항상 blocking, non-blocking과 따라다닙니다.

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/dae97935-6ca6-46b7-8a71-686340c57541)


일반적으로 동기 API는 특정 메서드를 호출하고 해당 메서드의 결과를 기다리는 것이라고 생각합니다.
하지만 여기에서는 Blocking이라는 개념도 적용되어 있습니다.

특정 메서드를 호출하면 해당 메서드로 제어권이 넘어가고, 호출자는 기다립니다. 이를 Blocking이라고 합니다.

따라서 정확하게는 Sync-Blocking으로 볼 수 있습니다.

반면에 비동기 API는 특정 메서드를 호출해도 호출한 쪽에서는 다음 코드를 진행하고, 호출된 메서드도 실행을 동시적으로 진행합니다. 이는 제어권이 호출자쪽에 있는것으로 non-blocking 상황을 말합니다.

따라서 Async-NonBlocking으로 볼 수 있습니다.

<br><br>

## ✅ 16.2 비동기 API 구현

### 동기 메서드를 비동기 메서드로 변환

```java
// 동기 메서드
public double getPrice(String product) {
    return calculatePrice(product);
}

private double calculatePrice(String product) {
    delay();
    return random.nextDouble() * product.charAt(0) + product.charAt(1);
}

// 비동기 메서드
public Future<Double> getPriceAsync(String product) {
    CompletableFuture<Double> futurePrice = new CompletableFuture<>();
    new Thread(() -> {
        double price = calculatePrice(product);
        futurePrice.complete(price);
    }).start();
    return futurePrice;
}
```

비동기 메서드를 살펴보면, 1초의 delay()가 발생하는 calculatePrice 메서드를 별도의 스레드에서 호출하도록 진행하고 있습니다.

이후 Future<Double> 타입의 futurePrice를 반환하고, 받는 측에선 1초의 시간이 지나고 결과를 꺼낼 수 있습니다.

따라서 오래 걸리는 계산 결과를 기다리지 않고 결과를 포함할 Future 인스턴스를 바로 반환합니다.

위 코드를 사용하는 측에서 futurePrice.get()을 할때 값이 영원히 계산되지 않는 경우도 존재합니다. 이런 경우 클라이언트가 블록되는 상황으로 이를 방지하기 위해 Future의 작업이 끝났을때만 이를 통지받는 람다 표현식이나 메서드 참조로 정의된 콜백 메서드를 실행하도록 합니다.

<br>

### 에러 처리 방법

에러가 발생하면 해당 스레드내에서만 영향을 받게 되므로 타임아웃을 활용하여 블록처리를 막을 수 있습니다. 

또한 스레드 내부에서 발생한 예외를 클라이언트로 전달할 수 있습니다.

```java
public Future<Double> getPriceAsync(String product) {
    CompletableFuture<Double> futurePrice = new CompletableFuture<>();
    new Thread(() -> {
        try {
            double price = calculatePrice(product);
            futurePrice.complete(price);
        } catch (Exception ex) {
            futurePrice.completeExceptionally(ex);
        }
    }).start();
    return futurePrice;
}
```

<br>

### 팩토리 메서드 supplyAsync 적용하여 CompletableFuture 만들기

아래 팩토리 메서드는 지금까지 만든 getPriceAsync 함수와 동일한 기능을 합니다.(에러 관리도 동일)

```java
public Future<Double> getPrice(String product) {
    return CompletableFuture.supplyAsync(() -> calculatePrice(product));
}
```

CompletableFuture는 Supplier인수를 실행하여 비동기적으로 결과를 생성합니다. ForkJoinPool의 Excecutor중 하나가 Supplier를 실행합니다.

두번째 인수에는 직접 실행하는 Executor를 전달할수도 있습니다.

<br><br>

## ✅ 16.3 Non-Block 코드 만들기

아래와 같은 상점이 존재할때 List<String> 타입을 반환하는 findPrices(String product) 메서드를 만들어야 합니다. Shop의 getPrice메서드는 내부적으로 1초의 딜레이 타임이 있습니다.

```java
List<Shop> shops = Arrays.asList(
        new Shop("BestPrice"),
        new Shop("LetsSaveBig"),
        new Shop("MyFavoriteShop"),
        new Shop("BuyItAll"),
        new Shop("ShopEasy"));
```

<br>

### 순차 실행

```java
public List<String> findPricesSequential(String product) {
    return shops.stream()
            .map(shop -> shop.getName() + " price is " + shop.getPrice(product))
            .collect(Collectors.toList());
}
// Shop의 개수만큼 시간이 소요됨 (4초)
```

<br>

### 병렬 스트림

```java
public List<String> findPricesParallel(String product) {
    return shops.parallelStream()
            .map(shop -> shop.getName() + " price is " + shop.getPrice(product))
            .collect(Collectors.toList());
}
// 4개의 작업이 병렬로 실행되므로 1초가 소요됨
```

<br>

### CompletableFuture로 비동기 호출

```java
public List<String> findPricesFuture(String product) {
    List<CompletableFuture<String>> priceFutures =
            shops.stream()
                    .map(shop -> CompletableFuture.supplyAsync(() -> shop.getName() + " price is "
                            + shop.getPrice(product), executor))
                    .collect(Collectors.toList());

    return priceFutures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList());
}
// 4개의 작업이 비동기로 호출되므로 1초의 작업이 소요됨
```

스트림은 지연연산을 하므로 비동기 작업과 결과 도출(join)을 하나의 파이프라인으로 작업한다면 동기적, 순차적으로 이뤄지는 결과와 동일해집니다.

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/c8a4ca84-5ff6-4b14-91ea-04e5016e4d65)

<br>

### 커스텀 Executor 사용하기

기본적으로 제공되는 스레드의 수에 비해 Shop의 개수가 늘어날수록 연산 속도는 더뎌 집니다.

따라서 커스텀하게 스레드 수를 지정한 Executor를 사용할 수 있습니다.

- 스레드 풀 크기 조절하기
    
    자바 병렬 프로그래밍 책에선 스레드 풀의 최적값을 찾는 방법을 제안합니다.
    
    $Nthreads = Ncpu * UCpu * (1 + W/C)$
    
    - **Ncpu**: `Runtime.getRuntime().avilableProcessors()`가 반환하는 코어의 수
    - **Ucpu**: 0과 1 사이의 값을 갖는 CPU 활용 비율
    - **W/C**:  대기시간과 계산시간의 비율

Executor는 다음과 같이 생성할 수 있습니다.

```java
private final Executor executor = Executors.newFixedThreadPool(shops.size(), (Runnable r) -> {
    Thread t = new Thread(r);
    t.setDaemon(true);
    return t;
});
```

스레드 풀은 데몬 스레드로 구성되며 main() 메서드가 종료되면 강제로 실행이 종료될 수 있습니다.

이제 다음과 같이 CompletableFuture를 만들 수 있습니다.

```java
CompletableFuture.supplyAsync(
				() -> shop.getName() + " price is " + shop.getPrice(product), executor);
```

<br>

### 스트림 병렬화와 CompletableFuture 병렬화

병렬화 기법은 Stream API를 이용한 방식과 CompletableFuture를 이용하는 방식 2개가 존재합니다.

병렬화 기법의 선택은 다음을 참고할 수 있습니다.

- I/O가 포함되지 않은 게산 중심의 동작을 실행할 때는 스트림 인터페이스가 가장 구현하기 간단하고 효율적입니다.
- I/O를 기다리는 작업을 병렬로 실행해야 할 때는 CompletableFuture가 더 많은 유연성을 제공합니다.
(스트림의 지연 연산의 특성으로 I/O를 실제로 언제 처리할지 예측하기 어려움)

<br><br>

## ✅ 16.4 비동기 작업 파이프라인 만들기

Shop → Quote → Discount → 결과를 생성합니다.

Shop에서 getPrice는 1초의 지연이, Discount에서 실제 할인을 계산하는 부분도 1초의 지연이 발생합니다.

각각 사용되는 메서드는 getPrice(), parse, applyDiscount 메서드 입니다.

<br>

### 할인 서비스 사용 - 순차적 findPrices 구현

```java
public List<String> findPricesSequential(String product) {
    return shops.stream()
            .map(shop -> shop.getPrice(product)) // 1초의 지연
            .map(Quote::parse)
            .map(Discount::applyDiscount) // 1초의 지연
            .collect(Collectors.toList());
}
```

shop의 개수 * 2 만큼의 시간이 지연됩니다.

<br>

### 동기 작업과 비동기 작업 조합하기

```java
public List<String> findPricesFuture(String product) {
    List<CompletableFuture<String>> priceFutures = shops.stream()
            .map(shop -> CompletableFuture.supplyAsync(() -> shop.getPrice(product), executor))
            .map(future -> future.thenApply(Quote::parse))
            .map(future -> future.thenCompose(
                    quote -> CompletableFuture.supplyAsync(() -> Discount.applyDiscount(quote), executor)))
            .collect(Collectors.<CompletableFuture<String>>toList());

    return priceFutures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList());
}
```

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/2abbea9a-e960-4ddf-8eeb-7366547ec191)


위 코드는 위의 작업처럼 동작합니다.

- **Quote 파싱하기**
    
    결과 문자열을 파싱을 하여 이름, 가격, 할인코드를 가진 객체인 Quote로 파생합니다. 이때 CompletableFuture의 thenApply 메서드를 이용하여 파싱을하고 파싱한 객체를 CompletableFuture 타입으로 감싸서 반환합니다.
    
    thenApply는 위의 supplyAsync 작업이 완전히 완료된 후 thenApply를 실행하므로 블록되지 않습니다.
    
- **CompletableFuture를 조합해서 할인된 가격 계산하기**
    
    applyDiscount또한 delay가 있으므로 비동기 작업이 필요합니다.
    
    thenCompose 메서드는 thenApply와 비슷하지만, Function<T, R>에서 R타입이 `CompletionStage` 의 하위 객체여야 합니다. 따라서 applyDiscount에 비동기 작업을 적용할 수 있고 이를 통해 두 개의 비동기 연산에 대한 파이프라이닝이 생성됩니다.
    
    (마치 Stream API의 flatMap과 같음)
    
<br>

### Async 버전의 메서드

thenCompose, thenApply 메서드는 모두 `thenComposeAsync`, `thenApplyAsync` 라는 비동기 메서드를 제공합니다.

Async로 끝나는 메서드는 해당 작업이 다른 스레드에서 실행되도록 스레드 풀로 작업을 제출합니다.

<br>

### 독립 CompletableFuture와 비독립 CompletableFuture 합치기

`CompletableFuture.thenCombine` 메서드를 활용해 두 개의 CompletableFuture의 결과를 합칠 수 있습니다.

thenCombine은 CompletableFuture와 BiFunction 인수를 받는데, 호출자 CompletableFuture(A)와 인수로 전달한 CompletableFuture(B)를 이용해 BiFunction에서 결과를 생성합니다. BiFunction의 인수에 순서대로 (A, B)가 됩니다.

```java
CompletableFuture<Double> futurePriceInUSD =
            CompletableFuture.supplyAsync(() -> shop.getPrice(product))
                    .thenCombine(CompletableFuture.supplyAsync(
                                    () -> ExchangeService.getRate(Money.EUR, Money.USD)),
                            (price, rate) -> price * rate
                    );
```

위 코드는 아래와 같이 동작합니다.

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/425883ea-6d23-4c2f-aeff-07b93e03e97a)


<br>

### (Java 9) 타임아웃 효과적으로 적용하기

```java
CompletableFuture<Double> futurePriceInUSD =
            CompletableFuture.supplyAsync(() -> shop.getPrice(product))
                    .thenCombine(CompletableFuture.supplyAsync(
                                    () -> ExchangeService.getRate(Money.EUR, Money.USD)),
                            (price, rate) -> price * rate
                    )
                    .orTimeout(3, TimeUnit.SECONDS);
```

3초내에 작업이 완료되지 않는다면 TimeoutException이 발생합니다.

내부의 getRate작업시 특정 환율을 찾기 못하는 상황이라면 최대 시간을 설정하고, 시간 이후에는 지정한 기본환율로 변환하는 기능도 추가할 수 있습니다.

```java
CompletableFuture<Double> futurePriceInUSD =
            CompletableFuture.supplyAsync(() -> shop.getPrice(product))
                    .thenCombine(CompletableFuture.supplyAsync(
                                            () -> ExchangeService.getRate(Money.EUR, Money.USD))
                                    .completeOnTimeout(ExchangeService.DEFAULT_RATE, 1, TimeUnit.SECONDS),
                            (price, rate) -> price * rate
                    )
                    .orTimeout(3, TimeUnit.SECONDS);
```

위와 같이 completeOnTimeout을 설정하면 1초동안 환율 계산이 완료되지 않는다면 기본 환율로 진행되도록 합니다.

<br><br>

## ✅ 16.5 CompletableFuture의 종료에 대응하는 방법

지금까지 get, join을 통해 블록되며 결과를 기다렸지만, 고객에게 각 상점에서 가격 정보를 제공할때마다 즉시 보여줄 수 있는 최저가격 검색 애플리케이션을 만들 수 있습니다.

<br>

### 최저가격 검색 애플리케이션 리팩토링

모든 가격 정보를 포함할때까지 Collectors.toList()로 리스트를 생성하고 있는데 이를 사용하지 않고 Stream의 요소에 직접 접근하여 제어해야 합니다.

```java
public Stream<CompletableFuture<String>> findPricesStream(String product) {
    return shops.stream()
            .map(shop -> CompletableFuture.supplyAsync(() -> shop.getPrice(product), executor))
            .map(future -> future.thenApply(Quote::parse))
            .map(future -> future.thenCompose(
                    quote -> CompletableFuture.supplyAsync(() -> Discount.applyDiscount(quote), executor)));
}
```

이후 thenAccept라는 메서드를 이용하는데 해당 메서드는 CompletableFuture의 결과를 소비하는 Consumer를 인수로 받습니다. 따라서 해당 작업을 통해 결과가 생성되면 즉시 출력하도록 다음과 같이 코드를 작성할 수 있습니다.

```java
public void printPricesStream(String product) {
    long start = System.currentTimeMillis();

    CompletableFuture[] futures = findPricesStream(product)
            .map(f -> f.thenAccept(s -> System.out.println(
                    s + " (done in " + (System.currentTimeMillis() - start) + " msecs)")))
            .toArray(CompletableFuture[]::new);

    CompletableFuture.allOf(futures).join();
    System.out.println("All shops have now responded in " + (System.currentTimeMillis() - start) + " msecs");
}
```

allOf는 모든 CompletableFuture가 완료될때 새로운 CompletableFuture를 리턴합니다. 만약 예외가 하나라도 발생하면 해당 에외를 원인으로 하는 CompletionException과 함께 변환된 CompletableFuture를 반환합니다.

CompletableFuture의 결과가 없다면 null값이 반환됩니다.

해당 메서드를 호출하면 결과가 완료될때까지 블록되며 이후 마지막 print문이 출력됩니다.