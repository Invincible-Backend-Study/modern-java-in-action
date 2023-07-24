package org.example.chapter6;

import static java.util.Arrays.asList;
import static java.util.stream.Collector.Characteristics.*;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.partitioningBy;
import static org.example.chapter6.Example.isPrime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Example {

    public static void main(String...args) {
        System.out.println("======= Collect max by");
        Dish.menu.stream()
                .collect(
                        Collectors.maxBy(Comparator.comparingInt(Dish::getCalories))
                );

        Dish.menu.stream()
                .collect(
                        Collectors.minBy(Comparator.comparingInt(Dish::getCalories))
                );

        Optional.of(Dish.menu.stream()
                .max(Comparator.comparingInt(Dish::getCalories)))
                .ifPresent(System.out::println);


        System.out.println("====== summary operation");

        Dish.menu.stream().collect(Collectors.summingInt(Dish::getCalories));

        Dish.menu.stream().mapToInt(Dish::getCalories).sum();

        System.out.println("====== string join");

        Optional.of(Dish.menu.stream()
                .map(Dish::getName)
                .collect(Collectors.joining(",")))
                .ifPresent(System.out::println);

        System.out.println("======= 그룹화");

        var d = Dish.menu.stream()
                .collect(groupingBy(Dish::getType));

        Dish.menu.stream()
                .collect(groupingBy(Dish::getType,
                        Collectors.filtering(
                                dish -> dish.getCalories() > 500, Collectors.toList())));

        System.out.println("======= 다수준 그룹화");

        Dish.menu.stream().collect(
                groupingBy(dish -> {
                    if(dish.getCalories() <= 400) {
                        return CaloricLevel.DIET;
                    }
                    else if(dish.getCalories() <= 700){
                        return CaloricLevel.NORMAL;
                    }
                    return CaloricLevel.FAT;
                })
        );

        System.out.println("======== 서브그룹으로 데이터 수집");
        Optional.of(Dish.menu.stream()
                        .collect(groupingBy(Dish::getType, counting())))
                .ifPresent(System.out::println);


        System.out.println("========= 분할");
        var a = Dish.menu.stream().collect(partitioningBy(Dish::isVegetarian));

        System.out.println("========= 숫자를 소수와 비소수로 분할하기");

        // n이하의 수 제한
        Predicate<Integer> isPrime = (candidate) -> IntStream.range(2, candidate)
                .noneMatch(i -> candidate % i == 0);

        // 제곱근 이하의 수로 제한
        Predicate<Integer> isPrime2 = (candidate) -> IntStream.range(2, (int) Math.sqrt((double) candidate))
                .noneMatch(i -> candidate % i == 0);

        Function<Integer, Map<Boolean, List<Integer>>> partitionPrime = n  -> IntStream.rangeClosed(2, n).boxed()
                .collect(partitioningBy(isPrime));

        Dish.menu.stream().collect(new ToListCollector<>());


        Dish.menu.stream()
                .collect(
                        ArrayList::new, //발행
                        List::add, // 누적
                        List::addAll // 합계
                );
    }

    static boolean isPrime(List<Integer> primes, int candidate){
        var candidateRoot = (int) Math.sqrt((double) candidate);
        return primes.stream()
                .takeWhile(i -> i <= candidateRoot)
                .noneMatch(i -> candidate % i == 0);
    }

}
enum CaloricLevel { DIET, NORMAL, FAT }

class Dish {

    private final String name;
    private final boolean vegetarian;
    private final int calories;
    private final Type type;

    public Dish(String name, boolean vegetarian, int calories, Type type) {
        this.name = name;
        this.vegetarian = vegetarian;
        this.calories = calories;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    public int getCalories() {
        return calories;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return name;
    }

    public enum Type {
        MEAT,
        FISH,
        OTHER
    }

    public static final List<Dish> menu = asList(
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

    public static final Map<String, List<String>> dishTags = new HashMap<>();
    static {
        dishTags.put("pork", asList("greasy", "salty"));
        dishTags.put("beef", asList("salty", "roasted"));
        dishTags.put("chicken", asList("fried", "crisp"));
        dishTags.put("french fries", asList("greasy", "fried"));
        dishTags.put("rice", asList("light", "natural"));
        dishTags.put("season fruit", asList("fresh", "natural"));
        dishTags.put("pizza", asList("tasty", "salty"));
        dishTags.put("prawns", asList("tasty", "roasted"));
        dishTags.put("salmon", asList("delicious", "fresh"));
    }

}

class ToListCollector<T> implements Collector<T, List<T>, List<T>>{
    @Override
    public Supplier<List<T>> supplier() {
        return ArrayList::new; // 수집 연산의 시작점
    }
    @Override
    public BiConsumer<List<T>, T> accumulator() {
        // reduce 에서 acc 라는 명칭을 사용하는데 해당 약자임
        return List::add; // 탐색한 항목을 누적하고 바로 누적자를 고친다
    }
    @Override
    public BinaryOperator<List<T>> combiner() {
        return(list1, list2) ->{
            list1.addAll(list2);
            return list1;
        };
    }
    @Override
    public Function<List<T>, List<T>> finisher() {
        return Function.identity();

    }
    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(
                IDENTITY_FINISH, CONCURRENT
        ));
    }
}

class PrimeNumbersCollector implements
        Collector<Integer,
                Map<Boolean, List<Integer>>, // 누적자 acc 형식
                Map<Boolean, List<Integer>>>  // 수집 연산의 결과 형식
{

    @Override
    public Supplier<Map<Boolean, List<Integer>>> supplier() {
        return () -> new HashMap<>() {{
            put(true, new ArrayList<>());
            put(false, new ArrayList<>());
        }};
    }

    @Override
    public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
        return (acc, candidate) ->{
            acc.get(isPrime(acc.get(true), candidate))
                    .add(candidate);
        };
    }

    @Override
    public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
        return (map1, map2) ->{
            map1.get(true).addAll(map2.get(true));
            map1.get(false).addAll(map2.get(false));
            return map1;
        };
    }

    @Override
    public Function<Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(IDENTITY_FINISH));
    }
}

