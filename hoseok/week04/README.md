# 📌 7장: 병렬 데이터 처리와 성능

Java 7에서 도입된 Fork/Join 프레임워크는 쉬운 병렬화를 제공하며 동시에 에러를 최소화 할 수 있습니다.

스트림은 순차 스트림을 병렬 스트림으로 자연스럽게 변경할 수 있는 기능을 제공하고 있는데 내부적으로 포크/조인 프레임워크가 해당 작업을 도와줍니다.

<br><br>

## ✅ 7.1 병렬 스트림

병렬 스트림은 여러 스레드에서 처리할 수 있도록 스트림 요소를 여러 chunk로 분할한 스트림입니다. 따라서 병렬 스트림을 이용하면 모든 멀티코어 프로세서가 각각의 청크를 처리하도록 할당할 수 있습니다.

`BaseStream` 인터페이스는 순차 스트림을 병렬 스트림으로 변환시켜주는 `parallel()` 메서드를 제공합니다

```java
public interface BaseStream<T, S extends BaseStream<T, S>>
        extends AutoCloseable {
		/**
		 * 병렬인 스트림을 반환합니다. 
		 * 스트림이 이미 병렬이거나 기본 스트림 상태가 병렬로 수정되었기 때문에 자체적으로 반환될 수 있습니다.
		 * 이것은 중간 연산입니다.
		 * @return a parallel stream
		 */
		S parallel();
}
```
<br>

### **순차 스트림 → 병렬 스트림 or 병렬 스트림 → 순차 스트림**

```java
public long parallelSum() {

    return Stream.iterate(1L, i -> i + 1)
						.limit(N)
						.parallel() // 단순 호출로 순차 스트림 -> 병렬스트림 변환 가능
						.reduce(0L, Long::sum);
}
```

병렬 스트림을 순차 스트림으로 변경하기 위해서는 sequential() 메서드를 호출하면 됩니다.

다만 연산마다 병렬, 순차를 지정할 수 없고 가장 마지막에 호출된 parallel 혹은 sequential 메서드를 통해 스트림이 병렬로 실행할지 순차로 실행될지 결정 됩니다.

병렬연산은 다음과 같이 동작합니다.

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/a74d05ec-3b92-447a-8bb8-6c00ad115f0b)

<br>

### 병렬 스트림에서 사용하는 스레드 풀 설정

병렬 스트림은 내부적으로 ForkJoinPool을 사용합니다. 해당 풀은 JVM에서 사용하는 프로세서의 수와 동일한 스레드를 갖게 됩니다.

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/1f207ba2-57ef-4fd0-bf00-20fdd9116071)


```java
int processorCounts = Runtime.getRuntime().availableProcessors();
System.out.println(processorCounts); // 8 출력

ForkJoinPool forkJoinPool = new ForkJoinPool();
long[] numbers = LongStream.rangeClosed(1, 1_000_000_000L).toArray();
forkJoinPool.invoke(new ForkJoinSumCalculator(numbers));

// java.util.concurrent.ForkJoinPool@eed1f14[Running, parallelism = 8, size = 8, active = 1, running = 0, steals = 34, tasks = 0, submissions = 0]
System.out.println(forkJoinPool);
// 8
System.out.println(forkJoinPool.getPoolSize());
```

아래와 같은 System 프로퍼티 설정을 통해 포크/조인 풀의 스레드 수를 조정할 수 있다고 하지만, 직접 테스트 해본 결과 예상처럼 동작하지 않습니다.

`System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "12");`

<br>

### 스트림 성능 측정 (with JMH)

JMH 라이브러리를 활용해 스트림의 성능을 벤치마킹할 수 있습니다.

해당 라이브러리를 활용하면 JVM이 바이트코드를 최적화 하기 위한 준비 시간, GC 오버헤드와 같은 여러 요소들을 고려하여 벤치마킹을 할 수 있습니다.

