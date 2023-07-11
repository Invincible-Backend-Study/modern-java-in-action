# 📌 1장: 자바 8, 9, 10, 11 : 무슨 일이 일어나고 있는가

- Java 1.0 → 스레드와 락, 메모리 모델 지원 → 저수준 기능을 온전히 활용하기 어려움
- Java 5 → 스레드 풀, 병렬 실행 컬렉션 등 강력한 도구 도입
- Java 7 → 병렬 실행에 도움을 주는 포크/조인 프로엠워크 제공 → 여전히 활용 어려움
- `Java 8 → 병렬 실행을 단순한 방식으로 접근할 수 있는 방법을 제공 → 몇가지 규칙을 지켜야 합니다.`
    - Stream API
    - 메서드에 코드를 전달하는 기법
    - Interface의 default method
    
<br><br>


## ✅ 1.1 역사의 흐름은 무엇인가? (feat. Java 8)

<br>

### 개요

Java 8은 데이터베이스 질의 언어에서 표현식을 처리하는 것처럼 병렬 연산을 지원하는 스트림이라는 새로운 API를 제공합니다.

DB 질의 언어에서 고수준 언어로 원하는 동작을 표현하면 스트림 라이브러리와 같은 구현에서 최적의 저수준 실행 방법을 선택하는 방식으로 동작함

`자동적으로 저수준 실행을 구성해주므로 synchronized와 같은 비용이 큰 키워드를 사용하지 않아도 됩니다.`

<br>

### 함수형 프로그래밍

Java 8은 메서드 참조, 람다와 인터페이스의 디폴트 메서드를 이용해 메서드에 코드를 전달할 수 있습니다.(더하여 결과를 반환하고 다른 자료구조로도 전달함) 이는 `함수형 프로그래밍에서 큰 위력을 발휘합니다.`

<br><br>

## ✅ 1.2 왜 아직도 자바는 변화하는가?

프로그래밍 언어는 생태계와 닮음 → 수 천개의 언어가 쏟아져 나옴
완벽한 언어는 없다, 언어는 꾸준히 발전해야함 → Java는 1995년 이후 자바 생태계를 성공적으로 구축해왔다.

- 자바는 객체 지향 언어
- 자바는 처음부터 스레드, 락을 통한 동시성도 지원함 → 멀티코어 프로세서에서 문제가 발생할 수 있음
- JVM위에서 바이트코드로 동작하기에 인터넷 애플릿 프로그램의 주요 언어가 됨
- JVM은 꾸준한 업데이트를 해줌
    - JDK7에는 invokedynamic 바이트코드가 추가됨
        - 런타임에 동적으로 호출할 메서드를 결정하기 위해 사용 내부적으로 Bootstrap Method의 호출 결과로 Call Site라는 객체를 반환합니다. Call Site는 Method Handle을 담아두는 홀더 역할을 하며 invokedynamic 명령이 호출되는 지점을 의미합니다. JVM에 의해 관리됩니다.
            
            ![스크린샷 2023-07-11 오전 2.08.29.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/40c48e4e-1fb6-44f8-85b0-f6b16e8db2f1/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2023-07-11_%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB_2.08.29.png)
            
- 자바가 대중적인 프로그래밍 언어가 될 수 있었던 이유
    - 객체지향 언어로 모든 것은 객체다 라는 개념을 통해 어떤 모델이든 쉽게 대응할 수 있었음
    - `Write-Once Run-Anywhere` → JVM의 역할이 큼, 운영체제에 독립적인 언어

<br>

### Java 8 설계의 밑바탕을 이루는 3가지 프로그래밍 개념

