# 📌 19장: 함수형 프로그래밍 기법

**키워드**: `고차원 함수`, `커링`, `영속 자료구조`, `지연 리스트`, `패턴 매칭`, `참조 투명성을 이용한 캐싱`, `콤비네이터`를 

<br><br>

## ✅ 19.1 함수는 모든 곳에 존재한다

- 함수형 프로그래밍
    - 함수, 메서드가 수학의 함수처럼 부작용 없이 동작함을 의미함
    - 함수를 일반값처럼 인수로 전달, 결과로 받거나, 자료구조에 저장 할 수 있게하는 `일급 함수` 취급
    - Java 8의 메서드 참조를 이용하거나 람다 표현식을 이용해 함수를 일급 객체로 사용할 수 있다.
        - 일급 객체: 변수에 할당 가능, 인자로 전달 가능, 함수의 결과로 리턴 가능

<br>

### 고차원 함수

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/fc04f984-61bc-4cf4-bb67-453a7d29a52c)


고차원 함수는 다음 조건을 만족합니다.

- 하나 이상의 함수를 인자로 받음
- 함수를 결과로 반환함

Comparator.comaring이 고차원 함수의 예시가 됩니다.

<br>

### 부작용과 고차원 함수

부작용: 부정확한 결과나 race condition이 발생하는 상황

- 고차원 함수나 메서드를 구현할 때 어떤 인수가 전달될지 알 수 없으므로 인수로 전달한 함수가 부작용을 포함할 가능성을 염두에 두어야 함
- 인수로 전달도니 함수가 어떤 부작용을 포함하게 될지 정확하게 문서화 하는것이 좋음

<br>

### 커링

- 함수를 모듈화하고 코드를 재사용하는데 도움을 주는 기법
- x와 y라는 두 인수를 받는 함수 f를 한 개의 인수를 받는 g라는 함수로 대체하는 기법
g역시 하나의 인수를 받는 함수를 반환합니다.
    
    $f(x, y) = (g(x))(y)$
    

```java
// 원본 메서드
static double converter(double x, double f, double b) {
		return x * f + b;
}

// 커링 적용
static DoubleUnaryOperator curriedConverter(double f, double b) {
		return (double x) -> x * f + b;
}
```

<br><br>

## ✅ 19.2 영속 자료구조

자료구조를 고칠 수 없는 상황에서 자료구조로 프로그램을 구현하기 위한 자료구조로 DB에서의 영속과는 다른의미 입니다.

- 함수형 메서드는 참조 투명성을 지키기 위해 전역 자료구조나 인수로 받은 자료구조를 갱신할 수 없습니다.

<br>

### 파괴적인 갱신과 함수형

```java
class TrainJourney {
		
		public int price;
		public TrainJourney onward;
		
		public TrainJourney(int p, TrainJourney t) {
		  price = p;
		  onward = t;
		}
}

// 파괴적인 갱신 원본 데이터 a가 회손됨
static TrainJourney link(TrainJourney a, TrainJourney b) {
    if (a == null) {
        return b;
    }
    TrainJourney t = a;
    while (t.onward != null) {
        t = t.onward;
    }
    t.onward = b;
    return a;
}
```

- 위의 코드는 a인자의 끝 칸에 b를 붙여주는 방식으로, a인자 자체가 변화하게 됩니다. 이러한 현상을 파괴적인 갱신이라 말합니다.
    - 위와 같이 주석을 통해 주의사항을 알려줄 수 있지만 남용하게 됐을때는 후에 유지보수하는 프로그래머를 괴롭힙니다.

함수형에서는 메서드를 제한하는 방식으로 문제를 해결합니다.

```java
static TrainJourney append(TrainJourney a, TrainJourney b) {
    return a == null ? b : new TrainJourney(a.price, append(a.onward, b));
}
```

- 위 코드는 외부 자료구조를 변경하지 않고, 새로운 TrainJourney객체를 생성하고 제일 끝 칸에 b를 붙입니다.
- **다만 사용자는 append의 결과를 갱신해서는 안됩니다. (b를 훼손할 수 있으므로)**