```java
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 2, jvmArgs = {"-Xms4G", "-Xmx4G"})
@Measurement(iterations = 2)
@Warmup(iterations = 3)
public class ParallelStreamBenchmark {

    private static final long N = 10_000_000L;

    @Benchmark
    public long iterativeSum() {
        long result = 0;
        for (long i = 1L; i <= N; i++) {
            result += i;
        }
        return result;
    }

    @Benchmark
    public long sequentialSum() {
        return Stream.iterate(1L, i -> i + 1).limit(N).reduce(0L, Long::sum);
    }

    @Benchmark
    public long parallelSum() {
        return Stream.iterate(1L, i -> i + 1).limit(N).parallel().reduce(0L, Long::sum);
    }

    @Benchmark
    public long rangedSum() {
        return LongStream.rangeClosed(1, N).reduce(0L, Long::sum);
    }

    @Benchmark
    public long parallelRangedSum() {
        return LongStream.rangeClosed(1, N).parallel().reduce(0L, Long::sum);
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        System.gc();
    }

}
```

```java
// 결과
Benchmark                                  Mode  Cnt   Score    Error  Units
ParallelStreamBenchmark.iterativeSum       avgt    4   3.284 ±  0.805  ms/op
ParallelStreamBenchmark.sequentialSum      avgt    4  58.633 ±  1.749  ms/op
ParallelStreamBenchmark.parallelSum        avgt    4  50.288 ± 18.215  ms/op
ParallelStreamBenchmark.rangedSum          avgt    4   5.695 ±  4.631  ms/op
ParallelStreamBenchmark.parallelRangedSum  avgt    4   0.642 ±  0.042  ms/op
```

<br>

### 성능 분석

같은 병렬 스트림이지만 `parallelSum`과 `parallelRangedSum`은 몇십배 이상 차이납니다.

우선 `parallelSum`은 다음과 같은 비용이 발생합니다.

- 반복 결과로 박싱된 객체가 만들어지는데 연산을 하기 위해서는 박싱을 해야 합니다. → `박싱 비용`
- 반복 작업은 병렬로 수행할 수 있는 독립적인 chunk로 나누기 쉽지 않습니다. → 시작 시점에 전체 숫자 리스트가 준비되기 어려움

반면에 `parallelRangedSum`은 성능이 압도적으로 좋은데 성능을 위해 다음과 같은 부분을 고려했습니다.

- 기본형 스트림을 사용하여 박싱에 대한 오버헤드를 줄임
- rangeClosed()를 활용해 처음부터 시작과 끝이 정해져 있으므로 쉽게 청크로 분할할 수 있음

결국 병렬 스트림을 제대로 사용하기 위해서는 올바른 자료구조 뿐만 아니라, 실행하려는 연산이 병렬 수행에 적합한지도 고려해야 합니다. → 확신이 없다면 성능 측정을 해보는것도 방법

<br>

### 병렬 스트림에서 발생하는 Race Condition

병렬 스트림은 내부적으로는 여러 스레드들이 작업을 나누어 실행하게 됩니다.

JVM에서 생성되는 스레드들은 힙 영역과 메소드 영역을 공유하게 됩니다.

따라서 병렬 스트림에서 인스턴스 변수에 접근하게 된다면 모든 스레드가 하나의 인스턴스를 가리키게 됩니다. 이때 해당 인스턴스에 대해 연산을 수행한다면 동시성 문제가 발생할 수 있습니다.

동시성 문제를 해결하게 되면 병렬의 장점도 사라지게 됩니다.

따라서 병렬 스트림을 사용하려면 **공유된 가변 상태를 피해야 합니다.**

<br>

### 병렬 스트림 효과적으로 사용하기

병렬 스트림을 사용하는 기준은 수치화하기 어렵습니다. 하려는 작업마다 비용이 많이 소모되는 구간이 다르기 때문입니다. 하지만 몇가지 정성적인 힌트가 있습니다.

1. 확신이 없다면 직접 측정
2. Boxing을 주의
3. 순차 스트림보다 병렬 스트림에서 성능이 떨어지는 연산을 주의 해야 합니다.(findFirst, limit) → 순서에 의존하므로 병렬 상황에선 순서를 계산하기 어려움
4. 스트림에서 수행하는 전체 파이프라인 연산 비용을 고려
    
    N개의 요소수가 있고 하나의 요소를 처리하는 비용이 Q일때 전체 비용은 NQ → Q값이 높아진다면 병렬 스트림으로 개선할 여지가 있음
    
