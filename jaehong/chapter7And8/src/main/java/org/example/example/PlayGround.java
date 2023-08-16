package org.example.example;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class PlayGround {
    public static void main(String... args) {
        var list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        var updatedList = list.stream()
                .map(i -> i + 1)
                .filter(i -> i % 2 == 0)
                .collect(Collectors.toList());
    }
}