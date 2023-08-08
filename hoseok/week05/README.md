# 📌 9장: 리팩터링, 테스팅, 디버깅

<br>

## ✅ 9.1 가독성과 유연성을 개선하는 리팩터링

람다 표현식은 동작 파라미터화 형식을 지원하므로 큰 유연성을 갖출 수 있습니다.

따라서 람다, 메서드 참조, 스트림등을 이용해 가독성이 좋은 코드로 리팩토링할 수 있습니다.

- 코드 가독성은 약 3가지 방법을 통해 개선할 수 있습니다.
    1. 익명 클래스를 람다 표현식으로 리팩터링 하기
    2. 람다 표현식을 메서드 참조로 리팩터링
    3. 명령형 데이터 처리를 스트림으로 리팩터링

<br>

### 1. 익명 클래스 → 람다 표현식으로 리팩터링

```java
public class Test {

    private final int b = 3;

    public void runnableTest() {
        int a = 1;
				// (1)
        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                int a = 2; // shadow variable -> runnableTest에 선언되어있는 지역변수 a를 가림
                System.out.println(a);
            }
        };
				// (2)
        Runnable r2 = () -> {
            int b = this.b;
            // int a = 2; // 변수 가리기 불가능
            System.out.println(a);
        };
    }
}
```

익명 클래스보다 람다 표현식이 훨씬 깔끔하지만 몇가지 주의사항이 존재합니다.

1. 익명 클래스에서 this는 익명 클래스 자신을 가리키지만, 람다 표현식에서 this는 람다를 선언한 클래스를 가리킴
2. 익명 클래스는 익명 클래스를 선언한 클래스의 지역변수를 가리지만, 람다 표현식에서는 지역변수와 동일한 변수를 선언하면 컴파일 에러가 발생합니다.

결국 람다에서 this는 람다를 선언한 클래스가 되므로 내부의 지역변수또한 선언 클래스에 포함되므로 위와 같은 상황이 발생합니다.

동일한 함수 디스크립터(시그니처)를 갖는 함수형 인터페이스가 존재한다면 위와 같은 람다식을 사용할때 어떤 인터페이스를 사용할지 모르므로 모호함이 발생합니다.(2장에서 설명했었음)

따라서 명시적인 형변환을 통해 해결할 수 있습니다.

<br>

### 2. 람다 표현식 → 메서드 참조로 리팩터링

```java
public void methodReferenceTest() {
    Map<CaloricLevel, List<Dish>> dishesByCaloricLevel1 = menu.stream()
            .collect(
                    Collectors.groupingBy(dish -> {
                        if (dish.getCalories() <= 400) {
                            return CaloricLevel.DIET;
                        } else if (dish.getCalories() <= 700) {
                            return CaloricLevel.NORMAL;
                        } else {
                            return CaloricLevel.FAT;
                        }
                    })
            );

    Map<CaloricLevel, List<Dish>> dishesByCaloricLevel2 = menu.stream()
            .collect(Collectors.groupingBy(Test::getCaloricLevel));
}

private static CaloricLevel getCaloricLevel(final Dish dish) {
    if (dish.getCalories() <= 400) {
        return CaloricLevel.DIET;
    } else if (dish.getCalories() <= 700) {
        return CaloricLevel.NORMAL;
    } else {
        return CaloricLevel.FAT;
    }
}
```

메서드 참조를 이용하면 간결하다는 장점도 있지만, 작성한 코드의 의도를 명확하게 파악할 수 있습니다.

<br>

### 3. 명령형 데이터 처리 → 스트림으로 리팩터링

특정 리스트에서 조건을 걸고, 조건에 부합하는 요소들만 골라야 할때 명령형 데이터 처리를 이용하는것보다 스트림 API를 사용하는 것이 좀 더 명확하고, 부가적인 최적화(쇼트서킷, Lazy Collection) 효과를 얻을 수 있습니다.

```java
public void imperativeDataProcessingToStream() {
    
    // 명령형 데이터 처리
    ArrayList<String> dishNames1 = new ArrayList<>();
    for (Dish dish : menu) {
        if (dish.getCalories() > 300) {
            dishNames1.add(dish.getName());
        }
    }
    
    // 스트림을 통한 데이터 처리
    List<String> dishNames2 = menu.stream()
            .filter(dish -> dish.getCalories() > 300)
            .map(Dish::getName)
            .collect(Collectors.toList());
}
```

<br>

### 동작 파라미터화를 통한 코드 유연성 개선

**조건부 연기 실행**

```java
if (logger.isLoggable(Log.FINER)) {
		logger.finer("Problem" + generateDiagnostic());
}
```

위 코드는 다음과 같은 문제점을 가짐

1. logger의 상태가 isLoggable이라는 메서드에 의해 노출됨
2. 메시지 로깅 때 마다 logger 객체의 상태를 매번 확인함
3. String + String 연산을 무조건 실행함

```java
public void log(Level level, Supplier<String> msgSupplier) {
		if (logger.isLoggable(level)) {
				log(level, msgSupplier.get());
		}
}

logger.log(Level.FINER, () -> "Problem: " + generateDiagnostic()); // 실행부분
```

- 람다식을 사용하면 logger의 상태가 노출되지 않습니다.
- 하지만 메시지 로깅때마다 logger객체의 상태를 매번 확인하지만 직접적으로 확인하는 부분이 노출되진 않습니다.
- 또한 if 문을 만족하던 하지않던 항상 실행했던 String 덧셈 연산이 람다식의 지연 평가(lazy evaluation) 덕분에 표현식의 실행이 해당 값이 실제로 필요한 시점까지 연기됩니다.

