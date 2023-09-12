# 📌 13장: 디폴트 메서드

정말 상용화되어 사용되고 있는 인터페이스를 변경하면 인터페이스를 구현했던 클래스들의 구현도 고쳐야 하는 문제점이 있습니다.

Java 8 부터는 기본 구현을 포함하는 인터페이스를 정의하는 두 가지 방법을 제공합니다.

- static method
- `default method`

위와 같은 방법을 통해 인터페이스에서 메서드 구현을 포함하는 인터페이스를 정의할 수 있습니다.
따라서 기존 코드 구현을 바꾸도록 강요하지 않고 인터페이스를 변경할 수 있습니다.

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/2265201d-3d96-45d5-b8f4-eaf354304ad2)

<br>

### 대표적인 default, static method

- java.util.List 인터페이스의 sort 메서드
    
    ```java
    // Java 17
    @SuppressWarnings({"unchecked", "rawtypes"})
    default void sort(Comparator<? super E> c) {
        Object[] a = this.toArray();
        Arrays.sort(a, (Comparator) c);
        ListIterator<E> i = this.listIterator();
        for (Object e : a) {
            i.next();
            i.set((E) e);
        }
    }
    ```
    
    List 객체에 직접 sort를 호출할 수 있게됨
    
- java.util.Comparator의 naturalOrder()
    
    ```java
    @SuppressWarnings("unchecked")
    public static <T extends Comparable<? super T>> Comparator<T> naturalOrder() {
        return (Comparator<T>) Comparators.NaturalOrderComparator.INSTANCE;
    }
    ```
    
    오름차순으로 요소를 정렬할 수 있도록 하는 Comparator 객체를 반환합니다.
    

> java.util 패키지는 default method 이전에 인터페이스의 인스턴스를 활용할 수 있는 다양한 정적 메서드를 정의하는 유틸클래스를 제공해줍니다. (Collection 객체를 활용하는 유틸클래스 Collections)

Java 8부터 정적 메서드를 인터페이스 내부에 직접 선언할 수 있게 됐으나, 호환성 유지를 위해 남아 있습니다.
> 

<br><br>

## ✅ 13.1 변화하는 API

```java
public interface Quackable {
    void quack();
}

class Duck implements Quackable {

    @Override
    public void quack() {
        System.out.println("quack");
    }
		
}

class Application {

    public static void main(String[] args) {
        Duck duck = new Duck();
        duck.quack();
    }
}
```

- Quckable을 구현하는 Duck 클래스는 quack이라는 메서드를 오버라이드 합니다.
- 보통 꽥 거리는 것들은 여러번 꽥꽥거리므로, 꽥거리는 숫자를 넘겨주면 해당 숫자만큼 꽥할 수 있도록 기능을 제공하고 싶습니다.
- 이때 default method를 활용해, 기존의 구현을 변경하지 않고 기능을 구현할 수 있습니다.

```java
public interface Quackable {
    void quack();

		// default 메서드 추가
    default void multiQuack(int count) {
        for (int i = 0; i < count; i++) {
            quack();
        }
    }
}
```

<br>

### 바이너리, 소스, 동작 호환성

- 바이너리 호환성
    - `정의`: 변경 이후에도 컴파일을 하지 않고, 바이너리를 실행할 수 있는 특성을 의미함
    - `등장이유`: 인터넷과 같은 분산환경에서 변경되는 유형(인터페이스 등)에 직간접적으로 의존하는 기존 바이너리를 자동으로 재 컴파일하는 것이 비실용적이고, 불가능한 경우가 많음
    - `바이너리 호환성의 목적`: 개발자가 기존 바이너리와의 호환성을 유지하며, 클래스 또는 인터페이스 유형에 적용할 수 있는 일련의 변경 사항을 정의합니다.
    - `발생할 수 있는 상황`
        
        https://docs.oracle.com/javase/specs/jls/se8/html/jls-13.html
        https://wiki.openjdk.org/display/csr/Kinds+of+Compatibility
        
        ![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/528977f2-bdb8-41df-b97c-58085156d623)

        