아래 그림은 파괴적인 갱신과 비파괴적인 갱신의 코드를 그림으로 나타낸 것입니다.

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/9a9cbd08-bd63-4536-bf8f-53b3b9d1955b)


<br>

### 트리를 사용한 예제

```java
static class Tree {

    private String key;
    private int val;
    private Tree left, right;

    public Tree(String k, int v, Tree l, Tree r) {
        key = k;
        val = v;
        left = l;
        right = r;
    }
}

// 파괴적인 갱신, 기존 트리 t의 구조를 변경하여 새로운 노드를 추가함
public static Tree update(String k, int newval, Tree t) {
    if (t == null) {
        t = new Tree(k, newval, null, null);
    } else if (k.equals(t.key)) {
        t.val = newval;
    } else if (k.compareTo(t.key) < 0) {
        t.left = update(k, newval, t.left);
    } else {
        t.right = update(k, newval, t.right);
    }
    return t;
}

// 함수형 접근법 사용
// 루트 부터 노드를 삽입하는 지점까지 거치는 모든 노드들을 새로 생성하여, 기존 t를 변경하지 않음
public static Tree fupdate(String k, int newval, Tree t) {
    return (t == null) ?
            new Tree(k, newval, null, null) :
            k.equals(t.key) ?
                    new Tree(k, newval, t.left, t.right) :
                    k.compareTo(t.key) < 0 ?
                            new Tree(t.key, t.val, fupdate(k, newval, t.left), t.right) :
                            new Tree(t.key, t.val, t.left, fupdate(k, newval, t.right));
}
```

- fupdate는 기존의 구조를 변경하지 않고 새로운 트리를 만듭니다. → 순수한 함수형
- 이런 함수형 자료구조를 `영속`이라고 하며 영속의 의미는 `저장된 값이 다른 누군가에 의해 영향을 받지 않는 상태를 말합니다.`
- **다만 거치지 않는곳은 기존의 노드를 재사용하므로 프로그래머는 반환 자료구조를 수정하면 안됩니다.**
- Tree의 필드들을 final로 두어 기존 구조를 변화시키지 않겠다고 직접적으로 선언할 수 도 있습니다.

<br><br>

## ✅ 19.3 스트림을 사용하는 지연 평가

스트림은 단 한 번만 소비할 수 있으므로 재귀적으로 정의할 수 없습니다.

이러한 제약으로 발생하는 문제가 있습니다.

<br>

### 자기 정의 스트림

소수를 구하는 알고리즘 중에서 대표적인 에라토스테네스의 체가 있습니다.

- 2를 제외한 모든 2의 배수들은 삭제
- 제거되지 않은 다음숫자인 3의 배수들을 삭제
- 제거되지 않은 다음숫자인 5의 배수들을 삭제

위 과정을 반복하면서 소수를 구할 수 있습니다.

이를 스트림으로 구현하면 다음과 같습니다.

```java
public static void main(String[] args) {
    IntStream range = IntStream.range(1, 101);
    primes(range, 100);
}

static IntStream primes(IntStream numbers, int n) {
    if (n == 0) {
        return IntStream.empty(); // No more primes to find
    }

    int head = head(numbers);
    return IntStream.concat(
            IntStream.of(head),
            primes(tail(numbers).filter(num -> num % head != 0), n - 1)
    );
}

private static IntStream tail(final IntStream numbers) {
    return numbers.skip(1);
}

private static int head(final IntStream numbers) {
    IntStream limit = numbers.limit(1);
    return limit.findFirst().getAsInt();
}
```

- 하지만 head에서 numbers 스트림의 요소를 소비하고, tail에서 소비된 numbers에 대해 연산을 하므로
`stream has already been operated upon or closes`라는 예외 메시지가 발생합니다.
- 또한 두 번째 인수인 primes를 직접 재귀호출하며 무한 재귀에 빠지게 됩니다. 이는 concat의 두번째 인수를 지연 평가하여 해결할 수 있습니다.
    - 즉, 소수를 처리할 필요가 있을때만 스트림을 실제로 평가 합니다.