5. 소량의 데이터에서는 병렬 스트림이 큰 도움이 되지 않는다.
6. 스트림을 구성하는 자료구조가 올바른지 판단
ArrayList, range로 만든 기본형 스트림과 같은 경우 쉽게 분할, 분해가 가능하지만 LinkedList의 경우 모든 요소를 파악하고 분할해야 하므로 상대적으로 성능이 좋지 않음
7. 스트림의 특성 및 중간연산이 스트림의 특성을 어떻게 변경시키는지에 따라 성능이 달라집니다. (예측할 수 없다면 성능이 좋지 못함)
    
    초기 크기가 정해진 스트림과 그렇지 않은 스트림의 경우 병렬 연산에서 매우 큰 속도차이가 존재합니다.
    filtering과 같은 경우도 필터를 만족하는 요소가 몇개인지 예측할 수 없습니다.
    
8. 최종 연산의 병합 과정 비용을 살펴봐야 함(ex: Collector의`combiner`) 병합 과정의 비용이 크다면 병렬 스트림으로 얻는 이익이 병합하는 비용에 상쇄될 수 있음

- 스트림 소스와 병렬화 친밀도
    
    
    | 소스 | 분해성 |
    | --- | --- |
    | ArrayList | 훌륭함 |
    | LinkeList | 나쁨 |
    | IntStream.range | 훌륭함 |
    | Stream.iterate | 나쁨 |
    | HashSet | 좋음 |
    | TreeSet | 좋음 |

<br><br>

## ✅ 포크/조인 프레임워크

`ForkJoin 프레임워크`은 인터페이스의 구현체로 병렬화 할 수 있는 작업을 재귀를 통해 작은 작업으로 분할하고 각 서브태스크의 결과를 합쳐 전체 결과를 도출합니다.

이 프레임워크는 하위 작업을 스레드 풀의 worker thread에 전달 기능을 제공하는 `ExecutorService` 인터페이스를 구현하고 있습니다. → `ForkJoinPool`이라고 함

- 스레드 풀을 이용하려면 RecursiveTask<R>의 하위 클래스를 만들어야 합니다.
- R은 병렬화된 작업이 반환하는 결과 형식입니다. → 결과가 없을때는 RecursiveAction형식 혹은 non-local구조로 변경하여 반환합니다.
- RecursiveTask를 상속받으면 `compute` 추상 메서드를 구현해야 합니다.
- compute 메서드에는 서브태스크로 분할하는 로직과 분할 크기가 적당히 작아졌을때 하나의 서브태스크의 결과를 생산할 알고리즘을 모두 정의해야 합니다.

```java
public class ForkJoinSumCalculator extends RecursiveTask<Long> {

    public static final long THRESHOLD = 10_000;

    private final long[] numbers;
    private final int start;
    private final int end;

    public ForkJoinSumCalculator(long[] numbers) {
        this(numbers, 0, numbers.length);
    }

    private ForkJoinSumCalculator(long[] numbers, int start, int end) {
        this.numbers = numbers;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        int length = end - start;
				// 결과를 도출할 길이가 10_000보다 작다면 연산을 실행함
        if (length <= THRESHOLD) {
            return computeSequentially();
        }

				// leftTask는 새로운 스레드에 작업을 할당 시킴 -> 추가 분할 할 수 있음
        ForkJoinSumCalculator leftTask = new ForkJoinSumCalculator(numbers, start, start + length / 2);
        leftTask.fork();

				// rightTask는 현재 스레드가 작업을 함 -> 추가 분할 할 수 있음
        ForkJoinSumCalculator rightTask = new ForkJoinSumCalculator(numbers, start + length / 2, end);
	
				// 결과 계산
        Long rightResult = rightTask.compute();
				// leftTask의 스레드가 종료되길 기다림
        Long leftResult = leftTask.join();
        return leftResult + rightResult;
    }

****    private long computeSequentially() {
        long sum = 0;
        for (int i = start; i < end; i++) {
            sum += numbers[i];
        }
        return sum;
    }

		// ForkJoinSumCalculator을 실행시킴
    public static long forkJoinSum(long n) {
        long[] numbers = LongStream.rangeClosed(1, n).toArray();
        ForkJoinTask<Long> task = new ForkJoinSumCalculator(numbers);
        return FORK_JOIN_POOL.invoke(task); // ForkJoinPool의 인스턴스를 싱글톤으로 유지

				// FORK_JOIN_POOL은 인수가 없는 생성자로 생성한 ForkJoinPool의 인스턴스
				//  -> 인수가 없으므로 모든 프로세서가 자유롭게 풀에 접근할 수 있음
    }

}
```

