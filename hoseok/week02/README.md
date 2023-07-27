# 📌 3장: 람다 표현식

- 람다 표현식은 익명 클래스처럼 이름이 없는 함수면서 메서드를 인수로 전달할 수 있습니다. 
(`람다 표현식 ≈ 익명 클래스`)

<br>
<br>

## ✅ 3.1 람다란 무엇인가?

- 익명 함수를 단순화 한 것
- `익명`: 이름이 없는 메서드 이므로 익명임
- `함수`: 특정 클래스에 종속되지 않은 성질(함수 ≈ 정적 메서드) 
하지만 메서드처럼 파라미터 리스트, 바디, 반환 형식을 가지며, 예외를 던질 수 있다.
- `전달`: 메서드 인수, 변수로 저장 가능
- `간결성`: 익명 클래스와 비교했을때 필요없는 코드들을 확 줄임

람다 표현식을 사용하면 메서드의 바디를 직접 전달하는 것 처럼 코드를 전달할 수 있습니다.

```java
(Apple a1, Apple a2) -> a.getWeight().compareTo(a2.getWeight());
	(람다 파라미터)     (화살표)               (람다 바디)
```

→ `화살표 연산자는 람다식이 등장하면서 추가됐습니다. 람다식에서 인수와 본문을 구분시켜주는 역할을 합니다.`

<br>

### 람다식의 규칙

- 중괄호를 사용하지 않는 경우
    
    `Function<Apple, Integer> f = (Apple a1) → a1.getWeight();`  return문을 생략, 1개의 문장까지만 가능
    
- 중괄호를 사용해야 하는 경우
    
    `Function<Apple, Integer> f = (Apple a1) → { return a1.getWeight(); };`  중괄호가 있다면 반드시 return문을 작성해야함, 1개 이상의 문장을 작성할 수 있으므로 각 문장마다 세미콜론 필요
    

결국 중괄호를 달면 1개 이상의 문장을 작성할 수 있지만, 각 문장 구분을 위한 세미콜론은 필수며, 반드시 return을 명시해주어야 합니다.

<br><br>

## ✅ 3.2 어디에, 어떻게 람다를 사용할까?

**`람다는 함수형 인터페이스에서 사용합니다.`**

함수형 인터페이스는 오직 1개의 추상 메서드를 갖는 인터페이스로, 그 외에 default, static 메서드의 개수는 상관하지 않습니다.

구현해야 할 메서드가 1개밖에 없으므로, 람다식은 자동적으로 해당 메서드를 구현함을 알 수 있습니다.

함수형 인터페이스를 명시적으로 표기하기 위해서는 `@FunctionalInterface`라는 애노테이션을 붙여주면 만에하나 2개 이상의 추상 메서드를 작성했을 경우 컴파일 에러로 이를 알려주게 됩니다.

```java
@FunctionalInterface
public interface Predicate<T> {
		boolean test (T t);

		default ~~ // 상관 없음

		static ~~ // 상관 없음
}
```

함수형 인터페이스로 람다 사용의 범위가 줄어들면서 람다의 전체 표현식을 함수형 인터페이스를 구현한 인스턴스라고 생각할 수 있습니다.

<br>

### 함수 디스크립터

- 함수형 인터페이스의 추상 메서드 시그니처는 람다 표현식의 시그니처를 가리킵니다. 
`(**인터페이스 추상 메서드 시그니처** ≈ **람다 표현식 시그니처** ≈ **함수 디스크립터**)`
    
    ```java
    @FunctionalInterface
    public interface Predicate<T> {
    		boolean test(T t);  // 람다식도 T타입을 받아서 boolean 타입을 리턴해야 함
    }
    ```
    
- 따라서 위와 같이 람다 표현식을 설명할 수 있는 `boolean test(T t)`와 같은 메서드를 `함수 디스크립터`라고 부릅니다.
- 모던 자바 인 액션에서는 함수 디스크립터를 조금 더 간단하게 () → Type과 같은 표기법을 이용하는데 위의 메서드는 `(T) → boolean`과 같이 표현할 수 있습니다.

