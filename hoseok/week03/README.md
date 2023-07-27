# 📌 5장: 스트림 활용

<br><br>

## ✅ 5.1 필터링

Stream이 지원하는 필터링은 Predicate<T>를 이용하는 방법과 고유 요소만 필터링 하는 방식이 있습니다.

- `Filtering with a Predicate`
    
    filter(Predicate<T>) 메서드를 활용해 직접 요소를 필터링 할 수 있는 조건을 동작 파라미터화 합니다.
    
    ```java
    // Method
    Stream<T> filter(Predicate<? super T> predicate);
    
    // Example
    List<Integer> transactionValues = transactions.stream()
            .filter(transaction -> transaction.getTrader().getCity().equals("Cambridge"))
            .map(Transaction::getValue)
            .collect(Collectors.toList());
    ```
    
- `Filtering unique elements`
    
    유니크한 요소만으로 이루어진 스트림을 반환합니다. 유니크함을 구별하기 위해 equals and hashcode 메서드를 활용합니다.
    
    ```java
    List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8);
    
    numbers.stream()
    				.filter(number -> number % 2 == 0) // 짝수 필터링
    				.distinct() // filtering unique elements
    				.forEach(System.out::println);
    
    ```
    
<br><br>

## ✅ 5.2 스트림 슬라이싱

스트림 슬라이싱은 스트림의 요소를 선택 혹은 스킵할 수 있는 방법을 제공합니다.

Predicate, 초기 몇개의 요소 무시, 특정 크기로 스트림을 줄이는것과 같이 다양한 방법들이 존재합니다.

```java
List<Dish> specialMenu = Arrays.asList(
        new Dish("prawns", false, 300, Dish.Type.FISH),
        new Dish("rice", true, 350, Dish.Type.OTHER),
        new Dish("season fruit", true, 120, Dish.Type.OTHER),
        new Dish("chicken", false, 400, Dish.Type.MEAT),
        new Dish("french fries", true, 530, Dish.Type.OTHER));

List<Dish> menu = Arrays.asList(
        new Dish("pork", false, 800, Dish.Type.MEAT),
        new Dish("beef", false, 700, Dish.Type.MEAT),
        new Dish("chicken", false, 400, Dish.Type.MEAT),
        new Dish("french fries", true, 530, Dish.Type.OTHER),
        new Dish("rice", true, 350, Dish.Type.OTHER),
        new Dish("season fruit", true, 120, Dish.Type.OTHER),
        new Dish("pizza", true, 550, Dish.Type.OTHER),
        new Dish("prawns", false, 400, Dish.Type.FISH),
        new Dish("salmon", false, 450, Dish.Type.FISH)
);
```

- `TakeWhile`
    
    필터링할 조건이 처음 거짓이 되기 전까지의 스트림을 슬라이싱 합니다.
    
    다만 특정 값 이상 혹은 이하의 조건을 제시하려면 슬라이싱 할 요소들이 정렬되어 있어야 모든 값들을 찾을 수 있습니다.
    
    ```java
    List<Dish> slicedMenu1 = specialMenu.stream()
            .takeWhile(dish -> dish.getCalories() < 320)
            .collect(toList());
    slicedMenu1.forEach(System.out::println);
    
    // 결과
    prawns
    ```
    
    칼로리가 320보다 작은 음식은 prawns외에 season fruit이 있지만 리스트가 정렬되어 있지 않으므로 rice에서 false가 발생해 다음 요소를 탐색하지 않습니다.
    
- `DropWhile`
    
    TakeWhile이 특정 조건을 만족하지 않을때까지 앞의 부분을 슬라이싱 한다면 DropWhile은 특정 조건이 만족되면 drop하고 처음 false가 발생하는 부분부터 끝까지 슬라이싱합니다. 
    
    역시 정렬된 원소에서 사용해야 정확한 결과를 얻을 수 있습니다.
    
    ```java
    List<Dish> slicedMenu2 = specialMenu.stream()
            .dropWhile(dish -> dish.getCalories() < 320)
            .collect(toList());
    slicedMenu2.forEach(System.out::println);
    
    // 결과
    rice
    season fruit
    chicken
    french fries
    ```
    
    season fruit은 320 이하이지만, rice가 320을 넘게 되므로 해당 위치부터 끝의 원소까지 슬라이싱 됨
    

- `스트림 축소 - limit`
    
    `limit(long maxSize)` 는 주어진 값 이하의 크기를 갖는 새로운 스트림을 반환합니다.
    
    ```java
    // 300칼로리 이상인 음식을 최대 3개 반환
    List<Dish> dishesLimit3 = menu.stream()
            .filter(d -> d.getCalories() > 300)
            .limit(3)
            .collect(toList());
    dishesLimit3.forEach(System.out::println);
    
    // 결과
    pork
    beef
    chicken
    ```
    