위와 같이 1 ~ N까지의 덧셈 연산을 병렬로 실행하기 위해 `RecursiveTask<Long>`를 구현합니다.

RecursiveTask는 다음과 같이 동작합니다.

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/3b445396-86b1-45fe-aae1-2621405ba794)

**동작 분석**

40_000이라는 값이 n으로 들어왔을때 딱 위의 그림처럼 분할하여 실행하게 됩니다.

이론적으로는 `FORK_JOIN_POOL.invoke(task);` 을 실행할때 main스레드에서 새로운 워커 스레드1이 실행됩니다. 

leftTask는 항상 새로운 스레드에 할당되므로 이를 고려하면 총 스레드는 4개를 사용하여 병렬 작업이 될 것으로 예상됩니다.

하지만 실제 결과를 출력하면 스레드는 최대3개 운이 좋다면 2개 정도에서 해당 태스크를 모두 처리합니다.

```java
ForkJoinPool-1-worker-1 start:0 end:40000
ForkJoinPool-1-worker-1 start:20000 end:40000
ForkJoinPool-1-worker-1 start:30000 end:40000
ForkJoinPool-1-worker-1 start:20000 end:30000
ForkJoinPool-1-worker-2 start:0 end:20000
ForkJoinPool-1-worker-3 start:0 end:10000
ForkJoinPool-1-worker-2 start:10000 end:20000
Result: 800020000
```

<br>

### Best practices for using the fork/join framework

- join 메서드 호출시 해당 작업에서 생성된 결과가 준비될 때까지 호출한 스레드는 block됩니다. 따라서 하위 작업의 계산이 모두 시작된 후 호출해야 합니다.
- RecursiveTask 구현체 내에서는 ForkJoinPool의 invoke를 실행하지 않습니다.(대신 compute, invoke 메서드를 사용합니다.
- 한쪽 작업에는 fork를 한쪽 작업에는 compute를 호출하는 것이 효율적입니다. → 재사용성 증가
- 포크 조인 프레임워크를 이용하는 병렬 계산은 디버깅하기 어렵습니다. → 서로 다른 스레드가 할당되므로 stack trace 추적 어려움
- 순차처리보다 무조건 빠르진 않다.
    - 성능을 개선하려면 태스크를 독립적인 서브 태스크로 분할 할 수 있어야 함
    - 서브태스크의 실행시간이 새로운 태스크 포킹 시간보다 길어야 함

<br>

### Work stealing

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/69fa1829-eccc-4ba5-a6d7-2047955d2ac8)

코어 개수에 맞춰서 forking하기 보단 적절한 크기로 분할된 많은 태스크를 포킹하는 것이 바람직합니다.

`forking: 새로운 스레드에 분할한 작업을 배치함`

여러 이유(디스크 접근 속도 저하, 외부 서비스 통신 등)로 각 코어가 실행하는 태스크가 동시에 끝나지 않습니다.

**결국 하나의 스레드가 다른 스레드보다 먼저 작업이 끝나게 됩니다.**

포크 조인 프레임워크는 이중 연결 리스트를 활용해 head 부분에서 작업을 가져와 처리합니다. 만약 두 개의 스레드 중에서 먼저 끝난 스레드가 있다면 끝나지 않은 스레드의 tail 작업을 stealing하여 자신이 처리합니다.

이를 통해 전체적인 스레드간 작업부하를 비슷한 수준으로 유지합니다.

지금까지 `스트림 → 배열` 변환을 하고나서 태스크를 분할했는데

`Spliterator`를 활용하면 스트림을 자동으로 분할하여 병렬 실행할 수 있습니다.

<br><br>

## ✅ 7.3 Spliterator 인터페이스

Parallel Stream은 작업 분할을 위해 병렬 작업에 특화되어 있는 인터페이스 Spliterator를 사용합니다.