<br><br>

## ✅ 3.3 람다 활용 : 실행 어라운드 패턴

실행 어라운드 패턴은  자원 처리에 사용되는 공통적인 부분을 패턴화 한 것입니다.

1. 자원을 열고
2. 특정 작업을 처리하고
3. 사용한 자원을 닫음

이러한 과정에서 1, 3과 같은 공통적인 부분을 제외하고 2번 작업을 그때그때 변경해야 한다면 외부에서 주입하는 방법을 선택할 수 있습니다. (`템플릿/콜백 메서드 패턴`)

```java
public class ExecuteAround {
		public static void main(String[] args) throws IOException {
		
				// 외부에서 특정 작업을 처리하는 부분에 대한 동작을 람다식을 이용해 주입
		    String oneLine = processFile((BufferedReader b) -> b.readLine());
		    System.out.println(oneLine);
		
		    String twoLines = processFile((BufferedReader b) -> b.readLine() + b.readLine());
		    System.out.println(twoLines);
		}
		
		
		public static String processFile(BufferedReaderProcessor p) throws IOException {
		    try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
		        return p.process(br);
		    }
		}
		
		@FunctionalInterface
		public interface BufferedReaderProcessor {
		
		    String process(BufferedReader b) throws IOException;
		
		}	
}
```

<br><br>

## ✅ 3.4 함수형 인터페이스 사용

Java 8 라이브러리로 java.util.function 패키지에서 여러 Functional Interface를 제공합니다.

- `Predicate<T>`: (T) → boolean
- `Consumer<T>`: (T) → void
- `Function<T, R>`: (T) → R

위의 Type Parameter가 특정 클래스의 인스턴스와 같이 참조형이라면 상관 없지만, 자바의 특성상 기본 자료형을 사용할때도 Wrapper Class를 이용해 참조형으로 전달해야 합니다. 

이때 Auto Boxing 및 Auto Unboxing이 발생하는 비용이 발생하는데 이런 부분에서의 비용을 줄이기 위해 기본 자료형에 특화된 function 패키지의 Functional Interface도 존재합니다.

