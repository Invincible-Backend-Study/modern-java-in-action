package org.example;

import java.text.MessageFormat;
import java.util.Random;

public class PreCoursePlayground {

    public static void main(String... args) {

        final var errorScenario = new Scenario(() -> System.out.println("에러 발생"));
        final var scenario1 = new Scenario(() -> System.out.println("1번 시나리오"));
        final var scenario2 = new Scenario(() -> {
            System.out.println("2번 시나리오");
            throw new IllegalArgumentException();
        });
        final var scenario3 = new Scenario(() -> {
            System.out.println("3번 시나리오");

            var random = new Random();
            if (random.nextInt(9) != 8) {
                throw new IllegalArgumentException();
            }
        });

        scenario2.onError(scenario3);
        scenario3.onError(scenario2);

        ScenarioBuilder.start()
                .next(scenario1)
                .next(scenario2)
                .next(scenario3)
                .run();


    }
}
