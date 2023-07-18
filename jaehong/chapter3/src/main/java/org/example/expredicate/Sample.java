package org.example.expredicate;

import java.util.function.IntPredicate;

public class Sample {
    public static void main(String...args) throws Exception{

        //new Sample().a( (b) -> true);
        new Sample().a((FakePredicate) (b) -> true);
        new Sample().a((FakePredicate1) (b) -> true);
    }
    public void a(FakePredicate fakePredicate){
        fakePredicate.test(1);
    }
    public void a(FakePredicate1 fakePredicate){
        fakePredicate.test(1);
    }
}

@FunctionalInterface
interface FakePredicate {
    boolean test(int value);
}

@FunctionalInterface
interface FakePredicate1{
    boolean test(int value);
}