- 소스 호환성
    - 코드를 고쳐도 기존 프로그램을 성공적으로 재컴파일할 수 있음을 의미(인터페이스에 새로운 추상 메서드 추가는 소스 호환성이 아님 → 구현 클래스를 고쳐야 하므로)

- 동작 호환성
    - 코드를 바꾼 다음에도 같은 입력값이 주어지면 프로그램이 같은 동작을 실행한다는 의미

<br><br>

## ✅ 13.2 default method란 무엇인가?

- Java 8에서 호환성을 유지하며 API를 바꿀 수 있게 하는 기능
- 인터페이스의 구현체에게 새로운 메서드를 구현하지 않을 수 있는 새로운 메서드 시그니처 제공
- 인터페이스 자체에서 기본으로 제공하는 메서드(그래서 default method
- default method는 추상 메서드가 아니다.

- default 메서드 정의 방법
    - default 라는 키워드로 시작되며 메서드 바디를 가짐
        
        ```java
        public interface Sized {
        		int size();
        
        		default boolean isEmpty() {
        				return size() == 0;
        		}
        }
        ```
        

> **추상 클래스와 인터페이스의 차이**

1. 클래스는 하나의 추상 클래스만 상속받을 수 있지만, 인터페이스는 여러개를 구현할 수 있음
2. 추상 클래스는 인스턴스 변수로 상태를 갖지만, 인터페이스는 인스턴스 변수를 가질 수 없다.
> 

<br><br>

## ✅ 13.3 default method 활용 패턴

- 디폴트 메서드를 이용하는 두 가지 방식이 존재합니다.
    1. 선택형 메서드
    2. 동작 다중 상속

<br>

### 선택형 메서드

- java.util.Iterator 인터페이스는 remove라는 추상 메서드를 제공하고 있지만, 실질적으로 잘 사용하고 있지 않았기에 많은 구현체들이 빈 구현을 제공하고 있었습니다.
- default method를 활용하면 remove 메서드에 대해 선택권을 줄 수 있습니다.
    
    ```java
    public interface Iterator<E> {
        
        boolean hasNext();
    
        E next();
    
        default void remove() {
            throw new UnsupportedOperationException("remove");
        }
    
        default void forEachRemaining(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            while (hasNext())
                action.accept(next());
        }
    }
    ```
    
    - 하위 구현체에서 remove 메서드를 사용하지 않는다면 굳이 빈 구현을 제공하지 않아도 되고(심지어 예외까지 터트려줌)
    - 구현이 필요하다면 default method인 remove를 오버라이드하면 됩니다.

<br>

### 동작 다중 상속

```java
package modernjavainaction.chap13.self.multiplebehavior;

public class Application {
}

class HurtPerson implements Swimmable {

    @Override
    public void swim() {
        System.out.println("swimming...");
    }
}

class NormalPerson implements Runnable, Swimmable, Jumpable {

    @Override
    public void run() {
        System.out.println("running...");
    }

    @Override
    public void swim() {
        System.out.println("swimming...");

    }

    @Override
    public void jump() {
        System.out.println("jumping...");
    }
}

interface Runnable {
    void run();
    
    default void prepareRunning() {
        System.out.println("러닝 준비중...");
    }
}

interface Swimmable {
    void swim();

    default void prepareSwimming() {
        System.out.println("수영 준비중...");
    }
}

interface Jumpable {
    void jump();

    default void prepareJumping() {
        System.out.println("점프 준비중...");
    }
}
```

- 필요에 따른 인터페이스 조합
    - 보통 사람(NormalPerson)은 뛰고, 점프하고, 수영을 할 수 있습니다.
    - 하지만 다친 사람(HurtPerson)은 상대적으로 부담이 덜한 수영만 할 수 있습니다.
- 재사용성
    - 이렇게 인터페이스를 조합하여 다양한 상태를 가진 사람(클래스)을 생성할 수 있습니다.
    후에 다른 사람이 필요하다면 위의 인터페이스들을 재사용할 수 있습니다.
- default method 장점 극대화
    - 각 인터페이스는 각 동작을 실행하기 위한 준비를 담당하는 default method를 제공합니다.
    - default method를 이용해 구현한 덕에 준비 과정이 좀 더 상세하게 필요하다면 해당 default method 하나만 수정하면 클래스들은 변경한 코드를 상속받게 되므로 좀 더 편리하게 이용가능합니다.
    - 만약 각자 다른 동작이 필요하다면 default method를 재정의하면 됩니다.

> **옳지 못한 상속**

상속으로 재사용 문제를 모두 해결할 수 있는것은 아닙니다. 하나의 기능이 필요한데 100개의 메서드 및 필드가 정의되어 있는 클래스를 상속받는 것은 옳지 못합니다.

이럴때는 멤버 변수를 이용해 클래스에서 필요한 메서드를 직접 호출하는 메서드를 작성하는것이 좋습니다.
> 

<br><br>

## ✅ 13.4 해석 규칙

자바는 여러 인터페이스를 구현할 수 있습니다. default method가 추가 되면서 같은 시그니처를 갖는 default method를 상속받는 상황이 생길 수 있습니다. 이때 자바 컴파일러는 이러한 충돌을 어떻게 해결하는지 설명합니다.

<br>

### 알아둬야 할 세가지 해결 규칙

1. 클래스가 항상 이긴다. 클래스 및 슈퍼클래스에서 정의한 메서드가 default method보다 우선권을 갖습니다.
2. 1번 규칙 이외 상황에선 서브인터페이스가 이긴다. 상속 관계를 갖는 인터페이스에서 같은 시그니처를 갖는 메서드를 정의할 때는 서브 인터페이스가 이깁니다. (B가 A를 상속받는다면 B가 A를 이깁니다.)
3. default method의 우선순위가 1, 2를 고려했음에도 결정되지 않았다면 여러 인터페이스를 상속받는 클래스가 명시적으로 default method를 오버라이드하고 호출해야 합니다.

<br>

### 디폴트 메서드를 제공하는 서브인터페이스가 이긴다.

`A, B는 인터페이스, C는 구현 클래스`

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/d032a975-8524-475f-8373-9a9931cc5f64)