```java

package java.util;

public interface Spliterator<T> {

		// Spliterator의 현재 요소를 소비하고, 순회할 다음 요소가 남아있다면 true를 아니라면 false를 반환합니다.
    boolean tryAdvance(Consumer<? super T> action);
   
		// Spliterator의 일부 요소를 분할해 두 번째 Spliterator로 분할하여 두 요소를 병렬로 처리하는데 사용합니다.
		// 특정 기준을 만족해 더 이상 분할하지 않고, null을 반환해 분할을 중단하도록 합니다.
    Spliterator<T> trySplit();

		// 순회해야 할 나머지 요소의 추정치를 제공합니다. 
    long estimateSize();

		// Spliterator 자체의 특성 집합을 포함하는 int 타입을 반환합니다. -> 주어진 정보를 이용해 동작을 최적화 할 수 있습니다.
    int characteristics();

}
```

| Spliterator 특성 | 설명 |
| --- | --- |
| ORDERED | 요소에 정해진 순서가 있음을 명시합니다. Spliterator는 요소 탐색, 분할시 순서를 유의하여 동작합니다. |
| DISTINCT | x, y 두 요소가 있을때 x.equals(y)는 항상 false를 반환합니다. |
| SORTED | 요소가 정렬되어 있음을 나타냅니다. SORTED를 반환하는 Spliterator는 ORDERED도 반환해야 합니다. |
| SIZED | 순회 혹은 분할 전에 estimateSize()에서 반환된 값이 순회에서 만나게 될 요소 수에 대한 정확하고 유한한 카운트 값을 반환하는 특성 값 입니다. |
| NONNULL | 소스가 발생한 요소가 null이 아님을 보장하는 특성 값 입니다. |
| IMMUTABLE | 요소를 구조적으로 수정할 수 없음을 나타냅니다. (교체, 제거 불가능) |
| CONCURRENT | 외부 동기화 없이 여러 스레드에서 요소를 안전하게 동시에 수정할 수 있음을 나타내는 특성 값 입니다.

- (최상위 Spliterator는 순회중에서 소스가 동시에 수정되면 유한 크기가 변경되므로 CONCURRENT와 SIZED를 모두 반환해서는 안됩니다. → 보장할 수 없음)

- (최상위 Spliterator는 상호 배타적이므로 CONCURRENT와 IMMUTABLE을 모두 반환해서는 안됩니다. → 일관성이 없음) |
| SUBSIZED | trySplit()의 결과인 모든 Spliterator가 SIZED 및 SUBSIZED입을 나타내는 특성값 입니다.

- (모든 자식 Spliterator는 SIZED가 됩니다. 또한 SIZED를 같이 반환하지 않는다면 일관성이 없으므로 계산의 결과를 보장할 수 없습니다.) |

<br><br><br>

# 📌 8장: 컬렉션 API 개선


## ✅ 8.1 컬렉션 팩토리

> 컬렉션 팩토리 메서드와 스트림 API모두 특정 Collection을 만드는 방법을 제공합니다.

책에서는 데이터 처리 형식을 설정하거나 데이터를 변환하지 않는다면 팩토리 메서드를 그렇지 않다면 스트림 API를 통해 컬렉션을 생성하기를 권장합니다.
> 

<br>

### List Factory

```java
// Unmodifiable List를 반환합니다.
// null이 입력되면 NPE 발생
List<String> friends5 = List.of("Raphael", "Olivia", "Thibaut");
```

List.of는 가변인수를 사용하지 않고, 최대 10개의 인자를 갖는 메서드들이 오버로딩 되어 있습니다.

가변인수는 결국 배열을 통해 전달되므로 배열에 대한 할당 및 초기화가 발생되고, 결국 GC에서의 비용이 발생하게 됩니다.

<br>

### Set Factory

List와 동일함

```java
// Unmodifiable Set을 반환
// null이 입력되면 NPE 발생
// 동일한 요소가 들어오면 IllegalArgumentException 발생
Set<String> friends = Set.of("Raphael", "Olivia", "Thibaut");
```

<br>

### Map Factory

- Map.of 활용
    
    ```java
    // key, value 순서대로 최대 10쌍을 입력할 수 있습니다.
    // null 입력시 예외 발생
    // 동일한 키 2개 입력시 예외 발생
    Map<String, Integer> ageOfFriends = Map.of("Raphael", 30, "Olivia", 25, "Thibaut", 26);
    ```
    
