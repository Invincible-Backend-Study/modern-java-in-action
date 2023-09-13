package org.example;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Chapter16PlayGround {

    private final static Logger log = LoggerFactory.getLogger(Chapter16PlayGround.class);

    public static void main(String... args) {

        ExecutorService executorService = Executors.newFixedThreadPool(Math.min(100, 5), r -> {
            var thread = new Thread(r);
            thread.setDaemon(true);
            return thread;

        });
        Future<Double> future = executorService.submit(Chapter16PlayGround::doLongCompetition);
        doSomethingElse();

        try {
            Double result = future.get(1, TimeUnit.SECONDS);
            System.out.println(result);
        } catch (ExecutionException | TimeoutException | InterruptedException e) {
            log.error("{}", e);
        }

        Shop shop = new Shop("BestShop");
        long start = System.nanoTime();
        Future<Double> futurePrice = shop.getPriceAsync("my Favorite Product");
        long invocationTime = ((System.nanoTime() - start) / 1_000_000);
        log.info("Invocation returned after {} mecs", invocationTime);

        doSomethingElse();

        try {
            double price = futurePrice.get();
            log.info("price is {}", String.format("%.2f", price));
        } catch (ExecutionException | InterruptedException e) {
            //
        }
        long retrievalTime = ((System.nanoTime() - start) / 1_000_000);
        log.info("Price returned after {} mesc", retrievalTime);

        List<Shop> shops = Arrays.asList(new Shop("BestPrice"), new Shop("LetsSaveBig"), new Shop("MyFavoriteShop"), new Shop("BuyItAll"));

        Function<String, List<String>> findProducts = (String product) -> shops.stream()
                .map(lshop -> String.format("%s price is %.2f", lshop.name(), lshop.getPrice(product)))
                .collect(Collectors.toList());

        // 상점 검색하는데 4초정도 걸림 4개의 상점을 둘러봐야하기 때문
        printRunSpeed(() -> findProducts.apply("myPhone"), "일반 상점 검색");

        Function<String, List<String>> findParallelProducts = (String product) -> shops.stream()
                .parallel()
                .map(lshop -> String.format("%s price is %.2f", lshop.name(), lshop.getPrice(product)))
                .collect(Collectors.toList());

        // 개선된 것을 확인할 수 있음
        printRunSpeed(() -> findParallelProducts.apply("myPhone"), "개선된 상점 검색");

        Function<String, List<CompletableFuture<String>>> findAsyncParallelProducts = (String product) -> shops.stream()
                .map(lshop -> CompletableFuture.supplyAsync(
                        () -> String.format("%s price is %.2f", lshop.name(), lshop.getPrice(product)))
                )
                .collect(Collectors.toList());

        // 얘가 왜 더 빠르지??
        printRunSpeed(() -> findAsyncParallelProducts.apply("myPhone")
                .stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList()), "비동기 상점 검색");
    }

    private static void printRunSpeed(Supplier<?> supplier, String message) {
        long start = System.nanoTime();
        supplier.get();
        long invocationTime = ((System.nanoTime() - start) / 1_000_000);
        log.info("{} {} mecs", message, invocationTime);
    }

    private static void doSomethingElse() {
        log.info("do something else");
    }

    private static Double doLongCompetition() {
        log.info("previous do long competition");
        IntStream.iterate(1, i -> i + 1)
                .sum();
        log.info("do long competition");
        return 1.0;
    }

}

class Shop {
    private final String name;

    public Shop(String name) {
        this.name = name;
    }

    public double getPrice(String product) {
        // getPrice가 호출되면 1초동안 블로킹이 된다.
        // 동기 처리되는 메서드
        return calculatePrice(product);
    }

    public Future<Double> getPriceAsync(String product) {
        CompletableFuture<Double> futurePrice = new CompletableFuture<>();
        new Thread(() -> {
            try {
                double price = calculatePrice(product);
                futurePrice.complete(price);
            } catch (Exception e) {
                futurePrice.completeExceptionally(e);
            }
        }).start();
        return futurePrice;
    }

    public Future<Double> getPriceAsyncV2(String product) {
        return CompletableFuture.supplyAsync(() -> calculatePrice(product));
    }

    public double calculatePrice(String product) {
        delay();
        return new Random().nextDouble() * product.charAt(0) + product.charAt(1);
    }

    public void delay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String name() {
        return this.name;
    }
}