- `요소 건너뛰기 - skip(long n )`
    
    입력한 숫자만큼 앞에서 부터 n개의 요소를 건너뛰고 나머지 스트림을 반환합니다.
    
    ```java
    List<Dish> dishesSkip2 = menu.stream()
            .filter(d -> d.getCalories() > 300)
            .skip(2)
            .collect(toList());
    dishesSkip2.forEach(System.out::println);
    
    // 결과
    chicken
    french fries
    rice
    pizza
    prawns
    salmon
    ```
    
<br><br>

## ✅ 5.3 Mapping

Stream API의 map과 flatMap은 특정 데이터를 선택할 수 있는 기능을 합니다.

map 메서드는 스트림의 각 원소에 적용되며 인수로 넘긴 함수를 적용한 결과가 새로운 스트림의 요소로 매핑됩니다.

```java
List<String> dishNames = menu.stream()
        .map(Dish::getName)
        .collect(toList());
System.out.println(dishNames);

// 결과 - Stream<Dish> -> Stream<String>
[pork, beef, chicken, french fries, rice, season fruit, pizza, prawns, salmon]
```

<br>

### FlatMap

flatMap은 스트림안의 스트림을 하나의 스트림으로 평탄화 시켜주는 역할을 합니다.

```java
List<String> words = Arrays.asList("Hello", "World");

// 위 2개의 단어를 알파벳으로 쪼개고 중복을 제거하여 출력하려면

// before flatMap -> stream[stream["H", "e", "l", "l", "o"], stream["W", "o", "r", "l", "d"]]
Stream<Stream<String>> splitStream = words.stream()
        .map(line -> Arrays.stream(line.split("")))
        .distinct();
splitStream.forEach(stringStream -> stringStream.forEach(System.out::println));

// 결과 - 서로 다른 스트림에 포함되어 있으므로 중복 제거가 되지 않음
H
e
l
l
o
W
o
r
l
d

// after flatMap
Stream<String> splitStream2 = words.stream()
        .flatMap((String line) -> Arrays.stream(line.split("")))
        .distinct();
splitStream2.forEach(System.out::println);

// 결과 - 우리가 원하는 결과 출력
H
e
l
o
W
r
d
```

flatMap은 각 배열을 스트림이 아니라 스트림의 콘텐츠로 매핑하게 됩니다.

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/89912cf9-92bd-4206-bb44-52d5bf113004)


<br><br>

## ✅ 5.4 검색과 매칭

Stream API는 특정 속성이 데이터 집합에 존재하는지 검색하는 데이터 처리에서도 사용됩니다.

allMatch, anyMatch, noneMatch, findFirst, findAny 등 여러 메서드들을 제공해줍니다.

- `하나의 요소와 일치하는지 확인 - anyMatch`
    
    ```java
    // 채식음식이 스트림 원소중에 하나라도 존재하면 true 반환
    boolean result = menu.stream()
    				.anyMatch(Dish::isVegetarian);
    ```
    
- `모든 요소와 일치하는지 확인 - allMatch`
    
    ```java
    // 스트림 원소 전부가 채식음식이라면 true 반환
    boolean result = menu.stream()
    				.allMatch(Dish::isVegetarian);
    ```
    
- `어떤 요소와도 일치하지 않는지 확인 - noneMatch`
    
    ```java
    // 채식음식이 하나도 없어야 true 반환
    boolean result = menu.stream()
    				.noneMatch(Dish::isVegetarian);
    ```
    
- `요소 검색 - findAny`
    
    ```java
    Optional<Dish> dish = menu.stream()
    				.filter(Dish::isVegetarian)
    				.findAny();
    ```
    
    채식음식이 존재하지 않을 수 있으므로 null이 반환될 수 있습니다.
    
    NPE가 발생할 수 있을 여지가 있으므로 Optional로 감싸서 결과를 반환합니다.
    
- `첫 번째 요소 검색 - findFirst`
    
    ```java
    Optional<Dish> dish = menu.stream()
    				.filter(Dish::isVegetarian)
    				.findFirst();
    ```
    
    menu가 정렬되어 있는 리스트라면 처음으로 발견한 채식음식이 있을때 반환하게 됩니다.
    
- findFirst? findAny?
    
    위의 2개의 예제에선 findFirst와 findAny의 결과값이 동일할 것 입니다. 그렇다면 어떤 상황에서 이들을 사용해야 할까요?
    
    - findFirst의 경우 먼저 발견되는 사실 자체가 중요할때 사용하면 좋습니다.
    - findAny의 경우 병렬 실행을 한다면 사용하는것이 좋습니다. 병렬 실행에선 findFirst와 같이 먼저 찾는 요소가 무엇인지 그 기준이 모호하기 때문입니다.