<br><br>

## ✅ 9.2 람다로 객체지향 디자인 패턴 리팩터링 하기

디자인 패턴은 소프트웨어 개발시 자주 사용되는 설계들을 패턴화 한 것 입니다.

람다 표현식을 같이 사용하여 패턴을 구현하면 좀 더 쉽고 간결하게 작성할 수 있습니다.

<br>

### Strategy Pattern(전략 패턴)

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/6636a62c-5b11-4a6d-8ade-2d226a036624)

전략 패턴은 실행 중에 특정 알고리즘을 선택할 수 있게 하는 행위 및 디자인 패턴 입니다.

```java
public class StrategyMain {

    public static void main(String[] args) {
        // old school
        Validator v1 = new Validator(new IsNumeric());
        System.out.println(v1.validate("aaaa"));
        Validator v2 = new Validator(new IsAllLowerCase());
        System.out.println(v2.validate("bbbb"));

        // with lambdas
        Validator v3 = new Validator((String s) -> s.matches("\\d+"));
        System.out.println(v3.validate("aaaa"));
        Validator v4 = new Validator((String s) -> s.matches("[a-z]+"));
        System.out.println(v4.validate("bbbb"));
    }

    interface ValidationStrategy {
        boolean execute(String s);
    }

    static private class IsAllLowerCase implements ValidationStrategy {

        @Override
        public boolean execute(String s) {
            return s.matches("[a-z]+");
        }

    }

    static private class IsNumeric implements ValidationStrategy {

        @Override
        public boolean execute(String s) {
            return s.matches("\\d+");
        }

    }

    static private class Validator {

        private final ValidationStrategy strategy;

        public Validator(ValidationStrategy v) {
            strategy = v;
        }

        public boolean validate(String s) {
            return strategy.execute(s);
        }

    }

}
```

- Validator 클래스는 검증을 위한 전략을 생성자를 통해 주입받습니다. 이때 ValidationStrategy는 인터페이스로 해당 인터페이스를 구현한 클래스라면 어떤 것이든 주입받을 수 있습니다.
- 결국 Validator에 어떤 전략이 들어있는지는 런타임 시점까지 미뤄지게 됩니다. 이를 통해 유연하게 검증 전략을 선택할 수 있습니다.
- 만약 ValidationStrategy의 메서드가 한 개 밖에 존재하지 않는다면 FunctionalInterface로 위의 예시와 같이 람다식을 통해 해당 인터페이스의 구현체를 전달할 수 있습니다.

<br>

### Template Method Pattern(템플릿 메서드 패턴)

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/df60f20d-c37f-41bb-b058-e1cc44b5e584)

템플릿 메서드 패턴은 동작 상의 알고리즘의 뼈대를 정의하는 행위 패턴 입니다.

알고리즘의 일부 실행 부분을 구조를 변경하지 않고 외부에서 주입할 수 있습니다.

결국 해당 알고리즘의 실행 부분또한 런타임에 결정됩니다.

```java
abstract class OnlineBanking {

    public static void main(String[] args) {
        OnlineBanking onlineBanking = new MyBanking();
        onlineBanking.processCustomer(3);
    }

    public void processCustomer(int id) {
        Customer c = Database.getCustomerWithId(id);
        makeCustomerHappy(c);
    }
		
		// 하위 클래스에게 해당 알고리즘의 구현을 맡김
    abstract void makeCustomerHappy(Customer c);

}

class MyBanking extends OnlineBanking {
    @Override
    void makeCustomerHappy(final Customer c) {
        System.out.println("I'm happy " + c);
    }
}

// 더미 Database 클래스
class Database {
    static Customer getCustomerWithId(int id) {
        return new Customer(id);
    }
}

// 더미 Customer 클래스
class Customer {
    private int id;

    public Customer(final int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
```

람다를 사용하게 되면 MyBanking이라는 구현 클래스를 굳이 작성하지 않아도 됩니다.

```java
public class OnlineBankingLambda {

    public static void main(String[] args) {
				// 메서드 시그니처와 일치하는 람다식 작성
        new OnlineBankingLambda().processCustomer(1337, (Customer c) -> System.out.println("Hello!"));
    }

    public void processCustomer(int id, Consumer<Customer> makeCustomerHappy) {
        Customer c = Database.getCustomerWithId(id);
        makeCustomerHappy.accept(c);
    }
}

// 더미 Database 클래스
class Database {
    static Customer getCustomerWithId(int id) {
        return new Customer(id);
    }
}

// 더미 Customer 클래스
class Customer {
    private int id;

    public Customer(final int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
```

<br>

### Observer Pattern(옵저버 패턴)

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/cda5deca-db0b-4086-9b97-2f3b482bb87d)

옵저버 패턴은 객체의 상태 변화를 관찰하는 옵저버들의 목록을 객체에 등록하고, 특정 객체의 상태 변화가 있을 때마다 메서드 등을 통해 객체가 직접 목록의 옵저버들에게 통지하는 디자인 패턴입니다.

