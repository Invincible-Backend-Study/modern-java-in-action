package org.example;


import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutorServiceTest {
    private static final Logger log = LoggerFactory.getLogger(ExecutorServiceTest.class);

    public static void main(String... args) {

        final var executors = Executors.newFixedThreadPool(3);

        Consumer<Integer> sleep = millis -> {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        IntStream.iterate(1, i -> i + 1)
                .limit(100)
                .peek(i -> log.info("checked {}", i))
                .forEach(i -> {
                    executors.submit(() -> {
                        sleep.accept(1000);
                        log.info("execute {}", i);
                    });
                });

    }
}
