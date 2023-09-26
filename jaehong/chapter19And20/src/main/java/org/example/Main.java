package org.example;

import static org.example.LazyList.primes;

import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

interface MyList<T> {
    T head();

    MyList<T> tail();

    default boolean isEmpty() {
        return true;
    }

    default MyList<T> filter(Predicate<T> p) {
        throw new UnsupportedOperationException();
    }
}

public class Main {
    public static void main(String[] args) {

        Supplier supplier = () -> {
            curriedConverter(9.0 / 5, 32)
                    .applyAsDouble(1000);

            IntFunction<Boolean> isPrime = (candidate) -> IntStream.rangeClosed(2, (int) Math.sqrt(candidate))
                    .noneMatch(i -> candidate % i == 0);

            Function<Integer, Stream<Integer>> primes = (n) -> Stream.iterate(2, i -> i + 1)
                    .filter(isPrime::apply)
                    .limit(n);

            Supplier<IntStream> numbers = () -> IntStream.iterate(2, n -> n + 1);
            Function<IntStream, Integer> head = (intStream -> intStream.findFirst().getAsInt());
            Function<IntStream, IntStream> tail = (intStream -> intStream.skip(1));
            IntStream filtered = tail.apply(numbers.get())
                    .filter(n -> n % head.apply(numbers.get()) != 0);
            return null;
        };

        final var numbers = LazyList.from(2);
        final var two = numbers.head();
        final var three = numbers.tail().head();
        final var four = numbers.tail().tail().head();

        System.out.println(two + " " + three + " " + four);

        System.out.println(
                primes(numbers).head() + " "
                        + primes(numbers).tail().head()
                        + " "
                        + primes(numbers).tail().tail().head()
        );

     
    }

    static boolean isPrime(int candidate) {
        return IntStream.rangeClosed(2, (int) Math.sqrt(candidate))
                .noneMatch(i -> candidate % i == 0);
    }

    static double converter(double x, double f, double b) {
        return x * f + b;
    }

    static DoubleUnaryOperator curriedConverter(double f, double b) {
        return (double x) -> x * f + b;
    }

}

// 단방향 연결리스트로 구현된 여행 구간의 가격 등 상세 정보를 포함하는 int 필드
//
class TrainJourney {
    int price;
    TrainJourney onward;

    public TrainJourney(int price, TrainJourney onward) {
        this.price = price;
        this.onward = onward;
    }

    static TrainJourney link(TrainJourney a, TrainJourney b) {
        if (a == null) {
            return b;
        }
        var t = a;
        while (t.onward != null) {
            t = t.onward;
        }
        t.onward = b;
        return a;
    }

    static TrainJourney append(TrainJourney a, TrainJourney b) {
        return a == null ? b : new TrainJourney(a.price, append(a.onward, b));
    }
}

class Tree {
    final Tree left;
    final Tree right;
    String key;
    int val;

    public Tree(String key, int val, Tree left, Tree right) {
        this.key = key;
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

class TreeProcessor {
    public static int lookup(String k, int defaultval, Tree t) {
        if (t == null) {
            return defaultval;
        }
        if (k.equals(t.key)) {
            return t.val;
        }
        return lookup(k, defaultval, k.compareTo(t.key) < 0 ? t.left : t.right);
    }

    public static void update(String k, int newval, Tree t) {
        if (t == null) {
        } else if (k.equals(t.key)) {
            t.val = newval;
        } else {
            update(k, newval, k.compareTo(t.key) < 0 ? t.left : t.right);
        }
    }

    public static Tree fupdate(String k, int newval, Tree t) {
        if (t == null) {
            return new Tree(k, newval, null, null);
        }
        if (k.equals(t.key)) {
            return new Tree(k, newval, t.left, t.right);
        }
        if (k.compareTo(t.key) < 0) {
            return new Tree(t.key, t.val, fupdate(k, newval, t.left), t.right);
        }
        return new Tree(t.key, t.val, t.left, fupdate(k, newval, t.right));
    }

}

class MyLinkedList<T> implements MyList<T> {
    private final T head;
    private final MyList<T> tail;

    public MyLinkedList(T head, MyList<T> tail) {
        this.head = head;
        this.tail = tail;
    }

    @Override
    public T head() {
        return head;
    }

    @Override
    public MyList<T> tail() {
        return tail;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}

class Empty<T> implements MyList<T> {

    @Override
    public T head() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MyList<T> tail() {
        throw new UnsupportedOperationException();
    }
}

class LazyList<T> implements MyList<T> {
    final T head;
    final Supplier<MyList<T>> tail;

    public LazyList(T head, Supplier<MyList<T>> tail) {
        this.head = head;
        this.tail = tail;
    }

    static LazyList<Integer> from(int n) {
        return new LazyList<>(n, () -> from(n + 1));
    }

    static MyList<Integer> primes(MyList<Integer> numbers) {
        return new LazyList<>(numbers.head(),
                () -> primes(numbers.tail().filter(n -> n % numbers.head() != 0))
        );
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

    @Override
    public MyList<T> filter(Predicate<T> p) {
        if (isEmpty()) {
            return this;
        }
        if (p.test(head())) {
            return new LazyList<>(head(), () -> tail().filter(p));
        }
        return tail().filter(p);
    }
}