<br><br>

## ✅ 5.5 리듀싱

reduce() 연산은 스트림 요소를 조합해 더 복잡한 질의를 표현하는 방법을 설명합니다.

모든 스트림의 요소를 처리해 최종적으로는 하나의 값으로 도출하게 됩니다.

```java
List<Integer> numbers = List.of(4, 5, 3, 9);

// 일반적인 합계 구하는 연산
int sum = 0;
for (int x : numbers) {
		sum += x;
}

// reduce() 활용
int sum1 = numbers.stream()
				.reduce(0, (a, b) -> a + b); // 초기값, 두 요소를 조합해 적용할 연산 BinaryOperator<T>

Optional<Integer> sum2 = numbers.stream() // 초기값이 없지만, 스트림 요소가 없을 수 있으므로 Optional로 감싸서 반환
				.reduce((a, b) -> a + b);
```

reduce()는 다음 그림과 같이 연산을 수행합니다.

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/3ec26399-f16d-49bd-9f2f-667d82887857)


<br>

### reduce 메서드의 장점과 병렬화

for문과 Stream의 차이와 동일하게 reduce 메서드를 활용하는것과 그렇지 않은 방식은 차이점이 존재합니다.

`reduce를 사용하면 내부 반복이 추상화`

사용자 입장에서는 reduce를 적용한 결과가 중요합니다. 실제 메서드도 초기값, 연산식만 넘겨주면 반복에 관한 부분은 추상화 되어 있습니다.

이러한 장점으로 **내부 구현에 병렬로 실행하도록 적용할 수 있고, 그렇지 않을 수도 있습니다.**

`reduce를 사용하지 않은 일반 반복은?`

일반 반복문에서 병렬로 연산을 실행시키려고 한다면 우선 `sum`이라는 변수의 공유에서 문제가 발생합니다. OS 입장에서 sum이란 변수는 공유 자원이며 스레드가 서로 공유자원을 사용하려고 할 때 발생되는 오버헤드가 병렬화로 얻는 이득을 감소시킬 수 있습니다.

<br>

### 스트림의 연산 : stateless, stateful

스트림을 사용하면 큰 노력없이 병렬성을 얻을 수 있습니다. 하지만 상태가 존재하는 연산의 경우 내부의 상태를 고려하면서 병렬실행 해야 합니다.

`map`, `filter`와 같은 연산은 각 요소를 매핑하거나, 참 거짓만을 판단하므로 내부 상태를 갖지 않는 stateless한 연산입니다.

반면에 `reduce`, `sum`, `max`같은 연산은 값을 누적하거나 기억하고 있어야 하므로 내부 상태가 필요한 stateful한 연산입니다.

`sorted`, `distinct`도 마찬가지로 stateful한데 정렬 혹은 중복을 제거하려면 다른 요소들의 상태도 알아야 합니다. 이때 모든 요소들은 버퍼에 추가되어 있어야 비교가 가능하기 때문입니다.

<br><br>

## ✅ 5.7 숫자형 스트림

기본 자료형은 Generic의 Type Parameter로 사용할 수 없기때문에 Wrapper Class를 제공합니다.

기본형 ←→ 래퍼 클래스 간 변환시 Auto Boxing, Unboxing 기능은 사용자에게 편리함을 주지만, 쓸모없는 비용이 발생합니다.

Stream<T>도 Generic을 사용하고 있으므로 위의 비용이 발생하는데 이런 부분을 줄이고자 3종류의 `기본형 스트림`을 제공해줍니다.

1. `IntStream`
2. `DoubleStream`
3. `LongStream`

리듀싱 연산은 스트림안에 값이 하나도 없을때 null발생을 막기 위해 Optional<T>타입으로 반환합니다.
기본형 스트림도 Optional을 반환하지만 기본형에 더 특화된

OptionalInt, OptionalDouble, OptionalLong과 같은 기본형 옵셔널 타입을 반환합니다.

각각의 인터페이스들은 sum, max와 같이 기본 자료형에 특화된 숫자 관련 리듀싱 연산을 제공합니다.
또한 다시 Type Argument로 변환한 객체 스트림으로 복원할 수 도 있습니다.

<br>

### 숫자 스트림 매핑

스트림을 특화 스트림으로 변환하려면 일반 map() 메서드가 아닌, mapToInt(), mapToDouble(), mapToLong()과 같은 메서드를 사용합니다.