- Map.ofEntries 활용 (키, 값 쌍이 10개를 초과하면 사용하길 권장함)
    
    ```java
    Map<String, Integer> ageOfFriends2 = Map.ofEntries(
            entry("Raphael", 30),
            entry("Olivia", 25),
            entry("Thibaut", 26));
    ```
    
<br><br>

## ✅ 8.2 List, Set 처리

Java 8에서는 List와 Set을 처리할 수 있는 여러 메서드들을 제공합니다.

<br>

### removeIf

List, Set에 존재하는 특정 요소를 지우고 싶을때 사용되는 메서드 입니다.

iterator를 통해 직접 조건을 탐색하고, 만족하는 조건을 삭제하거나 남길 수 있지만 반복과 삭제에 대한 조건을 명시하는 부분이 모두 코드로 보여지게 되며 코드가 복잡해지고, 실수를 할 수 있습니다.

따라서 단순히 Predicate 조건을 넘겨주면 삭제를 시켜주는 removeIf 메서드를 사용하는것이 안전하고 간결합니다.

```java
List<String> friends = new ArrayList(List.of("Raphael", "Olivia", "Thibaut"));
friends.removeIf(friend -> friend.startsWith("R"));
System.out.println(friends);

// [Olivia, Thibaut]
```

<br>

### replaceAll

List, Set 내의 모든 요소를 전달받은 UnaryOperator를 통해 새로운 요소로 변경합니다.

```java
List<String> friends = new ArrayList(List.of("Raphael", "Olivia", "Thibaut"));
friends.replaceAll(String::toUpperCase);
System.out.println(friends);

// [RAPHAEL, OLIVIA, THIBAUT]
```

<br><br>

## ✅ 8.3 맵 처리

### forEach

Map의 키와 값을 이용해 반복 확인하는 작업을 간단하게 할 수 있도록 제공해줍니다.

```java
Map<String, Integer> ageOfFriends = Map.of("Raphael", 30, "Olivia", 25, "Thibaut", 26);
ageOfFriends.forEach((k, v) -> System.out.println("key: " + k + " value: " + v))

// key: Olivia value: 25
// key: Thibaut value: 26
// key: Raphael value: 30
```

<br>

### 정렬(키, 값 기준)

Entry 클래스는 Map의 키 또는 값을 기준으로 정렬할 수 있는 메서드를 제공합니다.

```java
Map<String, String> alphas = Map.of("A", "C", "B", "B", "C", "A");

alphas.entrySet()
        .stream()
        .sorted(Entry.comparingByKey())
        .forEach(System.out::println);

System.out.println();

alphas.entrySet()
        .stream()
        .sorted(Entry.comparingByValue())
        .forEach(System.out::println);

// A=C
// B=B
// C=A
// 
// C=A
// B=B
// A=C
```

<br>

### getOrDefault

Map의 get 메서드는 키에 일치하는 값이 없다면 null값을 반환합니다.

이때 null값이 아니라 기본값을 반환하도록 도와주는 메서드를 제공합니다.

(이미 value가 null로 저장되어 있는 경우 기본값이 아니라 null이 반환됨)

```java
Map<String, String> alphas = new HashMap<>(Map.of("A", "C", "B", "B", "C", "A"));

alphas.put("F", null);
String result1 = alphas.getOrDefault("D", "NOTHING");
String result2 = alphas.getOrDefault("F", "NOTHING");
System.out.println(result1);
System.out.println(result2);

// NOTHING
// null
```

<br>

### 계산 패턴 (`computeIfAbsent`, `computeIfPresent`, `compute`)

맵에 키가 존재하는지 여부에 따라 특정 동작을 실행하고 결과를 저장하도록 할 수 있는 연산을 제공합니다.

- `computeIfAbsent`: 제공된 키에 해당하는 값이 없다면(값이 없거나, null) 키를 이용해 새 값을 계산하고 맵에 추가
- `computeIfPresent`: 제공된 키가 존재하면 새 값을 계산하고 맵에 추가함
- `compute`: 제공된 키로 새 값을 계산하고 맵에 저장함

```java
String s = alphas.computeIfAbsent("G", name -> "NEW");
System.out.println(s);

// NEW -> 새로 등록한 값이 반환됨
```