<br>

### 지연 리스트 만들기

```java
interface MyList<T> {

    T head();

    MyList<T> tail();

    default boolean isEmpty() {
        return true;
    }

    MyList<T> filter(Predicate<T> p);

}

class LazyList<T> implements MyList<T> {

    final T head;
    final Supplier<MyList<T>> tail;

    public LazyList(T head, Supplier<MyList<T>> tail) {
        this.head = head;
        this.tail = tail;
    }

    @Override
    public T head() {
        return head;
    }

    @Override
    public MyList<T> tail() {
        return tail.get();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    *@Override
    public MyList<T> filter(Predicate<T> p) {
        return isEmpty() ? this
                : p.test(head()) ? new LazyList<>(head(), () -> tail().filter(p)) : tail().filter(p);
    }*

}

public static LazyList<Integer> from(int n) {
    return new LazyList<Integer>(n, () -> from(n + 1));
}

// 실제 사용
LazyList<Integer> numbers = from(2);
int two = numbers.head();
int three = numbers.tail().head();
int four = numbers.tail().tail().head();
System.out.println(two + " " + three + " " + four);
// 1, 2, 3
```

- 숫자를 1씩 증가하여 리스트를 재귀적으로 생성하지만, Supplier 덕분에 호출하기 전까지 재귀 동작을 지연시킬 수 있습니다.
- 재귀로 동일 함수를 호출하므로 결국 호출 횟수마다 재귀가 돌게 되어 무한대의 지연 리스트를 생성할 수 있습니다.

지연 리스트를 이용해 소수를 생성하면 다음과 같습니다.

```java
public static MyList<Integer> primes(MyList<Integer> numbers) {
    return new LazyList<>(numbers.head(), () -> primes(numbers.tail().filter(n -> n % numbers.head() != 0)));
}

numbers = from(2);
int prime_two = primes(numbers).head();
int prime_three = primes(numbers).head();
int prime_five = primes(numbers).tail().tail().head();
System.out.println(prime_two + " " + prime_three + " " + prime_five);
// 2, 3, 5 출력
```

- primes가 반환하는 LazyList 객체의 tail을 primes메서드로 감싸져 있으며 내부적으로 filtering을 통해 배수는 거르는 에라토스테네스의 체 알고리즘을 구현합니다.
- filter에선 Predicate가 참이라면 지연 리스트를 생성하여 반환하지만, 거짓이라면 해당 숫자는 소수가 아니므로 즉시 연산으로 숫자를 증가시키고 재귀를 돌아 배수가 아닌 숫자가 나올때까지 필터링 합니다.

하지만 위의 구조는 몇가지 문제가 있습니다.

- 자료구조의 10% 미만의 데이터를 활용하는 상황에서 지연 실행에 의한 오버헤드가 더 크다.
- LazyList값이 진짜로 게으르지 않다 → LazyList값을 tail()을 이용해 탐색할때 각 노드들이 두 개씩 생성됨
    - 처음 tail() 호출만 Supplier가 호출되도록하고 결과값을 캐시하여 문제를 해결할 수 있습니다.

<br><br>

## ✅ 19.4 패턴 매칭

수학은 다음과 같은 정의를 할 수 있습니다.

$f(0) = 1$, $f(n) = n * f(n - 1)$

이를 자바로 구현하기 위해선 if-then-else 혹은 switch를 통해 구현해야 하는데 이는 순수한 로직을 제외한 코드의 양이 많아지게 됩니다.

자바는 패턴 매칭 기능이 없지만 비슷하게 구현하여 불필요한 코드들을 줄일 수 있습니다.

<br>

### 방문자 디자인 패턴

visitor design pattern은 자료형을 unwrap할 수 있는 패턴입니다. 

이는 특정 데이터 형식을 방문하는 알고리즘을 캡슐화 하는 클래스를 따로 만들 수 있습니다.