```java
int calories = menu.stream()
        .mapToInt(Dish::getCalories)  // IntStream 객체 반환
        .sum();  // IntStream 인터페이스에 정의된 reduce 메서드

// sum 메서드 선언
@Override
public final int sum() {
    return reduce(0, Integer::sum);
}
```

<br>

### 객체 스트림 복원

기본형 스트림에서 일반 스트림으로 변환하는 메서드로 boxed()를 제공합니다.

```java
IntStream intStream = menu.stream()
                .mapToInt(Dish::getCalories);
Stream<Integer> boxedStream = intStream.boxed(); // IntStream -> Stream<Integer>
```

<br>

### 숫자 범위

특정 범위의 숫자를 지정해서 어떤 연산을 하려면 보통 for문을 사용하는데 정수형 스트림(IntStream, LongStream)은 range(), rangeClosed()라는 정적 메서드를 제공하여 쉽게 특정 범위의 숫자를 반복시킬 수 있습니다.

두 메서드 모두 2개의 인자를 받는데 이를 start, end로 두겠습니다. 두 메서드는 다음과 같이 숫자의 범위를 반복시킵니다.

- `range` : start ≤ number < end
- `rangeClosed` : start ≤ number ≤ end

```java
IntStream.rangeClosed(1, 5)
        .forEach(System.out::println);

System.out.println();

IntStream.range(1, 5)
        .forEach(System.out::println);

// 출력 결과
1
2
3
4
5

1
2
3
4
```

<br><br>

## ✅ 5.8 스트림 만들기

Stream은 스트림을 만들 수 있는 정적 메서드들을 지원합니다.

<br>

### `Stream.of` - T타입으로 이루어진 스트림 반환

```java
public static<T> Stream<T> of(T... values) {
    return Arrays.stream(values);
}
```

<br>

### `Stream.empty()` - 비어있는 스트림 반환

```java
public static<T> Stream<T> empty() {
    return StreamSupport.stream(Spliterators.<T>emptySpliterator(), false);
}
```

<br>

### `Stream.ofNullable` - null이 될 수 있는 객체를 포함하는 스트림 값을 반환 (null이라면 Stream.empty())

```java
public static<T> Stream<T> ofNullable(T t) {
    return t == null ? Stream.empty()
                     : StreamSupport.stream(new Streams.StreamBuilderImpl<>(t), false);
}
```

<br>

### `Arrays.stream()` - 배열 → Stream 변환

```java
public static <T> Stream<T> stream(T[] array) {
    return stream(array, 0, array.length);
}
```

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/e3ebd5d6-e043-4872-a0c0-5b693d9d4d2e)


<br>

### `Java8의 NIO활용한 파일로 스트림 만들기`

```java
// 특정 파일의 모든 라인 읽어서 Stream에 저장
public static Stream<String> lines(Path path, Charset cs) throws IOException {
    // Use the good splitting spliterator if:
    // 1) the path is associated with the default file system;
    // 2) the character set is supported; and
    // 3) the file size is such that all bytes can be indexed by int values
    //    (this limitation is imposed by ByteBuffer)
    if (path.getFileSystem() == FileSystems.getDefault() &&
        FileChannelLinesSpliterator.SUPPORTED_CHARSET_NAMES.contains(cs.name())) {
        FileChannel fc = FileChannel.open(path, StandardOpenOption.READ);

        Stream<String> fcls = createFileChannelLinesStream(fc, cs);
        if (fcls != null) {
            return fcls;
        }
        fc.close();
    }

    return createBufferedReaderLinesStream(Files.newBufferedReader(path, cs));
}

// 사용
long uniqueWords = Files.lines(Paths.get("modernjavainaction/chap05/data.txt"), Charset.defaultCharset())
        .flatMap(line -> Arrays.stream(line.split(" ")))
        .distinct()
        .count();
```

<br>

### Stream.iterate()

iterate와 generate 메서드는 무한 스트림을 만들 수 있습니다. 일반적인 애플리케이션에서는 절대 무한 스트림을 만들면 안됩니다. 따라서 특정 조건을 걸어주어 무한 스트림이 되지 않도록 해야 합니다.

iterate는 메서드 인자로 `T seed, UnaryOperator<T> f`를 받습니다. 각각 초기값과 값에 적용할 연산을 의미 합니다.

```java
IntStream.iterate(1, n -> n + 1)
				.limit(5)
				.forEach(System.out::println);

// 결과
1
2
3
4
5
```

Java 9에서는 혹시나 실수로 무한 스트림을 만들 수 있는 위험성을 고려해서 종료조건을 같이 넘겨줄 수 있는 기존의 메서드를 오버로딩한 iterate()메서드를 제공합니다.