- A, B는 인터페이스이고, hello()는 default method입니다.
- C 클래스가 A, B 인터페이스를 구현했을때 hello()를 호출하면 B의 hello()가 호출됩니다. (`규칙 2`)

`A, B는 인터페이스`, `D는 A 인터페이스 구현`, `C는 D상속 및 A, B 인터페이스 구현`

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/65cbec42-2643-4708-b11a-d652ce02d3a8)


- 1번 규칙은 클래스 및 슈퍼 클래스에 정의한 메서드가 우선권을 갖는다고 하지만 D 클래스에 정의한 메서드가 없습니다.
    - 만약 D클래스에 `void hello()`라는 메서드를 오버라이드하면 D클래스의 hello() 메서드가 호출됩니다.
- 따라서 B클래스의 hello() 메서드가 호출됩니다.

<br>

### 충돌 그리고 명시적인 문제 해결

`A, B는 인터페이스, C는 A, B를 구현한 클래스`

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/68b3af13-26fe-4d78-b67d-f1c2e0fd9f8d)


```java
public class Ambiguous {

    public static void main(String... args) {
        new C().hello();
    }

    interface A {

        public default void hello() {
            System.out.println("Hello from A");
        }

    }

    interface B {

        public default void hello() {
            System.out.println("Hello from B");
        }

    }

    static class C implements B, A {

        @Override
        public void hello() {
            A.super.hello();
        }

    }

}
```

