package org.example.expredicate;

import com.sun.source.tree.LambdaExpressionTree;
import java.util.Random;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Car {
    private final int position;

    public Car(int position) {
        this.position = position;
    }
    public static void main(String...args){
        var randomMoveSupplier = new RandomMoveSupplier();
        var carMove = new CarMovePredicate();
        new Car(0);
    }

    private Car move(IntPredicate predicate, IntSupplier sup){
        if(predicate.test(sup.getAsInt())){
            return new Car(position + 1);
        }
        return this;
    }
}

class RandomMoveSupplier implements IntSupplier {
    @Override
    public int getAsInt() {
        return new Random().nextInt();
    }
}

class CarMovePredicate implements Predicate<Integer> , IntSupplier{
    @Override
    public int getAsInt() {
        return new Random().nextInt(9);
    }

    @Override
    public boolean test(Integer integer) {
        return false;
    }
}