```java
IntStream.iterate(1, n -> n <= 5, n -> n + 1)
        .forEach(System.out::println);

// 결과
1
2
3
4
5
```

다음과 같이 filtering을 할 수 있을것 같지만 언제 작업을 중단해야 할지 기준이 모호하기 때문에 무한 출력이 됩니다.

```java
IntStream.iterate(1, n -> n + 1)
        .filter(n -> n < 5)
        .forEach(System.out::println); // 값이 무한 출력됨 - jdk 17
```

<br>

### Stream.generate()

generate 메서드 또한 무한 스트림을 만듭니다. 하지만 iterate와 같이 규칙적이거나 연속적인 값 뿐만 아니라 불규칙적인 값을 출력할수도 있게 Supplier<T> (`(void) → T`)를 인수로 받습니다.

```java
public class Test {
    public static void main(String[] args) {

        Stream.generate(Test::randomInteger) // 2자리 정수를 반환
                .limit(5)
                .forEach(System.out::println);
    }

    public static int randomInteger() {
        return (int) (Math.random() * 100);
    }
}
```

<br>

### 가변 상태 익명 클래스를 사용하지 말자

함수형 인터페이스는 단순히 하나의 추상 메서드만 가지고 있고 주로 람다를 활용해 메서드를 인자로 전달합니다.

하지만 익명 클래스를 생성하게 된다면 구현체 내부에 상태를 둘 수 있습니다.

```java
// 피보나치 수 f(n) = f(n - 1) + f(n - 2)
IntSupplier fib = new IntSupplier() {

    private int previous = 0;  // 상태를 둘 수 있음
    private int current = 1;

    @Override
    public int getAsInt() {
        int nextValue = previous + current;
        previous = current;
        current = nextValue;
        return previous;
    }
};

IntStream.generate(fib)
        .limit(5)
        .forEach(System.out::println);

// 결과
1
1
2
3
5
```

위의 익명 클래스는 인스턴스 변수가 존재하게 되면서 언제든지 값이 변화할 수 있는 `가변상태`가 됩니다.

만약 스트림을 병렬로 사용하길 원한다면 위의 가변 상태의 익명 클래스는 문제가 될 수 있습니다.

여러개의 스레드를 이용해 lock 없이 5만번의 ++연산을 했을시 값이 50_000으로 딱 맞아 떨아지지 않는 경우를 생각해보면 왜 가변상태가 위험한지 이해할 수 있습니다.

따라서 iterate, generate를 사용할때는 불변 상태를 고수해야 합니다.

<br><br><br>

# 📌 6장: 스트림으로 데이터 수집

```java
// 리팩토링 이전 -> 나름 스트림을 잘 쓰지 않았을까 생각했었음
private List<PostDetailResponse> createPostsResponse(final List<Post> postPages,
                                                     final Map<Post, List<Comment>> groupedComments) {
    return postPages.stream()
            .map(post -> {
                if (groupedComments.containsKey(post)) {
                    return PostDetailResponse.of(post, groupedComments.get(post));
                }
                return PostDetailResponse.of(post, Collections.emptyList());
            }).collect(Collectors.toList());
}

private Map<Post, List<Comment>> separateLimitCommentsByPost(final List<Comment> comments, final int commentLimit) {
    Map<Post, List<Comment>> groupedComments = Maps.newHashMap();
    for (Comment comment : comments) {
        Post post = comment.getPost();
        if (!groupedComments.containsKey(post)) {
            groupedComments.computeIfAbsent(post, k -> Lists.newArrayList());
        }
        if (groupedComments.get(post).size() < commentLimit) {
            groupedComments.get(post).add(comment);
        }
    }
    return groupedComments;
}

// 리팩토링 이후 -> 컬렉터를 활용해 메서드를 리팩토링할 수 있었음 -> 편안..!
private List<PostDetailResponse> createPostsResponse(final List<Post> postPages,
                                                     final Map<Post, List<Comment>> groupedComments) {
    return postPages.stream()
            .map(post -> PostDetailResponse.of(post, groupedComments.getOrDefault(post, Collections.emptyList())))
            .collect(Collectors.toList());
}

private Map<Post, List<Comment>> separateLimitCommentsByPost(final List<Comment> comments, final int commentLimit) {
    return comments.stream()
            .collect(Collectors.groupingBy(Comment::getPost, Collectors.collectingAndThen(Collectors.toList(),
                    eachComments -> eachComments.subList(0, Math.min(eachComments.size(), commentLimit)))));
}
```

<br><br>

## ✅ 6.1 컬렉터란 무엇인가?