```java
class BinOp extends Expr {

		public Expr accept(SimplifyExprVisitor v) {
				return v.visit(this);
		}
}

class SimplifyExprVisitor {
		
		public Expr visit(BinOp e) {
				if ("+".equals(e.opname) && e.right instanceof Number && ...) {
							return e.left;
				}
		}
		return e;
}
```

위와 같이 BinOp의 accept의 인수인 SimplifyExprVisitor에게 자기 자신을 넘겨주어 unwrap합니다.

<br>

### 패턴 매칭의 힘

스칼라는 패턴 매칭을 지원하는 언어로 가장 큰 특징은 아주 커다란 switch문, if-then-else문을 피할 수 있다는 사실입니다.

자바 8의 람다를 이용해 패턴 매칭과 비슷한 코드를 만들 수 있습니다.

```java
// 패턴 매칭에 사용되는 클래스들
class Expr {
}

class Number extends Expr {

    int val;

    public Number(int val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return "" + val;
    }

}

class BinOp extends Expr {

    String opname;
    Expr left, right;

    public BinOp(String opname, Expr left, Expr right) {
        this.opname = opname;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "(" + left + " " + opname + " " + right + ")";
    }

}

interface TriFunction<S, T, U, R> {
    R apply(S s, T t, U u);
}
```

```java
public class PatternMatching {

    public static void main(String[] args) {
        Expr e1 = new BinOp("+", new Number(5), new Number(0));
        Expr simplify = simplify(e1);
        System.out.println(simplify);

        Expr e2 = new BinOp("+", new Number(5), new BinOp("*", new Number(3), new Number(4)));
        Integer result = evaluate(e2);
        System.out.println(e2 + " = " + result);
    }

		// 간단한 패턴 매칭
    private static Expr simplify(Expr e) {
        TriFunction<String, Expr, Expr, Expr> binopcase = (opname, left, right) -> {
            if ("+".equals(opname)) {
                if (left instanceof Number && ((Number) left).val == 0) {
                    return right;
                }
                if (right instanceof Number && ((Number) right).val == 0) {
                    return left;
                }
            }
            if ("*".equals(opname)) {
                if (left instanceof Number && ((Number) left).val == 1) {
                    return right;
                }
                if (right instanceof Number && ((Number) right).val == 1) {
                    return left;
                }
            }
            return new BinOp(opname, left, right);
        };
        Function<Integer, Expr> numcase = val -> new Number(val);
        Supplier<Expr> defaultcase = () -> new Number(0);

        return patternMatchExpr(e, binopcase, numcase, defaultcase);
    }

		// 실제 모든 경우 입력에 대하여 덧셈, 곱셈을 정의함
    private static Integer evaluate(Expr e) {
        Function<Integer, Integer> numcase = val -> val;
        Supplier<Integer> defaultcase = () -> 0;
        TriFunction<String, Expr, Expr, Integer> binopcase = (opname, left, right) -> {
            if ("+".equals(opname)) {
                if (left instanceof Number && right instanceof Number) {
                    return ((Number) left).val + ((Number) right).val;
                }
                if (right instanceof Number && left instanceof BinOp) {
                    return ((Number) right).val + evaluate(left);
                }
                if (left instanceof Number && right instanceof BinOp) {
                    return ((Number) left).val + evaluate(right);
                }
                if (left instanceof BinOp && right instanceof BinOp) {
                    return evaluate(left) + evaluate(right);
                }
            }
            if ("*".equals(opname)) {
                if (left instanceof Number && right instanceof Number) {
                    return ((Number) left).val * ((Number) right).val;
                }
                if (right instanceof Number && left instanceof BinOp) {
                    return ((Number) right).val * evaluate(left);
                }
                if (left instanceof Number && right instanceof BinOp) {
                    return ((Number) left).val * evaluate(right);
                }
                if (left instanceof BinOp && right instanceof BinOp) {
                    return evaluate(left) * evaluate(right);
                }
            }
            return defaultcase.get();
        };

        return patternMatchExpr(e, binopcase, numcase, defaultcase);
    }

    static T patternMatchExpr(Expr e, TriFunction<String, Expr, Expr, T> binopcase, Function<Integer, T> numcase,
                                  Supplier<T> defaultcase) {
        if (e instanceof BinOp) {
            return binopcase.apply(((BinOp) e).opname, ((BinOp) e).left, ((BinOp) e).right);
        } else if (e instanceof Number) {
            return numcase.apply(((Number) e).val);
        } else {
            return defaultcase.get();
        }
    }

}
```