```java
public class ObserverMain {

    public static void main(String[] args) {

				// 일반적인 옵저버 패턴
        Feed f = new Feed();
        f.registerObserver(new NYTimes());
        f.registerObserver(new Guardian());
        f.registerObserver(new LeMonde());
        f.notifyObservers("The queen said her favourite book is Java 8 & 9 in Action!");

				// 람다로 실행하는 옵저버 패턴
        Feed feedLambda = new Feed();

        feedLambda.registerObserver((String tweet) -> {
            if (tweet != null && tweet.contains("money")) {
                System.out.println("Breaking news in NY! " + tweet);
            }
        });
        feedLambda.registerObserver((String tweet) -> {
            if (tweet != null && tweet.contains("queen")) {
                System.out.println("Yet another news in London... " + tweet);
            }
        });

        feedLambda.notifyObservers("Money money money, give me money!");
    }

    interface Observer {
        void inform(String tweet);
    }

    interface Subject {
        void registerObserver(Observer o);

        void notifyObservers(String tweet);
    }

    static private class NYTimes implements Observer {

        @Override
        public void inform(String tweet) {
            if (tweet != null && tweet.contains("money")) {
                System.out.println("Breaking news in NY!" + tweet);
            }
        }

    }

    static private class Guardian implements Observer {

        @Override
        public void inform(String tweet) {
            if (tweet != null && tweet.contains("queen")) {
                System.out.println("Yet another news in London... " + tweet);
            }
        }

    }

    static private class LeMonde implements Observer {

        @Override
        public void inform(String tweet) {
            if (tweet != null && tweet.contains("wine")) {
                System.out.println("Today cheese, wine and news! " + tweet);
            }
        }

    }

    static private class Feed implements Subject {

        private final List<Observer> observers = new ArrayList<>();

        @Override
        public void registerObserver(Observer o) {
            observers.add(o);
        }

        @Override
        public void notifyObservers(String tweet) {
            observers.forEach(o -> o.inform(tweet));
        }
    }

}
```

- Feed 클래스는 옵저버들을 동록하고, tweet이 들어오면 등록되어 있는 옵저버들에게 트윗을 통지합니다.
- Observer를 구현한 구현체들은 통지받은 tweet에 특정 키워드가 있다면 콘솔에 출력합니다.

위의 두 과정을 위해 인터페이스에 대한 구현 클래스를 직접 작성해야 하지만, 람다식을 활용하면 별도의 구현 클래스 없지 옵저버들을 등록할 수 있습니다.

<br>

### Chain Of Responsibility Pattern(책임 연쇄 패턴)

책임 연쇄 패턴은 명령 객체와 일련의 처리 객체를 포함하는 디자인 패턴 입니다.

각 처리 객체는 명령 객체를 처리할 수 있는 연산의 집합이고, 체인 안의 처리 객체가 핸들할 수 없다면 해당 명령은 다음 처리 객체로 넘겨집니다.

대표적으로 Spring Security의 Filter Chain을 생각할 수 있습니다.

```java
public class ChainOfResponsibilityMain {

    public static void main(String[] args) {
				// 일반적인 책임 연쇄 패턴
        ProcessingObject<String> p1 = new HeaderTextProcessing();
        ProcessingObject<String> p2 = new SpellCheckerProcessing();
        p1.setSuccessor(p2);
        String result1 = p1.handle("Aren't labdas really sexy?!!");
        System.out.println(result1);

				// 람다를 활용한 책임 연쇄 패턴 -> 굳이 관련 클래스를 사용하지 않고, 함수 체인을 이용해 구현할 수 있음
        UnaryOperator<String> headerProcessing = (String text) -> "From Raoul, Mario and Alan: " + text;
        UnaryOperator<String> spellCheckerProcessing = (String text) -> text.replaceAll("labda", "lambda");
        Function<String, String> pipeline = headerProcessing.andThen(spellCheckerProcessing);
        String result2 = pipeline.apply("Aren't labdas really sexy?!!");
        System.out.println(result2);
    }

    private static abstract class ProcessingObject<T> {
				
				// 특정 작업을 마치고 다음 작업으로 successor를 호출합니다.
        protected ProcessingObject<T> successor;

        public void setSuccessor(ProcessingObject<T> successor) {
            this.successor = successor;
        }

        public T handle(T input) {
            T r = handleWork(input);
            if (successor != null) {
                return successor.handle(r);
            }
            return r;
        }

        abstract protected T handleWork(T input);

    }

    private static class HeaderTextProcessing extends ProcessingObject<String> {

        @Override
        public String handleWork(String text) {
            return "From Raoul, Mario and Alan: " + text;
        }

    }

    private static class SpellCheckerProcessing extends ProcessingObject<String> {

        @Override
        public String handleWork(String text) {
            return text.replaceAll("labda", "lambda");
        }

    }

}
```

<br>

### 팩토리 로직에 적용한 람다

```java
// 다음과 같은 생성 로직을
public static Product createProduct(String name) {
    switch (name) {
        case "loan":
            return new Loan();
        case "stock":
            return new Stock();
        case "bond":
            return new Bond();
        default:
            throw new RuntimeException("No such product " + name);
    }
}

// 다음과 같이 메서드 호출을 사용하여 변경할 수 있음
final static private Map<String, Supplier<Product>> map = new HashMap<>();

static {
    map.put("loan", Loan::new);
    map.put("stock", Stock::new);
    map.put("bond", Bond::new);
}

public static Product createProductLambda(String name) {
    Supplier<Product> p = map.get(name);
    if (p != null) {
        return p.get();
    }
    throw new RuntimeException("No such product " + name);
}
```

