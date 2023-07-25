package org.example.Chapter5;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Example {

    public static void main(String...args) {
    /*    var vegetarianMenu = Dish.menu.stream()
                .filter(Dish::isVegetarian)
                .collect(Collectors.toList());


        Stream.of(1,2,1,3,3,2,4)
                .filter(i -> i % 2 == 0)
                .distinct()
                .forEach(System.out::println);*/

        /*tream.of(1,2,3,4,5,6,1,2,3,4,5,6)
                .distinct()
                .collect(Collectors.toList());

        var result1 = Dish.menu.stream()
                .sorted(Comparator.comparingInt(Dish::getCalories))
                .takeWhile(dish -> dish.getCalories() < 320)
                .map(Dish::toString)
                .collect(Collectors.joining(","));

        var result2 = Dish.menu.stream()
                .sorted(Comparator.comparingInt(Dish::getCalories))
                .dropWhile(dish -> dish.getCalories() < 320)
                .map(Dish::toString)
                .collect(Collectors.joining(","));

        System.out.println(result1);
        System.out.println(result2);

        IntStream.range(1,10).forEach(d ->{
            System.out.println("=====");
        var result = List.of(8,9,1,2,3,4,5)
                .parallelStream()
                .map(i -> {
                    System.out.println(i);
                    return i;
                })
                .limit(5)
                .collect(Collectors.toList());

        System.out.println(result);
            System.out.println("=====");

        });

        Optional.of(1)
                .map(i -> i + 2);
        Optional.of(1)
                .flatMap(i -> Optional.of(i + 1))
                .flatMap(i -> Optional.of(i + 2))
                .flatMap(i -> Optional.empty());


        // 매칭
        Dish.menu.stream()
                .anyMatch(dish -> dish.isVegetarian());


        List.of(1,2,3,4,5)
                .parallelStream()
                .dropWhile(i -> i < 2)
                .forEach(System.out::println);

        System.out.println("====== findAny findFirst example");

        Stream.of(1,2,3,4,5)
                .filter(i -> i % 2 == 0)
                .findFirst()
                .ifPresent(System.out::println);
        IntStream.rangeClosed(1, 10).forEach((a) -> {
        IntStream.range(1, 100)
                .parallel()
                .filter(i -> i % 2 == 0)
                .findAny()
                .ifPresent(System.out::println);
        });

*/
/*
        System.out.println("========= 리듀싱 연산");

        Optional.of(List.of(1,2,3,4)
                .parallelStream().reduce(1, (pre,cur) -> pre + cur))
                .ifPresent(System.out::println);

*/
        Optional.of(List.of(1,2,3,4,5).stream()
                        .reduce(Integer::min))
                .ifPresent(System.out::println);

        System.out.println("========= reduce 병렬처리 문제점");

        Stream.iterate(1, i -> i + 1)
                .limit(10)
                .map(i -> Stream.of("Hello", " ", "World", " ", "Java").parallel()
                        .reduce("", (acc, cur) -> acc + cur)).forEach(System.out::println);

        System.out.println("========= 숫자형 스트림");
        Optional.of(Dish.menu.stream()
                .mapToInt(Dish::getCalories)
                .sum()).ifPresent(System.out::println);

        List.of(1,2,3,4,5).stream()
                .forEach(System.out::println);

        Stream.of(1,2,3,4,5)
                .forEach(System.out::println);

        System.out.println("====== Stream null이 될 수 있는 경우");

        var keys = Stream.of("config", "home", "user")
                .flatMap(key -> Stream.ofNullable(System.getProperty(key)));

        System.out.println("===== 배열로 스트림 만들기");
        var numbers = new int[] {1,2,3,4,5};
        Arrays.stream(numbers).forEach(System.out::println);

        System.out.println("===== 무한 스트림");
        Stream.iterate(0, n -> n + 2)
                .limit(10)
                .forEach(System.out::println);


        Stream.iterate(1, n -> n + 1)
                .limit(100)
                .map(i -> new Member("테스트"));
    }
}

class Member{
    String name;

    Member(String name){
        this.name = name;
    }
}
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

    public enum Type {
        MEAT,
        FISH,
        OTHER
    }

    @Override
    public String toString() {
        return name;
    }

    public static final List<Dish> menu = Arrays.asList(
            new Dish("pork", false, 800, Type.MEAT),
            new Dish("beef", false, 700, Type.MEAT),
            new Dish("chicken", false, 400, Type.MEAT),
            new Dish("french fries", true, 530, Type.OTHER),
            new Dish("rice", true, 350, Type.OTHER),
            new Dish("season fruit", true, 120, Type.OTHER),
            new Dish("pizza", true, 550, Type.OTHER),
            new Dish("prawns", false, 400, Type.FISH),
            new Dish("salmon", false, 450, Type.FISH)
    );

}

