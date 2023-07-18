package org.example.exstream;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.example.expredicate.Cars.Car;

public class Cars {
    private List<Car> cars;

    public List<Car> refereeWinner1(){
        // 컨테이너를 활용함
        var container = new ArrayList<Car>();
        for(var car: cars){
            if(car.getPosition() == 5){
                container.add(car);
            }
        }
        return container;
    }
    public List<Car> refereeWinner2(){
        Predicate<Car> winnerCase = (car) -> car.getPosition() == 5;
        return cars.stream()
                .map((car) -> car).filter(winnerCase)
                .collect(Collectors.toList());
    }

    static class Car{
        int position;

        public int getPosition() {
            return position;
        }

        public boolean isArrive(Predicate<Car> predicate){
            return predicate.test(this);
        }
    }
}