- 위와 같은 상황에선 A, B의 default method 중에서 어떤 것을 호출해야 할지 모르는 모호한 상황이 발생합니다.
- 따라서 구현 클래스에서 명시적으로 어떤 인터페이스의 메서드를 호출할지 결정합니다.
- Java 8은 `A.super().hello()`와 같은 새로운 문법을 제공합니다.
- 두 개의 hello의 반환값이 서로 다르면 컴파일 에러가 발생합니다. → 메서드를 구분할 수 없음
    - 만약 Integer, Number와 같이 상하 관계를 갖는 타입이 반환 타입으로 된다면 하위 타입(더 구체적인)을 반환하도록 메서드를 재정의하면 해결할 수 있습니다. → 부모는 자식을 참조할 수 있으므로

<br>

### 다이아몬드 문제

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/07d344d4-4479-47da-8116-0055abfa007c)


- C, B 인터페이스는 결국 A의 hello()를 상속받고 재정의하지 않으므로 D 클래스에서 hello를 호출하면 A의 hello()가 호출됩니다.

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/828fb3be-c2e7-47ed-b57c-0a97753a8472)


- 위와 같이 C 인터페이스에 추상 메서드 hello()를 추가하면 D클래스는 반드시 hello() 메서드를 오버라이드 합니다.
- 오버라이딩 된 대상 메서드는 A, C 인터페이스의 hello() 메서드입니다. 따라서 해당 메서드 내에서 특정 인터페이스의 super.hello() 호출은 컴파일 에러가 발생합니다.
    
    `Bad type qualifier in default super call: method hello is overridden in modernjavainaction.chap13.Diamond.C`
    
<br><br><br>

# 📌 14장: 자바 모듈 시스템

<br><br>

## ✅ 14.1 원동력: 소프트웨어에 대한 추론

- 저수준 영역: 특정 코드를 이해하고 유지보수하기 쉬운 코드를 구성하는 과정
- 고수준 영역: 소프트웨어 아키텍처 자체를 변경하여 생산성을 높이는 과정

추론하기 쉬운 소프트웨어를 만드는데는 관심사의 분리와 정보 은닉이 큰 도움을 줍니다.

<br>

### 관심사 분리

- Separation of Concerns(SoC)는 프로그램을 고유의 기능으로 나누는 동작을 권장하는 원칙입니다.
- 지출 분석 프로그램은 다음과 같이 나뉠 수 있음
    - 지출 내역 추출
    - 분석
    - 레포트 기능
- 위와 같이 서로 겹치지 않은 영역을 코드 그룹으로 분리하고, 분리한 부분들을 모듈화하여 클래스간의 관계를 시각적으로 보여줄 수 있습니다.
- SoC의 원칙은 아래 장점을 제공합니다.
    - 개별 기능을 따로 작업 → 쉽게 협업 가능
    - 개별 부분 재사용
    - 전체 시스템을 쉽게 유지보수할 수 있음

<br>

### 정보 은닉

- 세부 구현을 숨기도록 장려하는 원칙
- 세부 구현을 숨기면 프로그램의 특정 부분이 변경 되었을 때 다른 부분에 미치는 영향을 줄일 수 있습니다.

**기존 자바가 제공하는 정보 은닉 - 캡슐화**

- 특정 코드 조각이 애플리케이션의 다른 부분과 고립되어 있음을 읭미함
- 자바에선 클래스 내의 컴포넌트에 적절한 private 키워드를 사용했는지를 통해 캡슐화를 확인할 수 있음
- 하지만 `클래스와 패키지가 의도된 대로 공개되었는지를 컴파일러로 확인할 수 있는 기능은 없음`
(Java 9의 모듈화는 가능케함)

<br><br>

## ✅ 14.2 자바 모듈 시스템을 설계한 이유

### 기존 모듈화의 한계

