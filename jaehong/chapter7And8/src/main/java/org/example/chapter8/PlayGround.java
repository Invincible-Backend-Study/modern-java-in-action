package org.example.chapter8;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class PlayGround {

    public static void main(String...args) throws Exception{
        title("컬렉션 팩터리 list");

        List<String> friends = new ArrayList<>();
        friends.add("Raphael");
        friends.add("Olivia");
        friends.add("Thibaut");

        var friends1 = Arrays.asList("Raphael","Olivia", "Thibaut");

        friends1.set(0,"Richard");
        //friends1.add("Thaibuat");

        title("리스트 팩토리");

        List.of("Raphael","Olivia", "Thibaut")
                .forEach(System.out::println);

        List<String> friends2 = List.of("Raphael","Olivia", "Thibaut");
//        friends2.add("Chih-Chun");

        title("집합 팩토리");

        Set.of("Raphael","Olivia", "Thibaut")
                .forEach(System.out::println);


        title("맵 팩토리");
        var a = Map.of("Raphael", 30, "Olivia","25");
        var b = Map.of("Raphael", 30, "Olivia",25);

        title("Map removeIf");

        System.out.println(b.getClass());
        System.out.println(a.getClass());

        title("Map sort");

        var favouriteMovies = Map.ofEntries(Map.entry("Raphael","Star Wars"),
                Map.entry("Cristina", "Matrix"),
                Map.entry("Olivia", "James Bond"));

        favouriteMovies.entrySet()
                .stream()
                .sorted(Entry.comparingByKey())
                .forEachOrdered(System.out::println);


    }

    static void title(String title){
        System.out.println("============" + title + "============");
    }
}