<br><br>

## ✅ 9.3 람다 테스팅

람다를 직접적으로 테스트하기에는 어려움이 있습니다.

우선 람다는 익명 함수이므로 람다 자체를 직접 호출하여 테스트할 수 는 없습니다.

람다는 다음과 같이 테스트할 수 있습니다.

1. **람다를 필드에 저장해 재사용 및 테스트를 가능케함**
2. **람다를 사용하는 메서드를 테스트하여 람다식까지 같이 테스트**
3. **람다식이 복잡하다면 복잡한 람다식을 개별 메서드로 분할**
4. **람다식의 결과가 값이나 객체가 아니라, 함수를 반환하는 경우 함수의 동작 자체를 테스트**

사실상 2번 방법이 가장 많이 사용되는 방식이 아닐까 합니다. private 메서드를 테스트 하는 방식과 마찬가지로, 결국 테스트가 해당 메서드를 사용하게 하여 검증할 수 있습니다.

<br><br>

## ✅ 9.4 디버깅

람다 표현식에 문제가 있다면 디버깅을 해야 합니다.

- 스택 트레이스
- 로깅

주로 디버깅을 할때는 위의 2개를 살펴봅니다.

<br>

### 람다의 스택 트레이스

람다 표현식이 스택 트레이스에 표현될때 가장 큰 걸림돌은 람다식이 이름이 없다는 점입니다.

따라서 람다식에서 발생한 예외의 스택 트레이스는 다음과 같습니다.

```java
public class Debugging {

    public static void main(String[] args) {
				// 두번째 인자가 null이므로 NPE 발생
        List<Point> points = Arrays.asList(new Point(12, 2), null);
        points.stream().map(p -> p.getX()).forEach(System.out::println);
    }

    private static class Point {

        private int x;
        private int y;

        private Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

    }

}

Exception in thread "main" java.lang.NullPointerException: Cannot invoke "modernjavainaction.chap09.Debugging$Point.getX()" because "p" is null
	at modernjavainaction.chap09.Debugging.lambda$main$0(Debugging.java:10)
	at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:197)
	at java.base/java.util.Spliterators$ArraySpliterator.forEachRemaining(Spliterators.java:992)
	at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:509)
	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:499)
	at java.base/java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
	at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
	at java.base/java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:596)
	at modernjavainaction.chap09.Debugging.main(Debugging.java:10)
```

- 람다식이 익명이므로 스택 트레이스에도 람다식의 이름이 존재하지 않습니다.
- 메서드 참조를 이용해도 메서드명이 존재하지 않습니다.
- **메서드 참조를 사용하는 클래스와 같은 곳에 선언되어 있는 메서드를 참조하면 해당 메서드 이름은 스택 트레이스에 나타납니다.**

<br>

### 정보 로깅

Stream API에서 peek이라는 기능을 사용하면 스트림의 각 요소를 소비한것처럼 동작합니다.

peek 메서드는 자신이 확인한 요소를 파이프라인의 다음 연산으로 그대로 전달합니다.

```java
public class Peek {

    public static void main(String[] args) {
        List<Integer> result = Stream.of(2, 3, 4, 5)
								// peek을 통해 map으로 전달될 요소들을 출력함
                .peek(x -> System.out.println("taking from stream: " + x))
                .map(x -> x + 17)
                .peek(x -> System.out.println("after map: " + x))
                .filter(x -> x % 2 == 0)
                .peek(x -> System.out.println("after filter: " + x))
                .limit(3)
                .peek(x -> System.out.println("after limit: " + x))
                .collect(toList());
    }

}
```

<br><br><br>

# 📌 10장: 람다를 이용한 도메인 전용 언어

프로그램은 사람이 이애할 수 있도록 작성되어야 하고 작성하는 의도가 명확하게 전달되어야 합니다.

Domain-Specific Languages는 소프트웨어 영역에서 일긱 쉽고 이해하기 쉬운 코드를 만들기 위해 고안되었습니다.

- `외부 DSL`
    
    DB 쿼리를 자바로 작성하지 않고 SELECT * FROM menu와 같이 SQL을 사용하는 방식은 DSL을 통해 데이터베이스를 조작하자는 의미와 일치합니다.
    
    위와 같이 데이터베이스가 텍스트로 구현된 SQL을 파싱하고 평가하는 API를 제공해주므로 이를 `외부 DSL`이라고 합니다.
    
    외부 DSL을 직접 구현하려면 DSL 문법 뿐만 아니라 DSL을 평가하는 parser도 구현해야 합니다.
    
- `내부 DSL`
    
    내부 DSL은 애플리케이션 수준 기본 요소를 데이터베이스를 나타내는 하나 이상의 클래스 유형에서 사용할 수 있는 Java 메서드로 노출됩니다. 외부 DSL과 대조적입니다. → 결국 Java 언어로 구성된 DSL을 말합니다.
    
- `fluent style`
    
    스트림 API를 활용한 메서드 체인은 자바의 반복문을 사용하는것보다 더욱 유창한 표현을 제공해주므로 불리게 됨
    
    ```java
    menu.stream()
    				.filter(d -> d.getCalories() < 400)
    				.map(Dish::getName)
    				.forEach(System.out::println);
    ```
    
<br><br>

## ✅ 10.1 도메인 전용 언어