```java
freindsToMovies.computeIfAbsent("Raphael", name -> new ArrayList<>())
				.add("star wars");
// 따라서 새로 등록한 ArrayList에 바로 요소를 추가할 수 있음
```

computeIfAbsent는 지정된 키가 아직 값에 연결되지 않았거나 null로 매핑된 경우 mappingFunction를 사용하여 해당 값을 계산합니다.

mappingFunction이 null을 반환하면 해당 매핑을 제거합니다.

<br>

### 교체 패턴

- replaceAll
    
    모든 요소를 일치하는 함수에 맞춰서 변경합니다.
    
    ```java
    Map<String, String> alphas = new HashMap<>(Map.of("A", "C", "B", "B", "C", "A"));
    // {A=c, B=b, C=a}
    ```
    
- replace
    
    특정 키가 존재하면 맵의 값을 바꿉니다.
    
<br>

### merge

두 개의 맵을 합칠때는 보통 하나의 맵에 다른 맵을 putAll()을 통해 합치게 됩니다.

하지만 중복된 키가 존재하는 경우 putAll에 넘겨진 Map 타입 인자가 우선순위를 갖게 됩니다. 즉 둘 중 하나의 값을 무시됩니다. 이를 좀 더 유연하게 대처하기 위해서 merge를 제공합니다.

```java
Map<String, String> alphas = new HashMap<>(Map.of("A", "C", "B", "B", "C", "A"));
Map<String, String> alphas1 = new HashMap<>(Map.of("A", "E", "B", "E", "C", "E"));
alphas.forEach((k, v) -> alphas1.merge(k, v, (v1, v2) -> v1 + "&" + v2));
System.out.println(alphas1);

// {A=E&C, B=E&B, C=E&A}
```

```java
// 아래 예제는 잘못된 예시가 아닌가? 
Map<String, Long> moviesToCount = new HashMap<>();
String movieName = "JamesBond";
long count = moviesToCount.get(movieName);
if(count == null) {
   moviesToCount.put(movieName, 1);
}
else {
   moviesToCount.put(moviename, count + 1);
}

// 둘다 value값인데 key, count가 아니라..?
moviesToCount.merge(movieName, 1L, (key, count) -> count + 1L);
```

<br><br>

## ✅ 8.4 ConcurrentHashMap

ConcurrentHashMap은 동시성 문제를 해결한 Map 자료형입니다.

(내부 자료구조의 특정 부분만 잠궈 동시 추가 및 갱신 작업 허용)

<br>

### 리듀스와 검색

3가지 종류의 연산을 지원합니다.

- forEach: (키, 값) 쌍에 주어진 액션 실행
- reduce: 모든 (키, 값) 쌍을 제공된 리듀스 함수를 이용해 결과로 합침
- search: 널이 아닌 값을 반환할때까지 각 (키, 값) 쌍에 함수 적용

각 종류의 연산은 네 가지 형식을 지원합니다

- `keys`
    - forEachKey, reduceKeys, searchKeys
- `values`
    - forEachValue, reduceValues, searchValues
- `Map.Entry`
    - forEachEntry, reduceEntries, searchEntries
- `(key, value) arguments`
    - forEach, reduce, search

위 연산은 모두 lock을 걸지 않고 진행하는 연산으로 계산이 진행되는 동안 바뀔 수 있는 객체, 값, 순서 (인스턴스 레벨의 변수들)등에 의존하면 안됩니다 (stateless)

각 연산은 병렬성을 위한 threshold를 지정해야 합니다.

- 1이라면 공통 스레드 풀을 이용해 병렬성을 활용합니다.
- Long.MAX_VALUE라면 한 개의 스레드로 연산을 합니다. (권장)

<br>

### counting

size()메서드는 int타입을 반환합니다. mappingCount를 long타입을 반환하므로 만약 int타입을 벗어날 만큼 매핑되어 있다면 mappingCount를 사용합니다.

<br>

### Set views

ConcurrentHashMap은 keySet()메서드를 제공합니다.

일반적인 Map의 keySet과 달리 실제 Map과 Set이 같은 인스턴스를 바라보므로 둘 중 하나라도 수정되면 두 곳 모두에서 수정된 결과를 확인할 수 있습니다.

이를 방지하려면 newKeySet을 이용해 새로운 인스턴스 집합을 만들 수 있습니다.8
