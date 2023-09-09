package org.example;

interface A {
    static void hello() {
        System.out.println("hello");
    }

    default void hello1() {
        System.out.println("hello");
    }
}

interface B extends A {
    default void hell() {
        System.out.println("helloB");
    }
}

interface C extends A {
    default void hell() {
        System.out.println("helloC");
    }
}

public class Main {
    public static void main(String[] args) {

        System.out.println("Hello world!");
    }
}