- DSL은 특정 비즈니스 도메인의 문제를 해결하고자 만든 언어 입니다.
- DSL은 특정 비즈니스 도메인을 인터페이스로 만든 API라고 생각할수도 있습니다.
- DSL은 특정 도메인에 국한되므로 자신 앞에 놓은 문제의 해결에만 집중합니다.
따라서 특정 도메인의 복잡성을 더 잘 다룹니다.
- DSL의 하위 수준 구현 세부사항을 숨겨야 좀 더 사용자 친화적입니다.

결국 DSL은 다음과 같은 이유로 개발해야 합니다.

- `커뮤니케이션이 가장 중요`: 코드의 의도가 명확히 전달되어야 합니다.
- `한 번 작성되는 코드지만 여러번 읽어야 함`: 동료가 쉽게 이해할 수 있게 코딩해야 합니다.

<br>

### DSL의 장단점

- 장점
    - `간결함`: 비즈니스 로직을 캡슐화 → 코드 간결해짐
    - `가독성`: 도메인 전문가 및 비 전문가도 코드를 쉽게 이해할 수 있음
    - `유지보수`: 잘 설계된 DSL은 쉽게 유지보수 할 수 있고 바꿀 수 있다.
    - `높은 수준의 추상화`: DSL은 도메인과 같은 추상화 수준에서 동작함 → 도메인 비관련 세부 사항 숨김
    - `집중`: 비즈니스 도메인의 규칙을 표현할 목적으로 설계됐으므로 프로그래머가 특정 코드에 집중할 수 있음
    - `관심사분리`: 비즈니스 로직을 지정된 언어로 표현하므로 애플리케이션의 인프라스트럭처와 쉽게 분리 가능 → 결국 유지보수가 쉬운 코드 생성
- 단점
    - `DSL 설계의 어려움`: 제한적인 언어에 도메인 지식을 담는 것은 쉬운 작업이 아님
    - `개발 비용`: DSL을 추가하게 되므로 초기 프로젝트 및 DSL 유지보수에 많은 비용 소모됨
    - `추가 우회 계층`: DSL은 추가적인 계층으로 도메인을 감쌈 → 비용
    - `새로운 언어`: DSL을 기존언어와 다른 언어로 쓴다면 새로운 언어를 배워야 함
    - `호스팅 언어 한계`: Java는 장황하고 엄격한 문법을 가집니다. 이를 이용해 사용자 친화적 DSL을 만들기 어렵습니다.

<br>

### 내부 DSL

내부 DSL은 자바 언어로 만듭니다.

자바 언어의 부족한 유연성을 람다가 보충해줍니다.

```java
public static void main(String[] args) {
    List<String> numbers = Arrays.asList("one", "two", "three");
		
		// 1.
    System.out.println("Anonymous class:");
    numbers.forEach(new Consumer<String>() {

        @Override
        public void accept(String s) {
            System.out.println(s);
        }

    });
		
		// 2.
    System.out.println("Lambda expression:");
    numbers.forEach(s -> System.out.println(s));

		// 3.
    System.out.println("Method reference:");
    numbers.forEach(System.out::println);
}
```

1에 비해 2, 3 방식은 딱 필요한 코드만 존재합니다.

순수 자바를 이용해 DSL을 만들면 다음과 같은 장점이 존재합니다.

- 새로운 패턴 기술을 배우는 것보다 노력이 현저히 줄어듦
- 같은 자바 코드이므로 나머지 코드와 함께 DSL 코드를 컴파일 할 수 있음
- 개발 팀이 새로운 언어를 배우거나 복잡한 외부 도구를 사용하지 않아도 됨
- 기존 Java IDE 사용 가능
- 한 개의 DSL 언어로 한 개에서 여러개의 도메인을 대응하지 못해 추가 DSL을 개발할때 쉽게 추가할 수 있음

<br>

### 다중 DSL

JVM 기반에서 돌아가는 언어들을 활용해 DSL을 구성하는것을 말합니다.

표현하고자 하는 도메인의 특성에 맞는 언어를 잘 고를수도 있지만 다음과 같은 불편함이 존재합니다.

- 좋은 DSL을 만들려면 고급 기능을 활용할 수 있는 충분한 지식 필요
- 두 개 이상의 언어가 혼재하므로 여러 컴파일러를 사용하여 소스를 빌드함
- JVM 실행 언어가 Java와 100퍼센트 호환되지 않을 수 있음

<br>

### 외부 DSL

외부 DSL은 자신만의 문법과 구문으로 새로운 언어를 설계해야 합니다.

가장 큰 장점은 외부 DSL이 제공하는 무한한 유연성입니다. → 내 입맛대로 설계하면 되므로

또한 자바로 개발된 인프라스트럭처와 외부 DSL로 구현한 비즈니스 코드를 쉽게 분리할 수 있습니다.

하지만 새로운 언어를 만드는것은 어렵고, 어렵고 어렵습니다. → 많은 비용 필요

<br><br>

## ✅ 10.2 최신 자바 API의 작은 DSL

람다가 등장하면서 네이티브 Java API에 변화가 생겼습니다.

단일 추상 메서드를 사용하는 몇 가지 인터페이스의 구현이 부피가 컸던 익명 클래스에서 람다와 메서드 참조를 활용해 필요한 코드만 작성할 수 있습니다.

Java 8의 Comparator 인터페이스는 람다 표현식을 활용해 정렬할 객체가 직접적으로 Comparator클래스를 익명 클래스로 작성하지 않고 람다를 활용할 수 있습니다.