```java
T patternMatchExpr(Expr e, TriFunction<String, Expr, Expr, T> binopcase, Function<Integer, T> numcase, Supplier<T> defaultcase)
```

- `binopcase`: 연산식을 계산하기 위해 정의한 TriFunction 타입
- `numcase`: 단순 숫자가 입력됐을경우 그대로 반환하기 위한 Function 타입
- `defaultcase`: BinOp, Number 타입이 아닌 경우 실행되는 기본값 반환 Supplier 타입

<br><br>

## ✅ 19.5 기타 정보

### 캐싱 또는 기억화

- 참조 투명성이 유지되는 상황에서 특정 구조를 재귀적으로 호출해야 하는 상황에서 새로운 리소스의 생성에 대한 오버헤드를 줄이기 위해 캐싱을 할 수 있습니다.
- 하지만 캐싱을 하기 위해선 함수 밖에 있는 변수에 액세스 해야 하며 공유된 가변 상태에 접근하게 됩니다.
    - 스레드 안전성, 레이스 컨디션 등 발생할 수 있는 문제가 많아짐
- 결국 함수형 프로그래밍의 목적은 동시성과 가변 상태가 만나는 상황을 없애는 것이지만 캐싱과 같은 저수준 성능 문제는 해결할 수 없습니다.

<br>

### 같은 객체를 반환함의 의미

`t2 = fupdate(”Will”, 26, t);`

`t3 = fupdate(”Will”, 26, t);`

위와 같은 코드에서 `t2 != t3`는 false가 됩니다.

인수가 같다면 결과도 같아야 하는 참조 투명성이 위배됐다고 생각할 수 있지만, 실제로는 그렇지 않습니다.

참조 투명성에서 말하는 같은 결과는 ==의 의미가 아니라 구조적인 값이 같음을 의미 합니다. 따라서 fupdate는 참조 투명성을 갖습니다.

<br>

### 콤비네이터

함수형 프로그래밍에서 많이 사용되는 형식은 두 함수를 인수로 받고 다른 함수를 반환하는 고차원 함수를 많이 사용합니다.

Java 8 API에 추가된 많은 기능은 combinator의 영향을 많이 받았습니다.

```java
// 함수 조합 코드들
static <A, B, C> Function<A, C> compose(Function<B, C> g, Function<A, B> f) {
    return x -> g.apply(f.apply(x));
}

static <A> Function<A, A> repeat(int n, Function<A, A> f) {
    return n == 0 ? x -> x : compose(f, repeat(n - 1, f));
}
```

위와 같이 사용하게 되면 반복 과정에서 전달되는 가변 상태 함수형 모델 등 반복 기능을 좀 더 다양하게 활용할 수 있습니다.

<br><br>

## ✅ 19.6 마치며

- 일급 함수: 인수 전달, 결과 반환, 자료구조에 저장될 수 있는 함수
- 고차원 함수: 한 개 이상의 함수를 인수로 받아서 다른 함수를 반환하는 함수
- 커링: 함수를 모듈화 하고 코드를 재사용할 수 있게 지원하는 기법
- 영속 자료구조: 갱신될 때 기존 버전인 자신을 보존함
- 패턴 매칭: 자료형을 언랩하는 함수형 기능
- 콤비네이터: 둘 이상의 함수나 자료구조를 조합하는 함수형 개념

<br><br><br>

# 📌 20장: OOP와 FP의 조화: 자바와 스칼라 비교