1. `스트림 처리`
    - 스트림이란 한 번에 한 개씩 만들어지는 연속적인 데이터 항목들의 모임입니다.
    - 입력스트림과 출력스트림은 한 개 씩 읽으며 출력 스트림은 다른 프로그램의 입력 스트림이 될 수 있습니다.
    - Unix의 파이프(|) 명령어와 비슷한 특성을 가짐
    - [java.util.stream](http://java.util.stream) 패키지에 추가된 Stream<T>는 T형식으로 구성된 일련의 항목을 의미합니다.
    - Stream<T>또한 유닉스 명령어의 파이프라인처럼 파이프라인을 만드는데 필요한 메서드들을 제공합니다.
    - 따라서 기존에는 한 번에 한 항목을 처리했지만, 고수준의 추상화를 통해 일련의 스트림으로 처리할 수 있습니다.
    - 추가로 여러 CPU 코어에 쉽게 할당하여 공짜 병렬성을 얻을 수 있습니다.
2. `behavior parameterized로 메서드에 코드 전달하기`
    - 코드 일부를 API로 전달하는 기능
    - Java 8은 메서드를 다른 메서드의 인수로 넘겨주는 기능을 제공합니다.
    - 이러한 기능을 behavior parameterized라고 부릅니다.
3. `병렬성과 공유 가변 데이터`
    - 스트림 메서드로 전달하는 코드는 다른 코드와 동시에 실행하더라도 안전하게 실행될 수 있어야 함
    - 그러기 위해서는 공유된 가변 데이터에 접근할 수 없음 → 스트림 내부에서 사용되는 변수는 final로 취급되는것과 일맥상통?
    - 이러한 함수를 stateless function이라고 말하고, 이런 사항들은 함수형 프로그래밍 패러다임의 핵심 사항입니다.

우리는 위와 같은 추가로 인해 특정 문제를 더 효율적으로 해결할 수 있는 다양한 도구를 얻게 되었습니다.

**언어는 하드웨어나 프로그래머의 기대의 변화에 부응하는 방향으로 변화해야 한다.**

<br><br>

## ✅ 1.3 자바 함수

Java 8은 함수를 새로운 값의 형식으로 추가합니다.
프로그래밍 언어의 핵심은 값을 바꾸는 것입니다. 프로그래밍 언어에서는 이런 값들을 `일급 시민`이라고 말합니다.

하지만 기존 자바에서는 메서드, 클래스를 자유롭게 전달할 수 없었습니다.

자바에서 값은 일급 시민이지만, 메서드, 클래스 등은 이급 시민에 해당됩니다.

런타임에 메서드를 전달할 수 있다면 유용하게 활용할 수 있습니다. 따라서 Java 8은 이급 시민을 일급 시민으로 바꿀 수 있는 기능을 추가했습니다.

<br>

### 메서드 참조

```java
// 각 행에서 하는 작업의 의미가 모호함
File[] hiddendFiles = new File(".").listFiles(new FileFilter() {
		public boolean accept(File file) {
				return file.isHidden();
		}
}

// isHidden이라는 함수를 직접적으로 참조할 수 있습니다.
// 이런 메서드 참조를 통해 메서드를 이급 시민에서 일급 시민으로 사용할 수 있습니다.
File[] hiddenFiles = new File(".").listFiles(File::isHidden);
```

위와 같이 메서드 참조를 통해 문제 자체를 좀 더 직접적으로 설명한다는 것이 Java 8의 장점입니다.

또한 메서드는 더이상 이급 시민이 아닌 일급 시민이 됩니다.

<br>

### 람다 : 익명 함수

- Java 8에서는 메서드 뿐만 아니라 람다를 포함하여 함수도 값으로 취급할 수 있습니다.
- `(int x) -> x + 1`
- 이를 통해 직접 클래스나 메서드가 없을 때 람다 문법을 통해 간결하게 코드를 구현할 수 있습니다.

<br>

### 메서드 참조 및 람다 예제 코드

```java
// given
List<Apple> inventory = Arrays.asList(
        new Apple(80, "green"),
        new Apple(155, "green"),
        new Apple(120, "red")
);

// Java 8 문법을 사용하지 않은 필터링
List<Apple> filteredGreenApples = filterGreenApples(inventory);
System.out.println(filteredGreenApples);

// 메서드 참조
List<Apple> greenApples = filterApples(inventory, FilteringApples::isGreenApple);
System.out.println(greenApples);

List<Apple> heavyApples = filterApples(inventory, FilteringApples::isHeavyApple);
System.out.println(heavyApples);

// 람다 전달
List<Apple> greenApples2 = filterApples(inventory, (Apple a) -> "green".equals(a.getColor()));
System.out.println(greenApples2);

List<Apple> heavyApples2 = filterApples(inventory, (Apple a) -> a.getWeight() > 150);
System.out.println(heavyApples2);

// raw
public static List<Apple> filterGreenApples(List<Apple> inventory) {
	  List<Apple> result = new ArrayList<>();
	  for (Apple apple : inventory) {
	    if ("green".equals(apple.getColor())) {
	      result.add(apple);
	    }
	  }
	  return result;
}

// Predicate를 받아서 필터링의 조건을 메서드로 전달합니다.
// raw 코드와 달리 필터링 조건이 메서드 내부에 위치하지 않고 외부에서 메서드를 직접 주입할 수 있습니다.
public static List<Apple> filterApples(List<Apple> inventory, Predicate<Apple> p) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
      if (p.test(apple)) {
        result.add(apple);
      }
    }
    return result;
  }
```

<br><br>

## ✅ 1.4 스트림

for-each 루프를 사용하면 반복하는 행위 자체가 프로그래머가 볼 수 있습니다.

이런 방식의 반복을 `외부 반복`(external iteration) 이라고 합니다.

```java
List<String> filteredNames = new ArrayList<>();
for (String name : names) { // List<String> names
		if (name.equals("A")) {
				filteredNames.add(name);
		}
}
```

Stream API를 사용하면 라이브러리 내부에서 모든 데이터가 처리됩니다. 또한 프로그래머는 이게 반복되는 행위인지 볼 수 없습니다.

이런 방식의 반복을 내부 반복(internal iteration)이라고 합니다.

```java
List<String> filteredNames = names.stream()
				.filter(name -> name.equals("A")
				.collect(Collectors.toList())
```

위의 리스트보다 훨씬 거대한 리스트가 존재할때 Stream<T>는 병렬 작업을 지원해줍니다.

더하여 Stream<T>는 `멀티코어 활용의 어려움` 및 `컬렉션을 처리하면서 발생하는 모호함과 반복적인 코드 문제`를 해결했습니다.

예를 들어

1. CPU를 가진 환경에서 리스트를 필터링할 때 한 CPU는 앞부분을 다른 CPU는 리스트의 뒷 부분을 처리하도록 요청합니다. (포킹 단계)
2. 각각의 CPU는 자신이 맡은 절반의 리스트를 처리합니다.
3. 하나의 CPU가 두 결과를 정리합니다.

와 같은 방식으로 동작들을 병렬화 할 수 있습니다.

단순히 순차적으로 데이터를 처리한다는 관점에서 컬렉션과 스트림 api는 비슷해 보입니다.

다만 컬렉션은 어떻게 데이터를 저장하고 접근할지에 중점을 두지만, 스트림은 데이터에 어떤 계산을 할 것인지 묘사하는 것에 중점을 둡니다.

병렬 처리 방식은 단순히 다음과 같이 사용할 수 있습니다.

```java
List<Apple> heavyApples = inventory.parallelStream()
				.filter((Apple a) -> a.getWeight() > 150)
				.collect(Collectors.toList());
```

- 자바의 병렬성과 공유되지 않은 가변 상태`(이해가 잘 되지 않음)`
    - Java 8은 라이브러리에서 분할을 처리합니다. (큰 스트림 → 작은 스트림으로 분할)
    - filter와 같은 라이브러리 메서드로 전달된 메서드가 상호작용을 하지 않는 다면 가변 공유 객체를 통해 병렬성을 사용할 수 있습니다.
    - 함수형 프로그래밍에서 함수형이란 함수가 일급 시민이라는 의미 외에도 프로그램이 실행되는 동안 컴포넌트 간에 상호작용이 일어나지 않는다란 의미도 있습니다.

<br><br>

## ✅ 1.5 interface의 default method 및 자바 모듈

- Java에 기존에 구현되어 있던 패키지의 인터페이스를 바꿔야 하는 상황이 존재함
- 해당 인터페이스를 구현하는 모든 클래스의 구현을 바꿔야 하는 문제가 발생함 → 노가다
- Java 8, 9에서는 다른 방법으로 문제를 해결함
    - Java 9는 모듈을 정의할 수 있습니다. → 14장 설명
    - Java 8은 인터페이스를 쉽게 바꿀 수 있도록 default method를 지원합니다.

default method는 구현 클래스에서 구현하지 않아도 되는 메서드를 인터페이스에 추가할 수 있습니다.

이때 default method의 메서드 본문은 구현 클래스가 아니라 인터페이스에 포함됩니다.

<br><br>

## ✅ 1.6 함수형 프로그래밍에서 가져온 다른 유용한 아이디어

- 지금까지 설명한 핵심 아이디어
    - 메서드와 람다를 일급 시민으로 사용
    - 가변 공유 상태가 없는 병렬 실행을 이용해 효율적이고 안전한 함수 및 메서드 호출
- 다른 유용한 아이디어
    - Optional → NullPointer를 피함
    - 구조적 패턴 매칭

<br><br><br>

# 📌 2장 동작 파라미터화(behavior parameterized) 코드 전달하기

소비자의 요구사항은 항상 변화합니다. 변화하는 요구사항에 따라 소프트웨어는 시시각각 변합니다.

동작 파라미터화를 사용하면 변화하는 요구사항에 유연하게 대응할 수 있습니다.

- 동작 파라미터화
    - 어떻게 실행할 것인지 결정하지 않은 코드 블록을 의미합니다.
    - 코드블록은 나중에 프로그램에서 호출되며, 코드 블록의 실행은 나중으로 미뤄집니다.
    - 결과적으로 코드블록에 따라 메서드의 동작이 파라미터화 됩니다.

- 예시
    - 리스트의 모든 요소에 대해서 ‘어떤 동작’을 수행
    - 리스트 관련 작업을 끝낸 다음에 ‘어떤 다른 동작’을 수행
    - 에러가 발생하면 ‘정해진 어떤 다른 동작’을 수행
    
    위에서 어떤 동작이 `동작 파라미터화` 라고 생각할 수 있습니다.
    

동작 파라미터화를 추가하면 직접적인 파라미터화에 상관없는 코드가 늘어나게 되는데 이를 최종적으론 람다 표현식을 이용해 간단하게 변경할 수 있습니다.

<br><br>

## ✅ 2.1 변화하는 요구사항에 대응하기

1. **첫 번째 요구사항: `녹색 사과 필터링`**
    
    ```java
    enum Color { RED, GREEN }
    
    public static List<Apple> filterGreenApples(List<Apple> inventory) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (apple.getColor() == Color.GREEN) {
                result.add(apple);
            }
        }
        return result;
    }
    ```
    
    - 한계: 좀 더 다양한 색을 필터링 할 수 없다.

1. **두 번째 요구사항: `다양한 색상을 필터링`**
    
    ```java
    public static List<Apple> filterApplesByColor(List<Apple> inventory, Color color) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (apple.getColor() == color) {
                result.add(apple);
            }
        }
        return result;
    }
    ```
    

1. **세 번째 요구사항: `색상이 아닌 무게를 필터링`**
    
    ```java
    public static List<Apple> filterApplesByWeight(List<Apple> inventory, int weight) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (apple.getWeight() > weight) {
                result.add(apple);
            }
        }
        return result;
    }
    ```
    
    - 추가적인 요구사항: `색상, 무게를 모두 필터링`
        
        ```java
        public static List<Apple> filterApples(List<Apple> inventory, Color color, int weight) {
            List<Apple> result = new ArrayList<>();
            for (Apple apple : inventory) {
                if ((flag && apple.getColor().equals(color))
                        || (!flag && apple.getWeight() > weight)) {
                    result.add(apple);
                }
            }
            return result;
        }
        ```
        

1. **중간점검 : 변화하는 요구사항에 맞춰서 `동작 파라미터화 적용`**
    
    두 번째 및 세 번째 요구사항의 차이점은 `if()` 안의 조건문에서 차이를 보이고 나머지 코드는 동일합니다.
    2개의 요구사항을 합친다면 바로 위의 코드 같이 작성하게 됩니다.
    
    무게, 색상을 제외한 요구사항이 추가된다면, 추가된 요구사항 만큼 if문 내의 조건은 복잡해지고 길어집니다.
    
    위와 같은 상황을 동작 파라미터화를 통해 해결할 수 있습니다.
    
    if문은 결국 참, 거짓이 필요하므로 해당 함수를 구현하는 predicate 인터페이스를 생성합니다.
    
    ```java
    public interface ApplePredicate {
    		boolean test (Apple apple);
    }
    ```
    
    ApplePredicate의 구현체를 작성합니다.
    
    ```java
    static class AppleWeightPredicate implements ApplePredicate {
    
        @Override
        public boolean test(Apple apple) {
            return apple.getWeight() > 150;
        }
    
    }
    
    static class AppleColorPredicate implements ApplePredicate {
    
        @Override
        public boolean test(Apple apple) {
            return apple.getColor() == Color.GREEN;
        }
    
    }
    
    static class AppleRedAndHeavyPredicate implements ApplePredicate {
    
        @Override
        public boolean test(Apple apple) {
            return apple.getColor() == Color.RED && apple.getWeight() > 150;
        }
    
    }
    ```
    
    → 위의 구현 방식은 strategy design pattern을 이용해 구현합니다! 현재는 3개의 전략을 구현하고 있습니다.
    

1. **세 번째 요구사항 리팩토링 : 동작 파라미터화한 `ApplePredicate` 적용**
    
    ```java
    // 메서드 구현
    public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (p.test(apple)) {
                result.add(apple);
            }
        }
        return result;
    }
    
    // 메서드 사용 : 동작 파라미터화 적용
    List<Apple> greenApples = filter(inventory, new AppleColorPredicate());
    ```
    
    중간 점검 및 리팩토링의 결과로 filterApples 메서드의 동작을 파라미터화 할 수 있게 됐습니다.
    
    **하지만 현재 구조에서는 몇가지 아쉬운 부분이 존재합니다.**
    
    1. 각 동작을 전달하기 위한 과정에서 인터페이스 및 여러 구현 클래스들을 작성하고, 직접 인스턴스화 해주어야 합니다.
    2. 1번의 결과로 가장 중요한 `test`메서드를 제외하고, 크게 중요하지 않은 코드들이 많아집니다.

1. **중간점검 과정을 좀 더 간소화 하기 : `익명 클래스` 사용**
    
    익명 클래스는 이름이 없는 클래스로, 클래스 선언 및 인스턴스화를 동시에 할 수 있습니다.
    
    ```java
    // 메서드 구현
    public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (p.test(apple)) {
                result.add(apple);
            }
        }
        return result;
    }
    
    // 메서드 사용 : 익명 클래스 적용
    List<Apple> redApples = filter(inventory, new ApplePredicate() {
        @Override
        public boolean test(Apple a) {
            return a.getColor() == Color.RED;
        }
    });
    ```
    
    익명 클래스 덕분에 ApplePredicate의 구현 클래스를 작성할 필요가 없어졌습니다.
    
    그럼에도 ApplePredicate를 직접 인스턴스화 해야 하는 부분에서 의미없는 코드가 많은데 이런 부분을 람다식을 이용하여 해결할 수 있습니다.
    
2. **6번 과정을 더 간소화 하기 : `람다식` 적용**
    
    ```java
    // 메서드 구현
    public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (p.test(apple)) {
                result.add(apple);
            }
        }
        return result;
    }
    
    // 메서드 사용 : 람다식 적용
    List<Apple> redApples3 = filter(inventory, (Apple apple) -> RED.equals(apple.getColor()));
    ```
    
    익명 클래스의 사용보다 코드가 훨씬 간결해짐을 알 수 있습니다.
    
3. **마지막 요구사항 : `다양한 과일 리스트에 적용`**
    
    ```java
    // 제네릭 메서드
    public static <T> List<T> filter(List<T> list, Predicate<T> p) {
        ArrayList<T> result = new ArrayList<>();
        for (T e : list) {
            if (p.test(e)) {
                result.add(e);
            }
        }
        return result;
    }
    
    // 메서드 사용 : Integer 타입 리스트를 이용한 람다식 사용
    List<Integer> numbers = List.of(1, 2, 3, 4);
    List<Integer> evenNumbers = filter(numbers, (Integer i) -> i % 2 == 0);
    ```
    
    위의 코드는 더이상 Apple이라는 구현체에 국한되지 않고, 다양한 참조 타입 T를 매개변수로 전달할 수 있는 유연함을 제공합니다.
    
    다양한 과일 뿐만 아니라 모든 타입에 적용할 수 있는 filtering 메서드가 됐습니다.