```java
public class DslSortingWithComparator {
    public static void main(String[] args) {
        
        // Java 8 이전의 방식 직접 클래스를 구현함 -> 부가적인 코드가 너무 많음
        Collections.sort(menu, new Comparator<Dish>() {
            @Override
            public int compare(final Dish o1, final Dish o2) {
                return o1.getCalories() - o2.getCalories();
            }
        });
        
        // Java 8의 람다 사용
        Collections.sort(menu, Comparator.comparing(d -> d.getCalories()));
        // Java 8의 메서드 참조 사용
        Collections.sort(menu, Comparator.comparing(Dish::getCalories));
        // List 인터페이스의 sort 메서드 사용
        menu.sort(Comparator.comparing(Dish::getCalories));
    }
}
```

<br>

### 스트림 API == 컬렉션을 조작하는 DSL

다음 코드는 특정 텍스트 파일을 읽어와 ERROR로 시작하는 문자라면 40개까지 담는 로직입니다.

```java
public class StreamDsl {
    public static void main(String[] args) throws IOException {
        List<String> errors = new ArrayList<>();
        int errorCount = 0;
        FileReader fileReader = new FileReader(
                StreamDsl.class.getClassLoader().getResource("modernjavainaction/chap10/error.txt").getFile());

        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();
        while (errorCount < 40 && line != null) {
            if (line.startsWith("ERROR")) {
                errors.add(line);
                errorCount++;
            }
            line = bufferedReader.readLine();
        }

        System.out.println(errors);
    }
}
```

위 코드는 몇가지 문제점이 존재함

- 동일한 역할의 코드가 분산되어 있음
    - FileReader의 생성
    - 파일을 다 읽었는지 확인하는 `line != null`
    - 파일의 다음행을 읽는 과정 `line = bufferedReader.readLine();`
- 첫 40행을 수집하는 코드가 분산 되어 있음
    - errorCount 변수를 초기화하는 코드
    - while 루프의 첫 번째 조건 코드
    - “ERROR”를 고르에서 발견하면 카운팅하는 코드

스트림 API를 활용하면 간단하게 줄일 수 있습니다.

```java
Path path = Paths.get("/Users/ihoseok/Downloads/source/ModernJavaInAction-master/target/classes/modernjavainaction/chap10/error.txt");
List<String> errors2 = Files.lines(path)
        .filter(fileLine -> fileLine.startsWith("ERROR"))
        .limit(40)
        .collect(Collectors.toList());
```

스트림 API의 fluent 형식은 잘 설계된 DSL의 특징입니다.

중간연산은 지연동작되고 체인을 구성할 수 있게 스트림 자신을 반환합니다. 이후 최종 연산에서 모든 스트림 요소들을 사용하여 연산합니다.

<br>

### 데이터를 수집하는 DSL인 Collectors

Stream API에서 최종 연산으로 collect를 사용하면 스트림의 요소들을 수집, 그룹화, 분할과 같은 작업을 수행할 수 있습니다.

또한 Collectors가 제공하는 여러 정적 팩토리 메서드를 활용해 필요한 Collector 객체를 만들고 합칠수도 있습니다.

DSL 관점에서 어떻게 메서드가 설계되었는지 알아볼 수 있습니다.

**Comparator는 메서드 체이닝(fluent style), Collectors는 중첩 메서드**

```java
// Comparator 메서드는 fluent style임
Comparator<Person> comparator =
        comparing(Person::getAge).thenComparing(Person::getName);

// 반면 Collectors 정적 팩터리 메서드는 중첩 메서드 스타일입니다.
Map<String, Map<Color, List<Car>>> carsByBrandAndColor = cars.stream()
				.collect(groupingBy(Car::getBrand, groupingBy(Car::getColor)));
```

위와 같이 두 스타일의 차이점은 메서드가 전하는 의미를 명확히 하기 위한 디자인 적인 선택입니다.

Comparator는 사람의 나이를 가지고 정렬하고, 동일한 나이라면 이름순으로 정렬하기에 fluent style이 어울립니다.

번면에 Collectors의 groupingBy는 가장 안쪽의 괄호의 그룹화가 먼저 진행되야 하지만, 논리적으로는 가장 마지막에 그룹화 되어야 하기 때문에 의도적인 중첩 디자인을 채택했습니다.

```java
public class Grouping {

    enum CaloricLevel {DIET, NORMAL, FAT};

    private static CaloricLevel getCaloricLevel(Dish dish) {
        if (dish.getCalories() <= 400) {
            return CaloricLevel.DIET;
        } else if (dish.getCalories() <= 700) {
            return CaloricLevel.NORMAL;
        } else {
            return CaloricLevel.FAT;
        }
    }

    private static Map<Dish.Type, Map<CaloricLevel, List<Dish>>> groupDishedByTypeAndCaloricLevel3() {
        Collector<? super Dish, ?, Map<Dish.Type, Map<CaloricLevel, List<Dish>>>> c = groupOn((Dish dish) -> getCaloricLevel(dish)).after(Dish::getType).get();
        return menu.stream().collect(c);
    }

    public static class GroupingBuilder<T, D, K> {

        private final Collector<? super T, ?, Map<K, D>> collector;

        public GroupingBuilder(Collector<? super T, ?, Map<K, D>> collector) {
            this.collector = collector;
        }

        public Collector<? super T, ?, Map<K, D>> get() {
            return collector;
        }

        public <J> GroupingBuilder<T, Map<K, D>, J> after(Function<? super T, ? extends J> classifier) {
            return new GroupingBuilder<>(groupingBy(classifier, collector));
        }

        public static <T, D, K> GroupingBuilder<T, List<T>, K> groupOn(Function<? super T, ? extends K> classifier) {
            return new GroupingBuilder<>(groupingBy(classifier));
        }

    }

}
```