![](https://velog.velcdn.com/images/wpdlzhf159/post/ea8a8faa-7ad8-4f55-8945-248f88f15def/image.png)



### 예외, 람다, 함수형 인터페이스의 관계

일반적으로 람다 바디에서 Unchecked Exception은 던질 수 있지만

```java
String oneLine = processFile((BufferedReader b) -> {
    throw new RuntimeException(); // 가능
});
```

Checked Exception은 무작정 던질 수 없습니다. Checked Exception을 던지기 위해서는 다음 조건이 필요합니다.

- **interface의 추상 메서드에 던져지는 예외 정의**
    
    ```java
    public interface BufferedReaderProcessor {
        String process(BufferedReader b) throws IOException;
    }
    
    // readLine은 IOException(checked)을 던짐
    String twoLines = processFile((BufferedReader b) -> b.readLine() + b.readLine());
    ```
    

- **람다식에서 직접 try-catch 사용**
    
    ```java
    public interface BufferedReaderProcessor {
        String process(BufferedReader b);
    }
    
    String twoLines = processFile((BufferedReader b) -> {
        try {
            return b.readLine() + b.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    });
    ```
    
<br><br>

## ✅ 3.5 형식 검사, 형식 추론, 제약

람다 표현식만을 보면 어떤 함수형 인터페이스를 구현했는지에 대한 정보가 하나도 없습니다.

그렇다면 컴파일러는 어떤 방식으로 함수형 인터페이스가 사용됐는지 파악할 수 있을까요?

컴파일러는 람다가 사용되는 context를 통해 람다의 형식을 추론합니다.
(context는 람다가 전달될 메서드 파라미터, 람다가 할당되는 변수 등을 말합니다.)

이렇게 추론한 람다의 형식을 `대상 형식`이라고 부릅니다.

다음과 같이 filter 메서드를 사용해보겠습니다.

```java
List<Apple> apples = filter(inventory, (Apple apple) -> apple.getWeight() > 150);
```

컴파일러는 다음과 같은 과정을 통해 람다의 대상 형식을 파악합니다.

1. filter의 메서드 선언을 확인함
    
    ```java
    filter(List<Apple> inventory, Predicate<Apple> p) {...} // Compiler에 의해 타입 추론이 된 상태
    ```
    
2. 두 번째 파라미터 `Predicate<Apple>` 정보를 통해 대상 형식을 파악합니다.
3. Predicate<Apple\> 인터페이스의 추상 메서드 정보를 확인
    
    ```java
    boolean test(Apple apple) // 타입 추론이 된 상태 (T -> Apple)
    ```
    
4. test 메서드의 함수 디스크립터를 추출 → `(Apple) → boolean`
5. 실제 filter 메서드로 전달된 람다식이 이를 만족하는지 함수 디스크립터 확인
 → `(Apple) → boolena`(a.getWeight() > 150)

**이를 통해 대상 형식 파악 완료**

<br>

### 대상 형식의 한계점

대상 형식을 추론하는 과정에서 함수 디스크립터가 사용됩니다.

따라서 같은 함수 디스크립터가 사용된다면 어떤 함수형 인터페이스를 사용해도 상관없음을 알 수 있습니다.

이는 유연함을 주는것처럼 보이지만, 모호함의 문제를 발생시키기도 합니다.

다음과 같이 동일한 함수 디스크립터를 생성할 수 있는 추상 인터페이스를 매개변수로 둔 2개의 오버로딩 메서드가 있습니다.

```java

@FunctionalInterface
public interface Runnable {
    public abstract void run();  // () -> void
}

@FunctionalInterface
interface Action {
		void act();   // () -> void
}

class Test {
		public void execute(Runnable runnable) {
				runnable.run();
		}

		public void execute(Action action) {
				action.act();
		}

		public static void main(String[] args) {
				new Test().execute(() -> {});   // 두 메서드 중에서 누구를 사용해야 할지 모름 -> 모호함
		}
}

```

따라서 다음과 같은 컴파일 에러가 발생합니다.

![](https://velog.velcdn.com/images/wpdlzhf159/post/fabe4d5b-a433-4a30-8467-75469064d6df/image.png)


이는 컴파일러가 제안을 하듯, 특정 타입으로 캐스팅을 해주면 해결할 수 있습니다.

```java
new Test().execute((Runnable) () -> {});
```

→ 하지만, 근본적으로 이미 존재하는 추상 인터페이스가 있고, 사용하려는 의도와 추상 인터페이스의 의도가 일치한다면 새로운 추상인터페이스를 생성하기 보다는 기존에 있는것을 사용하면 이런 모호함을 피할 수 있을것이라 생각됩니다.

<br>

### 특별한 void 호환 규칙

Predicate는 `(T) → boolean`입니다.

하지만, void를 반환하는 함수 디스크립터와 호환됩니다.

```java
Consumer<String> p = s -> list.add(s);
```

이를 특별한 void 호환 규칙이라고 합니다. 하지만 void 호환 람다는 사용하지 않는 편이 좋습니다. → 혼동 가능성 존재

<br>

### 형식 추론

```java
List<Apple> apples = filter(inventory, (Apple apple) -> apple.getWeight() > 150);
```

이 코드를 좀 더 단순화 시킬 수 있습니다.

```java
List<Apple> apples = filter(inventory, apple -> apple.getWeight() > 150);
```

(Apple이라는 타입을 제거했고, 1개의 인수만 존재하므로 `(,)`도  제거할 수 있습니다.

이런 방식이 가능한 이유는 자바에서는 타입 추론을 지원해주기 때문입니다. 
아래와 같은 과정을 통해 함수 디스크립터를 알아낼 수 있음을 확인해습니다.

![](https://velog.velcdn.com/images/wpdlzhf159/post/bc3f2725-be53-4c25-970d-2332e35b524e/image.png)


결과적으로 컴파일러는 람다 표현식의 파라미터 타입을 알아낼 수 있으므로 이를 통해 타입을 추론할 수 있게 되며 람다 파라미터의 타입을 생략할 수 있게 됩니다.

<br>

### 지역 변수 사용 및 제약

람다 표현식에서 파라미터로 넘겨진 변수가 아닌 식 외부에 있는 변수를 `자유 변수`라고 합니다.

람다 표현식에서 자유 변수를 사용하게 되면 해당 변수는 final 변수로 취급됩니다. 따라서 다음과 같이 자유 변수를 사용 하고 자유 변수의 값을 변경하면 컴파일 에러가 발생합니다.

```java
// 자유변수 사용 제약
int portNumber = 8080;
Runnable runnable = () -> System.out.println(portNumber); // 컴파일 에러
portNumber = 3000;
```

![](https://velog.velcdn.com/images/wpdlzhf159/post/3c0f23b0-8b7b-49af-96a7-6a99a3669c43/image.png)


이런 현상이 발생하는 이유는 다음과 같습니다.

- 자유 변수의 참조가 특정 이유로 인해 할당이 해제 됐을때, 람다식에서는 해당 변수를 더이상 접근할 수 없음
- 이런 상황을 방지하기 위해 자유 변수의 복사본을 람다식 내부에서 사용하게 됨 (`람다 캡쳐링`)
- 복사본의 값이 바뀌지 않아야 하므로, 자유 변수는 final 처리

인스턴스 변수(참조 변수)의 경우도 동일합니다.

하지만 인스턴스 변수의 경우 인스턴스 내부의 값은 변경할 수 있습니다.

이는 참조 변수가 인스턴스의 주소 값을 가지고 있고, 람다 캡쳐링시 주소값이 복사됩니다. 따라서 내부의 값은 변경해도 상관 없지만 인스턴스를 재할당(new)하는 행위는 금지됩니다.

```java
ArrayList<Object> objects = new ArrayList<>();
Runnable runnable1 = () -> System.out.println(objects.add(2));
Runnable runnable2 = () -> objects = new ArrayList<>(); // 컴파일 에러
```

![](https://velog.velcdn.com/images/wpdlzhf159/post/9ee48fe6-671a-4320-a1d0-1293c81912e9/image.png)


<br><br>

## ✅ 3.6 메서드 참조

메서드 참조 이용시 메서드 정의를 활용해 람다처럼 전달합니다.

```java
inventory.sort((Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()));

inventory.sort(comparing(Apple::getWeight)); // 메서드 참조
```

- 가장 큰 이점은 가독성을 높일 수 있다는 점입니다.
- 메서드 참조를 활용하는 방법은 `Apple::getWeihgt` 와 같이 구분자를 이용하여 클래스 명과, 메서드 명을 붙여줍니다.
- 메서드 참조는 3가지 유형으로 나뉩니다.

    1. 정적 메서드 참조
    `(String s) → Integer.parseInt(s)` → `Integer::parseInt`
    2. 다양한 형식의 인스턴스 메서드 참조
    `(String s) → s.length()` → `String::length`
        람다 파라미터로 전달된 인스턴스의 메서드를 호출할때 사용
    3. 기존 객체의 인스턴스 메서드 참조(자유 변수)
        
        ```java
        String s = "abc";
        Predicate<String> p = (String str) -> s.contains(str);
        Predicate<String> p = s::contains; // 다음과 같이 메서드 참조
        ```
        
    
    컴파일러는 람다 표현식의 형식을 검사하는 방식과 비슷한 과정으로 메서드 참조가 주어진 함수형 인터페이스와 호환하는지 확인 합니다.
    
    메서드 참조는 실제 콘텍스트의 형식과 일치해야 합니다.

    <br>
    
    ### 생성자 참조
    
    생성자 참조는 메서드 참조시 생성자를 활용하는 방법입니다.
    
    `Supplier<Apple>`를 예로들어서 인수가 없는 생성자를 생성한다면 다음과 같이 람다식을 생성자 참조로 변경할 수 있습니다.
    
    ```java
    Supplier<Apple> s1 = () -> new Apple();
    Supplier<Apple> s2 = Apple::new;
    ```
    
    만약 1개의 인수를 갖는 생성자라면 Function<T, R>의 시그니처와 일치하므로 해당 인터페이스를 사용하여 구현할 수 있습니다.
    
    ```java
    Function<Integer, Apple> s3 = i -> new Apple(i);
    Function<Integer, Apple> s4 = Apple::new;
    ```
    
    결국 생성자 참조시 필요한 매개변수와 일치하는 함수형 인터페이스가 존재한다면(없으면 커스텀) 생성자 참조 기능을 이용할 수 있습니다.(메서드 참조도 마찬가지)

<br><br>

## ✅ 3.7 람다, 메서드 참조 활용해 순차적으로 간소화 시키기

```java
public class SelfSorting {

    public static void main(String[] args) {
        // 생성
        List<Apple> inventory = new ArrayList<>();
        inventory.addAll(Arrays.asList(
                new Apple(80, Color.GREEN),
                new Apple(155, Color.GREEN),
                new Apple(120, Color.RED)
        ));
        
        
        // 1. Comparator 상속 객체 사용
        inventory.sort(new AppleComparator());

        
        // 2. 익명 클래스 사용
        inventory.sort(new Comparator<Apple>() {
            @Override
            public int compare(final Apple o1, final Apple o2) {
                return o1.getWeight().compareTo(o2.getWeight());
            }
        });

        
        // 3. 람다 사용
        inventory.sort((o1, o2) -> o1.getWeight().compareTo(o2.getWeight()));
        
        
        // 4. 메서드 참조 사용
        inventory.sort(Comparator.comparingInt(Apple::getWeight));
    }

    static class AppleComparator implements Comparator<Apple> {

        @Override
        public int compare(final Apple o1, final Apple o2) {
            return o1.getWeight().compareTo(o2.getWeight());
        }
    }

}
```
<br><br>

## ✅ 3.8 람다 표현식을 조합할 수 있는 유용한 메서드

<br>

### Comparator

java.util.Comparator 클래스는 수많은 정적 메서드 및 default 메서드를 제공해줍니다. 이를 이용해 다양한 기능을 조합해 사용할 수 있습니다.

역정렬을 하고 싶다면 다음과 같이 사용할 수 있습니다.

```java
inventory.sort(Comparator.comparingInt(Apple::getWeight));
```

- 의문점
    
    ```java
    // 역정렬 의문
    inventory.sort(Comparator.comparing(Apple::getWeight).reversed()); // 가능
    
    inventory.sort(Comparator.comparing(apple -> apple.getWeight()).reversed()); // 컴파일 에러 comparing 반환값을 Object로 인식
    
    Comparator<Apple> objectComparator = Comparator.comparing(apple -> apple.getWeight());
    inventory.sort(objectComparator.reversed()); // 가능
    ```
    
    왜 람다식은 comparing() 반환값을 Object로 인식할까요? ㅠ
    

또한 여러 조건으로 정렬을 할 수 있습니다.

```java
inventory.sort(Comparator.comparing(Apple::getWeight)
        .reversed());
        .thenComparing(Apple::getCountry));
```

동일한 사과의 무게에 대해서는 나라 이름순으로 정렬

<br>

### Predicate (and, or, not)

- predicate의 결과값을 반대로
    
    ```java
    Predicate<Apple> notRedApple = redApple.negate();
    ```
    
- and
    
    ```java
    Predicate<Apple> redAndHeavyApple = redApple.and(apple -> apple.getWeight() > 150);
    ```
    
- or
    
    ```java
    Predicate<Apple> redAndHeavyApple = redApple.or(apple -> apple.getWeight() > 150);
    ```
    
- and, or
    
    ```java
    Predicate<Apple> redAndHeavyApple = redApple.and(apple -> apple.getWeight() > 150);
            .or(apple -> GREEN.equals(apple.getColor()));
    ```
    

and, or가 연속적으로 사용된다면 좌측 → 우측으로 진행됩니다.

a.or(b).and(c).or(d) → `(((a || b) && c) || d)`

<br>

### Function 조합

Function에는  a다음 b를 실행하라는 의미의 andThen과

b이후 a를 실행하라는 compose가 있습니다.

```java
// 아래와 같은 식이 있을때
Function<Integer, Integer> f = x -> x + 1;
Function<Integer, Integer> g = x -> x * 2;

Function<Integer, Integer> h = f.andThen(g); // g(f(x))
Function<Integer, Integer> i = f.compose(g); // f(g(x))

h.apply(1); // (1 + 1) * 2 = 4
i.apply(1); // (1 * 2) + 1 = 3
```

디폴트 메서드 andThen, compose의 선언

```java
default <V> Function<V, R> compose(Function<? super V, ? extends T> before) {
    Objects.requireNonNull(before);
    return (V v) -> apply(before.apply(v));
}

default <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {
    Objects.requireNonNull(after);
    return (T t) -> after.apply(apply(t));
}
```

<br><br><br>

# 📌 4장: 스트림 소개

<br><br>

## ✅ 4.1 스트림이란 무엇인가?

- 스트림은 Java8에 추가된 API로 스트림을 사용하면 선언형으로 컬렉션 데이터를 처리할 수 있습니다.
- 스트림을 이용하면 멀티스레드 코드를 구현하지 않아도 데이터를 투명하게 병렬로 처리할 수 있습니다.

<br>

### 스트림을 통해 얻을 수 있는 이득

- `선언형으로 코드를 구현할 수 있습니다.`
    
    향상된 for문인 foreach를 이용하더라도, 특정 조건을 필터링하거나, 필요한 데이터를 변환하려면 동작의 수행을 일일이 지정해야하고, 각종 분기가 필요합니다. 스트림을 사용하면 선언형 코드와, 동작 파라미터화를 활용해 요구사항에 대한 분기를 확 줄이고 단순하게 선언을 통해 요구사항에 대응할 수 있습니다.
    
- `**고수준 빌딩 블록 연산**을 연결해 복잡한 데이터 처리 과정을  **파이프라이닝**할 수 있습니다. 이는 특정 **스레딩 모델**에 제한되지 않고 자유롭게 어떤 상황에서든 사용할 수 있습니다.`
    
    ![](https://velog.velcdn.com/images/wpdlzhf159/post/287cb525-70a2-4dd6-a7be-bd7ae21c9612/image.png)

    
    - `**고수준**`: 인간에게 좀 더 친숙하다는 의미, 빌딩 블록 연산이 사용자가 이해하기 편한 고수준(저수준은 기계에 가까움)
    - `**빌딩 블록 연산**`: 하나의 프로그램을 작성할 때 여러 블록으로 나눠 작업해 전체 프로그램을 완성해 가는 방식
    - `**파이프라이닝**`: 한 단계의 출력이 다음 단계의 입력으로 이어지는 연속적인 형태의 구조
    - **`스레딩 모델에 제한되지 않음`**: 싱글 혹은 멀티스레드의 상황이라도 안정적으로 데이터를 처리할 수 있다는 의미로 유추됨
    

참고로 스트림 API는 굉장히 비용이 큰 연산입니다.

결국 스트림 API는 `**선언형**`으로 인해 가독성이 좋고, 여러 연산들을 `**조립할 수 있는**` 유연성이 있으며 `**병렬**`작업을 통해 성능을 향상 시킬 수 있습니다.

<br><br>

## ✅ 4.2 스트림 시작하기

<br>

### 스트림이란

스트림이란 `데이터 처리 연산을 지원하도록 소스에서 추출된 연속된 요소로 정의할 수 있습니다.`

- `연속된 요소`: 특정 요소 형식으로 이루어진 연속된 값 집합의 인터페이스를 제공함
- `소스`: 컬렉션, 배열, I/O 자원 등의 데이터 제공 소스로부터 데이터를 소비합니다. (정렬된 요소라면 정렬된 상태 그대로 스트림이 생성됨)
- `데이터 처리 연산`: 스트림은 함수형 프로그래밍 언어에서 일반적으로 지원하는 연산과 데이터베이스와 비슷한 연산을 지원합니다. (filter, map, reduce, find, match, sort 등) 또한 순차적으로 실행하거나 병렬로 실행할 수 있습니다.

<br>

### 스트림의 두 가지 중요 특징

- `파이프라이닝`: 대부분의 스트림 연산(중간 연산)은 스트림 연산끼리 연결해서 커다란 파이프라인을 구성할 수 있도록 스트림 자신을 반환합니다. → laziness, short-circuiting과 같은 최적화를 얻을 수 있음
- `내부 반복`: 반복자를 이용해 명시적인 반복을 하는 컬렉션과 달리 스트림은 내부 반복을 지원합니다.

<br>

### 예제

```java
List<String> names = menu.stream()
        .filter(dish -> dish.getCalories() > 300)
        .map(Dish::getName)
        .limit(3)
        .collect(toList());
System.out.println(names);
```

![](https://velog.velcdn.com/images/wpdlzhf159/post/427c9cdc-9e2e-4139-8b37-d9be89d313c9/image.png)


- filter, map, limit은 파이프라인 형성을 위해 Stream을 반환합니다
    - filter: 특정 조건을 만족하는 요소만 다음 스트림의 요소로 선택됩니다. (true) → 칼로리 300 초과
    - map: 현재 스트림의 요소를 다른 요소로 변환하거나 정보를 추출합니다. 여기서는 Dish → String(이름)으로 변환합니다.
    - limit: limit로 지정된 숫자만큼만 저장되도록 스트림을 잘라냅니다.
    - collect: 스트림 요소를 다른 형식으로 변환합니다. 여기서는 `List<String>` 타입으로 변환합니다.
- collect는 위에서 계산된 스트림 결과를 List<String\> 타입으로 변환시켜 반환합니다.
- 결국 collect 연산이 실행되기 전까지는 각 메서드들의 호출은 묶여있고, 대기열에 있다고 생각할 수 있습니다.

<br><br>

## ✅ 4.3 스트림과 컬렉션

컬렉션과 스트림 모두 특정 형식으로 구성된 연속된 요소를 저장하는 역할을 합니다. 하지만 큰 차이점이 존재합니다.

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/4fe87e1b-d7f0-4944-ba0c-a6b15c58ff28)


이 둘의 차이점은 데이터를 언제 계산하느냐에서 나타납니다.

- 컬렉션의 모든 요소는 컬렉션에 추가하기 전에 계산되어야 한다.
    - `**컬렉션의 모든 요소는 반드시 메모리에 올라가 있어야 합니다.**`
- 스트림은 이론적으로 요청할 때만 요소를 계산하는 고정된 자료구조 입니다.
    - `**스트림은 최종 연산 요청이 있을때만 필요한 부분을 메모리에 올립니다.` (Lazy Collection)**
- 스트림은 딱 한번만 탐색할 수 있습니다.
    - 스트림의 요소는 특정 작업을 하면서 소비됩니다. 한 번 소비된 요소를 다시 탐색하고 싶다면 초기 데이터 소스에서 새로운 스트림을 만들어야 합니다.
    
    ```java
    List<String> names = Arrays.asList("Java8", "Lambdas", "In", "Action");
    Stream<String> s = names.stream();
    s.forEach(System.out::println);
    // 스트림은 한 번 만 소비할 수 있으므로 아래 행의 주석을 제거하면 IllegalStateException이 발생
    //s.forEach(System.out::println);
    ```
    
- 스트림은 내부 반복, 컬렉션은 외부 반복
    - 컬렉션을 사용하려면 사용자가 직접 요소를 반복해야 합니다. (for문) → 외부 반복
    - 스트림 라이브러리는 내부 반복을 사용합니다.
    
    ```java
    // 컬렉션을 직접 이용하면 외부반복, 조건, 필터링 관련 코드들을 모두 외부에 노출해야 합니다.
    public static List<String> getLowCaloricDishesNamesInJava7(List<Dish> dishes) {
        List<Dish> lowCaloricDishes = new ArrayList<>();
        for (Dish d : dishes) {
            if (d.getCalories() < 400) {
                lowCaloricDishes.add(d);
            }
        }
        List<String> lowCaloricDishesName = new ArrayList<>();
        Collections.sort(lowCaloricDishes, new Comparator<Dish>() {
            @Override
            public int compare(Dish d1, Dish d2) {
                return Integer.compare(d1.getCalories(), d2.getCalories());
            }
        });
        for (Dish d : lowCaloricDishes) {
            lowCaloricDishesName.add(d.getName());
        }
        return lowCaloricDishesName;
    }
    
    // 스트림을 사용하면 내부반복을 합니다. 조건, 필터링 관련은 선언을 통해 커버할 수 있습니다.
    public static List<String> getLowCaloricDishesNamesInJava8(List<Dish> dishes) {
        return dishes.stream()
                .filter(d -> d.getCalories() < 400)
                .sorted(comparing(Dish::getCalories))
                .map(Dish::getName)
                .collect(toList());
    }
    ```
    
    결국 컬렉션은 원하는 요소를 필터링 하기 위해서는, 처음부터 끝 요소에게 전부 질문을 해야 합니다. → 병렬성과 같은 부가적인 기능들을 스스로 관리(구현)해야 합니다.
    
    하지만, 스트림은 단지 어떤 동작을 해줘 라는 명령을 하면 알아서 처리해줍니다. → 이러한 특성 덕분에 내부에서 병렬처리 및 최적화 다양한 동작들을 알아서 실행할 수 있습니다.
    

> 스트림은 내부 반복을 사용하므로 반복 과정을 우리가 신경쓰지 않아도 됩니다.
이러한 이점을 누리려면 filter, map과 같이 반복을 숨겨주는 연산들이 미리 정의되어 있어야 합니다.
스트림 API에서는 반복을 숨겨주는 대부분의 연산을 람다 표현식을 인수로 받습니다.(동작 파라미터화 적용)


<br><br>

## ✅ 4.4 스트림 연산

```java
List<String> names = menu.stream()
        .filter(dish -> dish.getCalories() > 300) // 중간 연산
        .map(Dish::getName) // 중간 연산
        .limit(3) // 중간 연산
        .collect(toList()); // 최종 연산
System.out.println(names);
```

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/4b5566c5-49da-4b86-8b7b-f5f017bfb296)


<br>

### 중간 연산 (서로 연결되어 파이프라인을 형성합니다.)

파이프라인을 구성할 수 있는 스트림의 연산을 나타냅니다. 이들은 모두 Stream 자신을 반환하는 연산입니다.
따라서 여러 중간 연산을 연결해서 질의를 만들 수 있습니다.

위의 코드만 본다면 하나씩 순차적으로 실행될것으로 예상되지만 실제 동작은 다음과 같습니다.

```java
List<String> names = menu.stream()
        .filter(dish -> {
            System.out.println("filtering " + dish.getName());
            return dish.getCalories() > 300;
        })
        .map(dish -> {
            System.out.println("mapping " + dish.getName());
            return dish.getName();
        })
        .limit(3)
        .collect(toList());
System.out.println(names);

// 실행결과
filtering pork
mapping pork
filtering beef
mapping beef
filtering chicken
mapping chicken
[pork, beef, chicken]
```

300 칼로리가 넘는 음식이 몇백개가 있더라도 limit으로 지정한 3개만 선택됩니다. 이는 `short-circuiting`이라는 기법 덕분입니다.

또한 filter, map은 서로 다른 연산이지만 한 과정으로 병합 됐는데 이 기법을 Loop Fusion 이라고 합니다.

<br>

### 최종 연산(파이프라인을 실행한 다음에 닫음)

최종 연산은 스트림 파이프라인에서 결과를 도출합니다. 여기서는 보통 컬렉션이나, Integer, void 등 스트림 이외의 결과가 반환됩니다.

<br>

### 스트림 이용하기

스트림 이용 과정은 크게 다음과 같이 3가지로 나뉩니다.

1. 질의를 수행할 (컬렉션과 같은) 데이터 소스
2. 스트림 파이프라인을 구성할 중간 연산 연결(빌더 패턴과 유사함)
3. 스트림 파이프라인을 실행하고 결과를 만들 최종 연산