- 스칼라: 객체지향 + 함수형 프로그래밍을 혼합한 언어

<br><br>

## ✅ 20.1 스칼라 소개

```scala
object Beer {

  def main(args: Array[String]) {
    imperative()
    functional()
  }

  def imperative() {
    var n: Int = 2
    while (n <= 6) {
      println(s"Hello ${n} bottles of beer")
      n += 1
    }
  }

  def functional() {
    2 to 6 foreach { n => println(s"Hello ${n} bottles of beer") }
  }

}
```

- 비재귀 메서드는 반환 형식을 추론할 수 있으므로 명시적으로 반환형식을 정의하지 않아도 됨
- 위의 `object Beer`로 인해 Beer 클래스 정의 및 싱글턴 인스턴스로 생성합니다. 따라서 object 내부의 메서드는 정적 메서드로 간주합니다.

<br>

### 기본 자료구조: 리스트, 집합, 맵, 튜플, 스트림, 옵션

- 스칼라는 불변 컬렉션 자체를 제공함
- 따라서 기존 컬렉션에 새로운 요소를 추가하면 새로운 컬렉션이 생성되어 반환됨: `영속성 유지`
- `scala.collection.mutable` 패키지에서 가변 버전의 컬렉션도 제공합니다.

**자바의 불변 컬렉션과 스칼라의 불변 컬렉션의 차이**

```java
Set<Integer> numbers = ...;
Set<Integer> newNumbers = Collections.unmodifiableSet(numbers);
```

- 자바도 Collections.unmodifiableSet(numbers); 를 통해 불변 컬렉션을 제공합니다.
하지만 numbers라는 컬렉션에 값을 추가하면 위 메서드로 반환된 컬렉션에도 값이 추가됩니다.
- 이는 해당 메서드로 반환되는 컬렉션이 가변 컬렉션의 wrapper class이기 때문입니다.
- 반면에 스칼라의 불변 컬렉션은 얼마나 많은 변수가 해당 컬렉션을 참조하는지와 관계없이 불변을 유지합니다.

**튜플의 제공**

스칼라는 tuple 자료형을 제공한다는 점에서 자바와 차이점이 있습니다.

**스칼라의 스트림**

자바와 마찬가지로 지연 평가를 하는 스트림을 제공합니다. 하지만 인덱스를 제공하므로 인덱스를 통해 스트림의 요소를 접근할 수 있어 자바보다는 메모리 효율성이 조금 떨어집니다. (캐싱)

**옵션**

스칼라도 null이 존재합니다. 따라서 자바의 Optional과 같은 기능을 제공합니다.

<br><br>

## ✅ 20.2 함수

스칼라의 함수는 어떤 작업을 수행하는 일련의 명령어 그룹을 말합니다.

스칼라는 다음과 같은 기능을 제공합니다.

- `함수 형식`: 자바의 함수 디스크립터의 개념을 표현하는 편의 문법입니다.
- `익명 함수`: 익명 함수는 자바의 람다와 달리 비지역 변수 접근 및 값 변경에 제한이 없습니다.
- `커링 지원`: 커링을 자체적인 문법으로 지원합니다. (여러 인수를 받는 함수를 일부 인수를 받는 여러개의 함수로 분리)

<br>

### 스칼라의 일급 함수

스칼라의 함수는 일급값 입니다.

```scala
object Tweets {

  def isJavaMentioned(tweet: String) : Boolean = tweet.contains("Java")
  def isShortTweet(tweet: String) : Boolean = tweet.length() < 20

  def main(args: Array[String]) {
    val tweets = List(
      "I love the new features in Java 8",
      "How's it going?",
      "An SQL query walks into a bar, sees two tables and says 'Can I join you?'"
    )

		// 위의 predicate를 통해 filtering 가능
    tweets.filter(isJavaMentioned).foreach(println)
    tweets.filter(isShortTweet).foreach(println)

    val isLongTweet : String => Boolean = (tweet : String) => tweet.length() > 60
    tweets.map(isLongTweet).foreach(println)
  }

}
```