- Colllector 인터페이스의 구현을 사용하면 스트림의 요소를 어떤 식으로 도출할지 직접 명시할 수 있습니다.
- 스트림의 collect를 호출하면 스트림의 요소에 리듀싱 연산이 수행됩니다.
    - Collector 인터페이스의 메서드를 구현하는 방식에 따라 스트림에 어떤 리듀싱 연산을 수행할지 결정합니다.
    - `Collectors` 클래스는 Collector 인터페이스를 구현한 여러 정적 팩토리 메서드를 사용할 수 있게 제공해줍니다.

미리 정의된 컬렉터의 기능은 크게 3가지로 구분됩니다.

1. `스트림 요소를 하나의 값으로 reduce, summarize`
2. `요소 grouping`
3. `요소 partitioning`

<br><br>

## ✅ 6.2 리듀싱과 요약

스트림의 요소를 하나의 요소로 만드는 연산을 주로 수행합니다.

총 합, 최댓값, 최솟값, 평균 등 다양한 리듀싱 연산을 제공합니다.

이들은 편의 메서드들을 모두 제공해주지만, reducing 메서드를 통해 직접 구현할 수 도 있습니다.

```java
int totalCalories1 = menu.steram()
				.collect(summingInt(Dish::getCalories));

int totalCalories2 = menu.stream()
				.collect(recuing(0, Dish::getCalories, (i, j) -> i + j));
```

상황에 따라 더 좋은 방식을 선택하면 되지만, 일반적으로 편의 메서드들의 가독성이 더 좋은것 같습니다!

<br>

### collect(), recue()의 차이

collect()의 기능은 앞에서 살펴본 reduce()의 기능과 비슷합니다.

하지만 의미론적인 문제와 실용성 문제 적인 측면에서 둘의 차이점이 존재합니다.

**의미론적 문제**

- collect 메서드는 도출 결과를 누적하는 컨테이너를 바꾸도록 설계된 메서드 (Stream → Collection 객체)이지만
- reduce 메서드는 두 값을 하나로 도출하는 불변형 연산입니다.

**실용적 문제**

- reduce 메서드를 잘못 사용하면 실용성 문제도 발생합니다. 여러 스레드가 동시에 같은 데이터 구조체를 고치면 리스트 자체가 망가지므로 리듀싱 연산을 병렬로 수행할 수 없습니다. → 병렬성을 얻고 싶다면 collect 메서드로 reducing 연산을 구현해야 함

<br><br>

## ✅ 6.3 그룹화

스트림의 여러 요소들을 하나 이상의 특성을 이용해 그룹화 합니다. 주로 Map 자료형으로 결과 컬렉션이 생성됩니다.

가장 많이 사용되는 메서드는 Collectors.groupingBy로 1개의 인자를 넘기는 메서드와 2개의 인자를 넘기는 메서드를 제공해줍니다.

**1개의 인자를 갖는 그룹 메서드는 toList()가 생략되어 있음**

```java
// 메서드 선언
public static <T, K> Collector<T, ?, Map<K, List<T>>> groupingBy(Function<? super T, ? extends K> classifier) {
    return groupingBy(classifier, toList());
}

// 사용
private static Map<Dish.Type, List<Dish>> groupDishesByType() {
    return menu.stream().collect(groupingBy(Dish::getType)); // Dish::getType, Collectors.toList() 가 생략되어 있음
}
```

**2개 인자를 가진 그룹 메서드는 두번째 인자로 컬렉터 구현체를 넘길 수 있습니다.**

```java
menu.stream()
				.collect(groupingBy(Dish::getType, mapping(Dish::getName, toList())));

menu.stream()
				.collect(groupingBy(Dish::getType, flatMapping(dish -> dishTags.get(dish.getName()).stream(), toSet())));

menu.stream()
				.collect(groupingBy(Dish::getType, filtering(dish -> dish.getCalories() > 500, toList())));
```

이를 이용해 Map<Type1, Map<Type2, 3>>와 같이 다수준 그룹화도 가능합니다.

```java
private static Map<Dish.Type, Map<CaloricLevel, List<Dish>>> groupDishedByTypeAndCaloricLevel() {
    return menu.stream().collect(
            groupingBy(Dish::getType,
                    groupingBy((Dish dish) -> {
                        if (dish.getCalories() <= 400) {
                            return CaloricLevel.DIET;
                        } else if (dish.getCalories() <= 700) {
                            return CaloricLevel.NORMAL;
                        } else {
                            return CaloricLevel.FAT;
                        }
                    })
            )
    );
}
```