위 builder에서 중첩화된 그룹화와 동일한 결과를 내려면 다음 코드와 같이 사용해야 합니다.

```java
Collector<? super Dish, ?, Map<Dish.Type, Map<CaloricLevel, List<Dish>>>> 
		c = groupOn((Dish dish) -> getCaloricLevel(dish))
						.after(Dish::getType)
						.get();
```

중첩된 그룹화와 반대 순서로 fluent style을 구성해야 하므로 코드 자체가 직관적이지 않습니다.

위와 같이 내부 설계를 왜 해당 디자인을 채택했을지를 고민해보면 DSL을 구현하는 유용한 패턴을 익힐 수 있습니다.

<br><br>

## ✅ 10.3 자바로 DSL 만들기

<br>

### 1. 메서드 체인

```java
Order order = forCustomer("BigBank")
        .buy(80)
            .stock("IBM")
            .on("NYSE")
            .at(125.00)
        .sell(50)
            .stock("GOOGLE")
            .on("NASDAQ")
            .at(375.00)
        .end();
```

흔히 사용하는 빌더 패턴으로 구성됩니다.

필요에 따라 여러 빌터 클래스를 조합해 사용자에게 지정된 순서로 fluent api를 호출하도록 강제할 수 있습니다.

- 장점
    - 메서드 이름 == 키워드 인수 역할
    - 도메인 필드에서 선택형 데이터가 있다면 사용하기 적합
    - DSL 사용자에게 정해진 순서대로 메서드를 호출하도록 강제할 수 있음
    - 정적 메서드를 최소한으로 사용함
    - 의미없는 코드량이 적음
- 단점
    - 구현 코드량이 많음
    - 사용할 빌더들을 연결하는 접착 코드가 반드시 필요함
    - 도메인 객체 계층을 들여쓰기만으로 표현할 수 있음

<br>

### 2. 중첩함수 활용

```java
Order order = order("BigBank",
        buy(80,
                stock("IBM", on("NYSE")),
                at(125.00)),
        sell(50,
                stock("GOOGLE", on("NASDAQ")),
                at(375.00))
);
```

중첩 함수 방식은 도메인 객체의 계층 구조를 그대로 반영할 수 있다는 장점이 있습니다.

- 장점
    - 구현의 장황함을 대폭 줄일 수 있음
    - 함수 중첩을 통해 도메인 객체 계층을 표현할 수 있음
- 단점
    - 정적 메서드의 사용이 굉장히 많음
    - 이름이 아니라 위치를 통해 인수를 정의함
    - 도메인에 선택적인 필드가 있다면 별도의 메서드 오버라이드 필요

<br>

### 3. 람다 표현식을 이용한 함수 시퀀싱

```java
Order order = LambdaOrderBuilder.order(o -> {
    o.forCustomer("BigBank");
    o.buy(t -> {
        t.quantity(80);
        t.price(125.00);
        t.stock(s -> {
            s.symbol("IBM");
            s.market("NYSE");
        });
    });
    o.sell(t -> {
        t.quantity(50);
        t.price(375.00);
        t.stock(s -> {
            s.symbol("GOOGLE");
            s.market("NASDAQ");
        });
    });
});
```

메서드 체인 패턴으 주문을 생성하는 최상위 빌더를 가지지만, 함수 시퀀싱에선 Consumer를 받음으로써 사용자가 람다 표현식을 통해 인수를 구현할 수 있게 합니다.

메서드 체인의 fluent style + 중첩함수 형식의 람다 적용을 통해 도메인 객체의 계층 구조를 유지합니다.

- 장점
    - 도메인에 선택적인 필드가 있다면 적합함
    - 정적 메서드를 최소화 할 수 있음
    - 람다 중첩을 통해 도메인 계층을 반영할 수 있음
    - 빌더에 필요한 접착 코드가 필요치 않음
- 단점
    - 구현이 복잡
    - 람다 표현식을 활용하므로 필요없는 코드량이 많아짐

<br>

### 조합하여 사용

```java
Order order = forCustomer("BigBank",
        buy(t -> t.quantity(80)
                .stock("IBM")
                .on("NYSE")
                .at(125.00)),
        sell(t -> t.quantity(50)
                .stock("GOOGLE")
                .on("NASDAQ")
                .at(375.00)));
```

여러 DSL 기법을 혼용하기 때문에 각 기법의 장점만 이용하여 조합할 수 있습니다.

- 장점
    - 여러 기법중에서 필요한 기법만을 활용하므로 도메인을 표현하는데 최적화 됨
- 단점
    - DSL을 사용하는 사용자가 조합된 여러 기법을 익히는데 많은 시간이 듦

<br>

### DSL에 메서드 참조 활용하기

