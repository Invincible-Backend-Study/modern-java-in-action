package org.example;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MethodReference{
    public static void main(String...args){
        var list = IntStream.range(1,10).mapToObj(String::valueOf).collect(Collectors.toList());

        list.stream().map(i -> {
            System.out.println("=" + i);
            return i;
        }).filter(i -> {
            System.out.println("==" + i);
            return true;
        }).collect(Collectors.toList());

        System.out.println("============");
        list.parallelStream().map(i -> {
            System.out.println("===" + i);
            return i;
        }).filter(i -> {
            System.out.println("====" + i);
            return true;
        }).collect(Collectors.toList());
    }
}