- `filter()`, `groupingBy(Function<T, R>, filtering())`의 차이점
    
    추가로 groupingBy의 두번째 인자로 필터링을 넘겨주는 방식과, filter()메서드 이후 groupingBy를 호출하는 방식은 차이가 있습니다.
    
    전자의 경우 특정 키 값의 밸류가 있지만 필터조건을 만족하지 못한다면 key값이 Map에 존재하지만 value를 없는 상태가 되며
    
    후자의 경우 키 값 자체가 filter에서 걸리지므로 Map에 키가 존재하지 않게 됩니다.
    

- 알아두면 좋을 추가 연산들
    - `Collectors.collectingAndThen()`

<br><br>

## ✅ 6.4 분할(특수한 종류의 그룹화)

`Collectors.partitioningBy()` 메서드로 활용할 수 있으며 인자는 Predicate를 분류 함수를 받아옵니다.

분할의 결과로는 항상 `Map<Boolean, Collection>`으로  true, false인 2개의 키값과 프리디케이트 조건으로 나뉜 value들로 나뉘게 됩니다.

이러한 특성 덕분에 filter()와 달리 조건과 일치하지 않는 정보도 같이 가져갈 수 있다는 장점이 있습니다.

```java
private static Map<Boolean, List<Dish>> partitionByVegeterian() {
    return menu.stream().collect(partitioningBy(Dish::isVegetarian));
}

// 역시 두번째 인자로 컬렉터를 받아올 수 있습니다. 아래 예제는 두 주순의 맵을 생성하는 메서드입니다.
private static Map<Boolean, Map<Dish.Type, List<Dish>>> vegetarianDishesByType() {
    return menu.stream().collect(partitioningBy(Dish::isVegetarian, groupingBy(Dish::getType)));
}
```

<br><br>

## ✅ 6.5 Collector 인터페이스

Collector 인터페이스는 리듀싱 연산을 어떻게 구현할지 제공하는 메서드들의 집합으로 구성됩니다.

```java
public interface Collector<T, A, R> {
   
    Supplier<A> supplier();

    
    BiConsumer<A, T> accumulator();

   
    BinaryOperator<A> combiner();

   
    Function<A, R> finisher();

   
    Set<Characteristics> characteristics();

  
    public static<T, R> Collector<T, R, R> of(Supplier<R> supplier,
                                              BiConsumer<R, T> accumulator,
                                              BinaryOperator<R> combiner,
                                              Characteristics... characteristics) {
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(accumulator);
        Objects.requireNonNull(combiner);
        Objects.requireNonNull(characteristics);
        Set<Characteristics> cs = (characteristics.length == 0)
                                  ? Collectors.CH_ID
                                  : Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH,
                                                                           characteristics));
        return new Collectors.CollectorImpl<>(supplier, accumulator, combiner, cs);
    }

    
    public static<T, A, R> Collector<T, A, R> of(Supplier<A> supplier,
                                                 BiConsumer<A, T> accumulator,
                                                 BinaryOperator<A> combiner,
                                                 Function<A, R> finisher,
                                                 Characteristics... characteristics) {
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(accumulator);
        Objects.requireNonNull(combiner);
        Objects.requireNonNull(finisher);
        Objects.requireNonNull(characteristics);
        Set<Characteristics> cs = Collectors.CH_NOID;
        if (characteristics.length > 0) {
            cs = EnumSet.noneOf(Characteristics.class);
            Collections.addAll(cs, characteristics);
            cs = Collections.unmodifiableSet(cs);
        }
        return new Collectors.CollectorImpl<>(supplier, accumulator, combiner, finisher, cs);
    }

   
    enum Characteristics {
       
        CONCURRENT,

        UNORDERED,

        IDENTITY_FINISH
    }
}
```

`Collector<T, A, R>` 에서 3개의 Type Parameter의 의미

- `T`: 수집될 스트림 항목의 제네릭 형식
- `A`: 누적자, 수집 과정에서 중간 결과를 누적하는 객체의 형식
- `R`: 수집 연산 결과 객체의 형식

<br>

### 메서드의 의미

- `Supplier<A> supplier()`
    - 새로운 결과 컨테이너 만들기
    supplier 메서드는 빈 결과로 이루어진 `Supplier` 객체를 반환해야 합니다.
- `BiConsumer<A, T> accumulator()`
    - 결과 컨테이너에 요소 추가하기
    - 지금까지 탐색한 항목에 대한 결과가 누적된 A타입에 현재 항목을 추가합니다.
- `BinaryOperator<A> combiner()`
    
    ![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/3a6ccaa7-f577-49b6-8441-8fe1fb4e5568)

    
    - 두 결과 컨테이너 병합
    - 병렬 실행시 사용되는 메서드로 위와 같이 분활된 컨테이너를 병합할때 combine 메서드가 사용됩니다.