```java
// 각 주문에 적용할 세금 목록
public class Tax {

    public static double regional(double value) {
        return value * 1.1;
    }

    public static double general(double value) {
        return value * 1.3;
    }

    public static double surcharge(double value) {
        return value * 1.05;
    }

}

// 세금 계산기
public class TaxCalculator {
    public DoubleUnaryOperator taxFunction = d -> d;

    public TaxCalculator with(DoubleUnaryOperator f) {
        taxFunction = taxFunction.andThen(f);
        return this;
    }

    public double calculateF(Order order) {
        return taxFunction.applyAsDouble(order.getValue());
    }

    public static void main(String[] args) {
        Order order = forCustomer("BigBank",
	              buy(t -> t.quantity(80)
	                      .stock("IBM")
	                      .on("NYSE")
	                      .at(125.00)),
	              sell(t -> t.quantity(50)
	                      .stock("GOOGLE")
	                      .on("NASDAQ")
	                      .at(125.00)));

        value = new TaxCalculator().with(Tax::regional)
                .with(Tax::surcharge)
                .calculateF(order);

        System.out.printf("Method references: %.2f%n", value);
    }

```

DoubleUnaryOperator 매개인자를 통해 계산할 세금의 종류를 with() 메서드에 전달합니다. 이때 andThen이라는 함수를 계속 조합하고, 최종 계산에서 적용한 모든 함수에 대한 결과값을 받습니다.

<br>

### 실생활의 자바 8 DSL 예제

<br>

**JOOQ**

QueryDSL과 함께 자주 거론되는 자바 코드 기반 DB 쿼리를 작성할 수 있습니다.

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/bc16413f-851c-4aa6-a932-df8df68d4aa6)

위의 형식을 보면 알겠지만 `메서드 체인 패턴`을 중점적으로 활용합니다.

DB 쿼리의 특성상 필수적으로 입력되어야 하는 구문이 있으므로 사용자에게 정해진 순서대로 사용할 수 있게 강제하거나 선택적인 파라미터를 입력하는데 적합합니다.

<br>

**Cucumber**

BDD 지원용 툴로 외부 DSL을 활용하여 테스팅을 할 수 있습니다.

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/46d31d4a-42b9-4645-bf7a-849dcdc44473)

위와 같이 시나리오를 작성하고 (given, when, then에 주목)

```java
public class BuyStocksSteps {

    private Map<String, Integer> stockUnitPrices = new HashMap<>();
    private Order order = new Order();

    @Given("^the price of a \"(.*?)\" stock is (\\d+)\\$$")
    public void setUnitPrice(String stockName, int unitPrice) {
        stockUnitValues.put(stockName, unitPrice);
    }

    @When("^I buy (\\d+) \"(.*?)\"$")
    public void buyStocks(int quantity, String stockName) {Trade trade = new Trade();
				trade.setType(Trade.Type.BUY);
				Stock stock = new Stock();
				stock.setSymbol(stockName);
				Populates the domain model accordinglytrade.setStock(stock);
		    trade.setPrice(stockUnitPrices.get(stockName));
		    trade.setQuantity(quantity);
		    order.addTrade(trade);
		}

@Then("^the order value should be (\\d+)\\$$")
    public void checkOrderValue(int expectedValue) {
        assertEquals(expectedValue, order.getValue());
    }
}
```

위와 같이 애노테이션을 통해 매칭하여 BDD 테스팅을 진행할 수 있습니다.

Java 8에 들어서면서 람다를 활용하여 의미있는 메서드의 이름을 굳이 지어주지 않아도 됩니다.

<br>

**Spring Integration**

```java
@Configuration
@EnableIntegration
public class MyConfiguration {

    @Bean
    public MessageSource<?> integerMessageSource() {
        MethodInvokingMessageSource source =
                new MethodInvokingMessageSource();
        source.setObject(new AtomicInteger());
        source.setMethodName("getAndIncrement");
        return source;
		}

		@Bean
		public DirectChannel inputChannel() {
		    return new DirectChannel();
		}

		@Bean
		public IntegrationFlow myFlow() {
		    return IntegrationFlows.from(this.integerMessageSource(),c -> c.poller(Pollers.fixedRate(10)))
								.channel(this.inputChannel())
								.filter((Integer p) -> p % 2 == 0)
								.transform(Object::toString)
								.channel(MessageChannels.queue("queueChannel"))
								.get();
		}
}
```

Spring Integration에서도 메서드 체이닝을 주로 사용하고 필요하다면 람다 표현식을 적용할 수 있습니다.

<br><br>

## ✅ 알아두어야 할 핵심 가치

1. **DSL의 목적은 개발자 ↔ 도메인 전문가 사이의 간극을 좁히는 것**
    
    개발자가 아닌 사람도 이해할 수 있는 언어로 비즈니스 로직을 구현하기 위함
    
2. DSL은 `내부 DSL`(자바 언어 혹은 해당 애플리케이션을 만들때 적용한 언어), `외부 DSL`(애플리케이션 언어와 달리 특화된 언어 혹은 직접 만든 언어) 크게 존재함
3. 다중 DSL을 개발할 수 있음(JVM 위에서 돌아가는 언어를 통해) → 통합시 문제 발생 여지 있음
4. Java는 내부 DSL을 구현하기 적합치 않으나 람다 및 메서드 참조를 통해 많이 개선됨
5. 최신 자바는 자체 API에서 DSL을 제공해주고 있음(Stream, Collectors, Comparator등)
6. 자바의 DSL 패턴은 `메서드 체인`, `중첩 함수`, `함수 시퀀싱`과 같이 크게 3가지가 존재
7. 여러 자바 프레임워크는 이미 DSL을 적극 활용해 구현되어 있음