filter의 시그니처는 아래와 같습니다. A 인자를 받으면 Boolean을 반환하는것을 문법으로 제공합니다.

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/cf83e5dd-3ecd-4095-adac-a7ebe5899c37)

<br>

### 익명 함수와 클로저

**익명함수**

```scala
// 익명 클래스
val isLongTweet : String => Boolean = new Function1[String, Boolean] {
		def apply(tweet: String): Boolean = tweet.length() > 60
}

// 익명 함수
val isLongTweet : String => Boolean = (tweet : String) => tweet.length() > 60
tweets.map(isLongTweet).foreach(println) // false
```

스칼라는 보통 함수를 호출하듯 apply를 호출할 수 있습니다. 즉 `f(a) → f.apply(a)`와 같은 호출이 됩니다.

```scala
isLongTweet("A very short tweet") // isLongTweet.apply("A very short tweet")과 동일
```

**클로저**

함수의 비지역 변수를 자유롭게 참조할 수 있는 함수의 인스턴스를 말합니다.

```scala
object Closure {

  def main(args: Array[String]) {
    var count = 0
    val inc = () => count += 1
    inc()
    println(count) // 1
    inc()
    println(count) // 2
  }

}
```

반면에 자바는 아래와 같은 코드가 불가능합니다.

```java
public static void main(String[] args) {
		int count = 0;
		Runnalbe inc = () -> count += 1; // 에초에 컴파일 에러
}
```

→ 클로저 기능은 정말 필요할때만 사용하는것이 바람직합니다.

<br>

### 커링

스칼라는 커링을 문법적으로 제공해줍니다.

```scala
object Currying {

  def main(args: Array[String]) {
    def multiply(x : Int, y: Int) = x * y
    val r1 = multiply(2, 10)
    println(r1)
		
		// 1개의 파라미터를 갖는 인수리스트를 2개 가짐
    def multiplyCurry(x :Int)(y : Int) = x * y

		// 커링한 함수에 한번에 값 대입
    val r2 = multiplyCurry(2)(10)
    println(r2) // 20
		

		// 함수를 부분적용하여 커링 함수를 사용함
    val multiplyByTwo : Int => Int = multiplyCurry(2)
    val r3 = multiplyByTwo(10)
    println(r3) // 20
  }

}
```

<br><br>

## ✅ 20.3 클래스와 트레이트

### 스칼라의 클래스

스칼라의 클래스는 getter, setter, 생성자가 암시적으로 생성되므로 코드가 훨씬 단순해집니다.

```scala
class Student(var name: String, var id: Int)

	object Student {
	
	  def main(args: Array[String]) {
		    val s = new Student("Raoul", 1)
		    println(s.name) // Raoul
		    s.id = 1337
		    println(s.id) // 1337
	  }
}
```

<br>

### 스칼라의 트레이트

스칼라의 트레이트는 자바의 인터페이스와 비슷하지만 추가적으로 필드도 가질 수 있습니다. 또한 인터페이스와 같이 다중 상속도 지원합니다.

**트레이트 선언 및 클래스와의 조합**

```scala
trait Sized {
  var size: Int = 0
  def isEmpty() = size == 0
}

object SizedRunner {
  def main(args: Array[String]) {
    class Empty extends Sized

    println(new Empty().isEmpty()) // true
  }
}
```

**인스턴스화 과정에서 트레이트 조합(컴파일시 조합결과가 결정됨)**

```scala
trait Sized {
  var size: Int = 0
  def isEmpty() = size == 0
}

object SizedRunner {

  def main(args: Array[String]) {
    class Box

    val b1 = new Box() with Sized // 인스턴스화시 조합
    println(b1.isEmpty()) // true
  }

}
```

같은 시그니처의 메서드 혹은 같은 이름의 필드를 정의하는 트레이트를 다중 상속하면 자바의 디폴트 메서드에서 문제를 해결한것과 비슷하게 제한을 둡니다.