Java 9 이전 까지는 모듈화된 소프트웨어를 만드는데 한계가 존재함
자바는 클래스, 패키지, Jar 3가지 수준의 코드를 그룹화 할 수 있는 방법을 제공합니다.

**하지만 오직 클래스에서만 접근 제한자 및 캡슐화를 지원함**

**제한된 가시성 제어**

패키지 간의 가시성은 거의 없는 수준이라고 볼 수 있습니다.
중첩 클래스를 제외하고 클래스에 적용할 수 있는 접근 제한자는 다음과 같습니다.

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/1dca076c-3ac1-4026-8799-d25a9345f338)


- 다른 패키지에서 이용해야 한다면 반드시 public으로 선언해야 하는데 이렇게 되면 모든 패키지에서 해당 클래스를 접근할 수 있는 구조가 됩니다.
- 결국 보안 측면에서 코드를 임의로 조작할 수 있는 의협에 더 많이 노출될 수 있습니다.
    
    

**클래스 경로**

클래스를 모두 컴파일하고 한 개의 평범한 Jar 파일에 넣습니다. 이후 클래스 경로에(classpath)에 해당 Jar 파일을 추가해 사용할 수 있습니다. → JVM이 동적으로 클래스 경로에 정의된 클래스를 필요할 때 읽음

위 과정은 몇가지 문제점이 있습니다.

1. 클래스 경로에는 같은 클래스를 구분하는 버전 개념이 없음 → 다양한 컴포넌트가 같은 라이브러리를 사용하지만 버전이 다른 경우 문제가 발생할 수 있다.
2. 클래스 경로는 명시적인 의존성을 지원하지 않음 → 한 Jar가 다른 Jar에 포함된 클래스를 사용할때 명시적으로 의존성을 정의하는 기능이 없습니다.
    
    Maven, Gradle을 사용하면 이런 부분에서 도움을 줍니다.(의존관계주입)
    

Java 9의 모듈 시스템을 사용하면 컴파일 타임에 이런 종류의 에러를 모두 검출할 수 있습니다.

<br>

### 거대한 JDK

JDK 라이브러리가 점차 추가되면서 점점 무거워져 갔고 땜질식 처방인 컴팩트 프로파일이라는 기법을 사용했습니다. 컴팩트 프로파일은 관련 분야에 따라 JDK 라이브러리의 3가지 버전을 제공합니다.

이때 JDK의 내부 API는 공개되지 않아야 하는데, 낮은 캡슐화 때문에 여러 프레임워크, 라이브러리에서 JDK 내부에서만 사용하는 클래스를 사용하기도 합니다. 이런 호환성을 깨지 않고 관련 API를 바꾸는 일은 쉽지 않습니다.

결국 JDK에서 필요한 부분만 골라 사용하고, 클래스 경로를 쉽게 유추할 수 있으면 플랫폼을 진화시킬 수 있는 강력한 캡슐화를 제공해줄 새로운 아키텍처가 필요하게 됩니다.

<br><br>

## ✅ 14.3 자바 모듈 : 큰 그림

Java 9는 모듈이라는 새로운 자바 프로그램 구조 단위를 제공합니다.

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/1a93446c-2b65-4cde-a6e6-6632873a0127)


이런 모듈은 모듈 디스크립터(module-info.java)에 이름과 바디를 추가해 정의할 수 있습니다.

- exports는 내가 제공하는 패키지
- requires는 내가 필요한 모듈이라고 생각할 수 있음

<br><br>

## ✅ 14.4 ~ 14.7 자바 모듈 시스템으로 애플리케이션 개발하기

- 애플리케이션 컨셉: 영수증 관리 애플리케이션
- 필요 작업
    - 파일이나 URL에서 비용목록 읽음
    - 비용의 문자열 표현을 파싱
    - 통계 계산
    - 유용한 요약 정보 표시
    - 각 태스크의 시작, 마무리 지점 표시