- `Function<A, R> finisher()`
    - 최종 변환값을 결과 컨테이너로 적용하기
    - toList()의 경우 누적자 자체가 최종 결과와 동일한 경우도 있습니다. (항등 함수 사용)
- `Set<Characteristics> characteristics()`
    - 컬렉터의 연산을 정의합니다.
    - `Set<Characteristics>` 를 반환타입으로 갖습니다.
    - `Characteristics`는 enum 입니다.
        1. **CONCURRENT**: 병렬 스트림 처리 시에 동시성을 유지하도록 합니다. 다수의 스레드가 동시에 스트림 요소를 처리할 수 있습니다.
        2. **UNORDERED**: 요소들의 처리 순서를 무시합니다. 결과에 대한 요소 순서가 보장되지 않습니다.
        3. **IDENTITY_FINISH**: 최종 결과를 추가적인 변환 없이도 스트림의 요소와 동일한 타입으로 바로 반환합니다.
        
        위의 요소들은 Collector의 동작 방식을 결정하는데에 사용됩니다.
        하지만 위의 옵션들을 적용하거나 하지 않는다고 해서 100% 적용되지 않습니다.
        
        예를 들어 **UNORDERED**를 적용하지 않는다고 해서 병렬 실행시 순서의 보장이 100%라는 의미가 아니라는 뜻입니다.
        
        요소들의 순서를 보장해야 한다면, 병렬 처리를 하지 않는 순차적인 스트림을 고려하거나, 병렬 처리 후에 결과를 정렬하는 등의 방법을 사용해야 합니다.
        
        ```java
        // Characteristics에 따라 메서드의 분기가 나뉨나뉨
        @Override
        @SuppressWarnings("unchecked")
        public final <R, A> R collect(Collector<? super P_OUT, A, R> collector) {
            A container;
            if (isParallel()
                    && (collector.characteristics().contains(Collector.Characteristics.CONCURRENT))
                    && (!isOrdered() || collector.characteristics().contains(Collector.Characteristics.UNORDERED))) {
                container = collector.supplier().get();
                BiConsumer<A, ? super P_OUT> accumulator = collector.accumulator();
                forEach(u -> accumulator.accept(container, u));
            }
            else {
                container = evaluate(ReduceOps.makeRef(collector));
            }
            return collector.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)
                   ? (R) container
                   : collector.finisher().apply(container);
        }
        ```
        
        - 추가 정보
            
            ```java
            // Collectors의 상단에 보면 자주 사용하는 Set<Collector.Characteristics> 을 미리 정의해두고
            /// 각 메서드에서 사용하고 있습니다.
            static final Set<Collector.Characteristics> CH_CONCURRENT_ID
                    = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.CONCURRENT,
                                                             Collector.Characteristics.UNORDERED,
                                                             Collector.Characteristics.IDENTITY_FINISH));
            static final Set<Collector.Characteristics> CH_CONCURRENT_NOID
                    = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.CONCURRENT,
                                                             Collector.Characteristics.UNORDERED));
            static final Set<Collector.Characteristics> CH_ID
                    = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));
            static final Set<Collector.Characteristics> CH_UNORDERED_ID
                    = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.UNORDERED,
                                                             Collector.Characteristics.IDENTITY_FINISH));
            static final Set<Collector.Characteristics> CH_NOID = Collections.emptySet();
            static final Set<Collector.Characteristics> CH_UNORDERED_NOID
                    = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.UNORDERED));
            ```
            
            Collectors.toList()의 경우 **IDENTITY_FINISH**만 사용합니다.
            
            Collectors.groupingBy()의 경우 아무런 옵션도 사용하지 않습니다.
            
            `groupingByConcurrent()`의 경우 UNORDERED, CONCURRENT를 사용합니다.
            
            - `그렇다면 위의 옵션 여부에 따라 성능에 차이가 심할까? 궁금증`
        
        [What does the Java 8 Collector UNORDERED characteristic mean?](https://stackoverflow.com/questions/39942054/what-does-the-java-8-collector-unordered-characteristic-mean)
        

<br>

### 위의 내용을 적용해서 구현한 ToListCollector<T>

```java
public class ToListCollector<T> implements Collector<T, List<T>, List<T>> {

    @Override
    public Supplier<List<T>> supplier() {
        return () -> new ArrayList<>();
    }

    @Override
    public BiConsumer<List<T>, T> accumulator() {
        return (list, item) -> list.add(item);
    }

    @Override
    public Function<List<T>, List<T>> finisher() {
        return i -> i;
    }

    @Override
    public BinaryOperator<List<T>> combiner() {
        return (list1, list2) -> {
            list1.addAll(list2);
            return list1;
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(CONCURRENT, IDENTITY_FINISH));
    }

}
```
