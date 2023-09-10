package org.example;


import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.example.Discount.Code;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Chapter16PipelinePlayGround {

    private final static Logger log = LoggerFactory.getLogger(Chapter16PipelinePlayGround.class);

    public static void main(String... args) {
        List<Shop> shops = Arrays.asList(new Shop("BestPrice"), new Shop("LetsSaveBig"), new Shop("MyFavoriteShop"), new Shop("BuyItAll"));

        Function<String, List<String>> findPrices = (String product) -> shops.stream()
                .map(shop -> shop.getPrice(product))
                .map(Quote::parse)
                .map(Discount::applyDiscount)
                .collect(Collectors.toList());

        printRunSpeed(() -> findPrices.apply("any"), "테스트");

        final var executor = Executors.newFixedThreadPool(10);
        Function<String, List<CompletableFuture<String>>> findPricesByCombineAsyncAndSync = (product) -> shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> shop.getPrice(product), executor))
                .map(future -> future.thenApply(Quote::parse))
                .map(future -> future.thenCompose(quote -> CompletableFuture.supplyAsync(
                        () -> Discount.applyDiscount(quote), executor
                )))
                .collect(Collectors.toList());

        printRunSpeed(() -> findPricesByCombineAsyncAndSync.apply("any")
                        .stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()),
                "비동기 동기를 조합한 검색"
        );
        // end::.[]
    }

    private static void printRunSpeed(Supplier<?> supplier, String message) {
        long start = System.nanoTime();
        supplier.get();
        long invocationTime = ((System.nanoTime() - start) / 1_000_000);
        log.info("{} {} mecs", message, invocationTime);
    }


    static class Shop {
        private final String name;

        public Shop(String name) {
            this.name = name;
        }

        public String getPrice(String product) {
            double price = calculatePrice(product);
            Discount.Code code = Discount.Code.values()[new Random().nextInt(Code.values().length - 1)];
            return String.format("%s:%.2f:%s", name, price, code);
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
}

class Discount {

    public static String applyDiscount(Quote quote) {
        return quote.shopName + "price is " + Discount.apply(quote.price, quote.code);
    }

    private static double apply(Double price, Code code) {
        delay();
        return price * (100 - code.percentage) / 100;
    }

    private static void delay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public enum Code {
        NONE(0), SILVER(5), GOLD(10), PLATINUM(15), DIAMOND(20);

        private final int percentage;

        Code(int percentage) {
            this.percentage = percentage;
        }
    }
}

class Quote {
    final String shopName;
    final Double price;
    final Discount.Code code;

    public Quote(String shopName, Double price, Code code) {
        this.shopName = shopName;
        this.price = price;
        this.code = code;
    }

    public static Quote parse(String s) {
        final var split = s.split(":");
        return new Quote(
                split[0],
                Double.parseDouble(split[1]),
                Discount.Code.valueOf(split[2])
        );
    }

}