- 실습 코드는 다음과 같음
    
    https://github.com/HiiWee/hiiwee-lab/tree/master/module
    
- java command 명령어
    
    ```java
    // 컴파일 하여 target 디렉토리에 저장
    javac module-info.java com/example/expenses/application/ExpensesApplication.java -d target .
    
    // target 디렉토리의 컴파일 결과를 이용해 jar 파일 생성
    jar cvfe expenses-application.jar com.example.expenses.application.ExpensesApplication -C target .
    
    // expenses-application.jar 모듈을 로드하도록 지정하고, 메인 모듈 및 실행 클래스를 지정함
    java --module-path expenses-application.jar --module expenses.application/com.example.expenses.application.ExpensesApplication
    
    // 2개의 모듈을 로드하도록 지정하고 메인 모듈 및 실행 클래스 지정
    java --module-path ./expenses.application/target/expenses.application-1.0.jar:./expenses.readers/target/expenses.readers-1.0.jar:./expenses.readers/target/dependency/httpclient-4.5.3.jar --module expenses.application/com.example.expenses.application.ExpensesApplication
    ```
    

- 모듈명 규칙
    - 모듈명은 작성자의 도메인 명을 역순 뒤집은것과 동일하게 시작해야 합니다.
    
- 자동 모듈
    
    외부 라이브러리를 사용하기 위해서는 pom.xml(메이븐), build.gradle(그래들)파일에 의존성을 외부에서 주입해주어야 합니다.
    
<br><br>

## ✅ 14.8 모듈 정의와 구문들

### requires

- 컴파일 타임 및 런타임에 한 모듈이 다른 모듈에 의존함을 정의함
- **requires는 모듈명을 인수로 받음**

```java
module controller {
	requires service;
}
```

<br>

### exports

- 현재 모듈의 지정한 패키지를 다른 모듈에서 이용할 수 있도록 public하게 만듭니다.
- 지정하지 않는다면 아무 패키지도 공개하지 않습니다.

```java
module service {
	exports com.example.service;
}
```

<br>

### requires transitive

- A모듈이 B모듈을 의존할때(requires) requires transitive로 변경하면 A모듈을 의존하는 다른 모듈들 (C, D, F)에서 B모듈도 읽을 수 있게 됨 (전이된다)

```java
module ui {
	requires transitive service;
}

module controller {
	requires ui // service 모듈도 사용 가능
}
```

<br>

### exports to

- 특정 모듈의 접근 권한을 다른 부분으로 제한할 수 있음

```java
module service {
	exports com.example.service to service;
}
```

<br>

### open과 opens

- open을 이용하면 모든 패키지를 다른 모듈에서 리플렉션을 이용해 접근할 수 있도록 허용(`reflective access`)합니다. (접근제어에는 영향 X)
- opens는 특정 패키지만 리플렉션을 허용할 경우에 사용합니다.

```java
open module my.module {

}

module my.module {
  opens com.my.package;
}
```

- **relective access operation has occurred 오류**
    
    모듈의 특성을 보면서 open, opens를 보고 과거 우테코에서 마주쳤던 에러 메시지가 생각났습니다.
    
     reflective access operation has occurred 메시지는 결국 Java 9에서 추가된 모듈이 리플렉션 접근을 기본적으로 허용하지 않기 때문에 발생했던 경고 메시지임을 알 수 있습니다.
    
    ```java
    WARNING: An illegal reflective access operation has occurred
    WARNING: Illegal reflective access by org.apache.poi.util.DocumentHelper (file:/C:/Users/User/.m2/repository/org/apache/poi/poi-ooxml/3.17/poi-ooxml-3.17.jar) to method com.sun.org.apache.xerces.internal.util.SecurityManager.setEntityExpansionLimit(int)
    WARNING: Please consider reporting this to the maintainers of org.apache.poi.util.DocumentHelper
    WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
    WARNING: All illegal access operations will be denied in a future release
    ```
