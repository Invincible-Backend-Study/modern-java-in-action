package org.example.expredicate;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Cars {
    private List<Car> cars;

    public boolean refereeWinner(){
        return new Car().isArrive((car) -> car.getPosition() == 5);
    }

    public static class Car{
        int position;

        public int getPosition() {
            return position;
        }

        public boolean isArrive(Predicate<Car> predicate){
            return predicate.test(this);
        }
    }
}
