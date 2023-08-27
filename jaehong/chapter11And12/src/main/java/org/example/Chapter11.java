package org.example;

import java.util.Optional;
import lombok.Getter;

public class Chapter11 {
    public static void main(String... args) throws Exception {

        Optional.of(1)
                .filter(i -> i % 2 == 0)
                .ifPresent(System.out::println);
        Optional.of(2)
                .filter(i -> i % 2 == 0)
                .ifPresent(System.out::println);
    }


    String getCarInsuranceName(Person person) {
        return person.getCar()
                .flatMap(Car::getInsurance)
                .flatMap(Insurance::getName)
                .orElse("Unknown");
    }


    @Getter
    class Person {
        private Optional<Car> car;

    }

    @Getter
    class Car {
        private Optional<Insurance> insurance;

    }

    @Getter
    class Insurance {
        private final Optional<String> name;

        public Insurance(Optional<String> name) {
            this.name = name;
        }

    }

}