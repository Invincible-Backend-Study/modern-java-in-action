package org.example.example;

import static java.util.Comparator.comparingInt;
import static org.example.example.Dish.menu;
import static org.example.example.NestedFunctionOrderBuilder.at;
import static org.example.example.NestedFunctionOrderBuilder.buy;
import static org.example.example.NestedFunctionOrderBuilder.on;
import static org.example.example.NestedFunctionOrderBuilder.order;
import static org.example.example.NestedFunctionOrderBuilder.sell;
import static org.example.example.NestedFunctionOrderBuilder.stock;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;

@FunctionalInterface
interface ValidationStrategy {
    boolean execute(String s);
}

interface Observer {
    void notify(String tweet);
}

interface Subject {
    void registerObserver(Observer o);

    void notifyObservers(String tweet);
}

public class PlayGround {

    public static void main(String... args) throws Exception {

        // 메서드 참조를 이용해서 가독성 높히기
        int totalCalories = menu.stream().map(Dish::getCalories)
                .reduce(0, Integer::sum);

        var numericValidator = new Validator(new IsNumeric());
        numericValidator.validate("aaaa");

        new Validator((s) -> s.matches("\\d+"));
        numericValidator.validate("aaaa");

        Collections.sort(menu, new Comparator<Dish>() {
            @Override
            public int compare(Dish o1, Dish o2) {
                return o1.getCalories() - o2.getCalories();
            }
        });

        Collections.sort(menu, (d1, d2) -> d1.getCalories() - d2.getCalories());
        Collections.sort(menu, comparingInt(Dish::getCalories));

        var errors = new ArrayList<String>();
        var errorCount = 0;
        var br = new BufferedReader(new FileReader("fileName"));
        var line = br.readLine();

        while (errorCount < 40 && line != null) {
            if (line.startsWith("ERROR")) {
                errors.add(line);
                errorCount++;
            }
            line = br.readLine();
        }

        Order order = new Order();
        order.setCustomer("BigBank");

        Trade trade1 = new Trade();
        trade1.setType(Trade.Type.BUY);

        Stock stock1 = new Stock();
        stock1.setSymbol("IBM");
        stock1.setMarket("NYSE");

        trade1.setStock(stock1);
        trade1.setPrice(125.00);
        trade1.setQuantity(80);
        order.addTrade(trade1);

        Trade trade2 = new Trade();
        trade2.setType(Trade.Type.BUY);

        Stock stock2 = new Stock();
        stock2.setSymbol("GOOGLE");
        stock2.setMarket("NASDAQ");

        trade2.setStock(stock2);
        trade2.setPrice(375.00);
        trade2.setQuantity(50);
        order.addTrade(trade2);

        Order order1 = order("BigBank",
                buy(80, stock("IBM", on("NYSE")), at(125.00)),
                sell(50, stock("GOOGLE", on("NASDAQ")), at(375.00))
        );
        UnaryOperator<String> headerProcessing = (String text) -> "From Raoul, Mario and Alan: " + text;
        UnaryOperator<String> spellCheckerProcessing = (String text) -> text.replaceAll("labda", "lambda");
        Function<String, String> pipeline = headerProcessing.andThen(spellCheckerProcessing);
        String result = pipeline.apply("Arent labdas really sexy?!!");

    }
}

@lombok.Getter
class Dish {

    public static final List<Dish> menu = Arrays.asList(
            new Dish("pork", false, 800, Type.MEAT),
            new Dish("beef", false, 700, Type.MEAT),
            new Dish("chicken", false, 400, Type.MEAT),
            new Dish("french fries", true, 530, Type.OTHER),
            new Dish("rice", true, 350, Type.OTHER),
            new Dish("season fruit", true, 120, Type.OTHER),
            new Dish("pizza", true, 550, Type.OTHER),
            new Dish("prawns", false, 400, Type.FISH),
            new Dish("salmon", false, 450, Type.FISH)
    );
    private final String name;
    private final boolean vegetarian;
    private final int calories;
    private final Type type;

    public Dish(String name, boolean vegetarian, int calories, Type type) {
        this.name = name;
        this.vegetarian = vegetarian;
        this.calories = calories;
        this.type = type;
    }

    @Override
    public String toString() {
        return name;
    }

    public enum Type {
        MEAT,
        FISH,
        OTHER
    }

}

class IsAllLowerCase implements ValidationStrategy {

    @Override
    public boolean execute(String s) {
        return s.matches("[a-z]+");
    }
}

class IsNumeric implements ValidationStrategy {

    @Override
    public boolean execute(String s) {
        return s.matches("\\d+");
    }
}

class Validator {
    private final ValidationStrategy strategy;

    public Validator(ValidationStrategy strategy) {
        this.strategy = strategy;
    }

    public boolean validate(String s) {
        return strategy.execute(s);
    }
}

abstract class OnlineBanking {
    public void processCustomer(int id, Consumer<Customer> consumer) {
        makeCustomerHappy(new Customer());
        consumer.accept(new Customer());
    }

    abstract void makeCustomerHappy(Customer c);

    private static class Customer {
    }
}

@Getter
@Setter
class Stock {
    private String symbol;
    private String market;
}

@Getter
@Setter
class Trade {
    private Stock stock;
    private int quantity;
    private double price;

    public double getValue() {
        return quantity * price;
    }

    enum Type {BUY, SELL}
}

@Getter
@Setter
class Order {
    private String customer;
    private List<Trade> trades = new ArrayList<>();

    public void addTrade(Trade trade) {
        this.trades.add(trade);
    }

    public double getValue() {
        return 1.0;
    }
}

class MethodChainingOrderBuilder {
    public final Order order = new Order();

    private MethodChainingOrderBuilder(String customer) {
        order.setCustomer(customer);
    }

    public static MethodChainingOrderBuilder forCustomer(String customer) {
        return new MethodChainingOrderBuilder(customer);
    }

    public TradeBuilder buy(int quantity) {
        return new TradeBuilder(this, Trade.Type.BUY, quantity);
    }

    public TradeBuilder sell(int quantity) {
        return new TradeBuilder(this, Trade.Type.SELL, quantity);
    }

    public MethodChainingOrderBuilder addTrade(Trade trade) {
        order.addTrade(trade);
        return this;
    }

    public Order end() {
        return order;
    }
}

class TradeBuilder {
    public final Trade trade = new Trade();
    private final MethodChainingOrderBuilder builder;

    TradeBuilder(MethodChainingOrderBuilder builder, Trade.Type type, int quantity) {
        this.builder = builder;
        trade.setType(type);
        trade.setQuantity(quantity);
    }

    public StockBuilder stock(String symbol) {
        return new StockBuilder(builder, trade, symbol);
    }
}

class StockBuilder {
    private final MethodChainingOrderBuilder builder;
    private final Trade trade;
    private final Stock stock = new Stock();

    public StockBuilder(MethodChainingOrderBuilder builder, Trade trade, String symbol) {
        this.builder = builder;
        this.trade = trade;
        stock.setSymbol(symbol);
    }

    public TradeBuilderWithStock on(String market) {
        stock.setMarket(market);
        trade.setStock(stock);
        return new TradeBuilderWithStock(builder, trade);
    }
}

class TradeBuilderWithStock {
    private final MethodChainingOrderBuilder builder;
    private final Trade trade;

    public TradeBuilderWithStock(MethodChainingOrderBuilder builder, Trade trade) {
        this.builder = builder;
        this.trade = trade;
    }

    public MethodChainingOrderBuilder at(double price) {
        trade.setPrice(price);
        return builder.addTrade(trade);
    }
}

public class NestedFunctionOrderBuilder {
    public static Order order(String customer, Trade... trades) {
        Order order = new Order();
        order.setCustomer(customer);
        Stream.of(trades).forEach(order::addTrade);
        return order;
    }

    public static Trade buy(int quantity, Stock stock, double price) {
        return buildTrade(quantity, stock, price, Trade.Type.BUY);
    }

    public static Trade sell(int quantity, Stock stock, double price) {
        return buildTrade(quantity, stock, price, Trade.Type.SELL);
    }

    private static Trade buildTrade(int quantity, Stock stock, double price, Trade.Type buy) {
        Trade trade = new Trade();
        trade.setQuantity(quantity);
        trade.setType(buy);
        trade.setStock(stock);
        trade.setPrice(price);
        return trade;

    }

    public static double at(double price) {
        return price;
    }

    public static Stock stock(String symbol, String market) {
        Stock stock = new Stock();
        stock.setSymbol(symbol);
        stock.setMarket(market);
        return stock;
    }

    public static String on(String market) {

        return market;
    }
}

class LambdaOrderBuilder {
    private final Order order = new Order();

    public static Order order(Consumer<LambdaOrderBuilder> consumer) {
        LambdaOrderBuilder builder = new LambdaOrderBuilder();
        consumer.accept(builder);
        return builder.order;
    }

    public void forCustomer(String customer) {
        order.setCustomer(customer);
    }

    public void buy(Consumer<TradeBuilder> consumer) {
        trade(consumer, Trade.Type.BUY);
    }

    public void sell(Consumer<TradeBuilder> consumer) {
        trade(consumer, Trade.Type.SELL);
    }

    private void trade(Consumer<TradeBuilder> consumer, Trade.Type type) {
        TradeBuilder builder = new TradeBuilder();
        builder.trade.setType(type);
        consumer.accept(builder);
        order.addTrade(builder.trade);
    }
}

class MixedBuilder {
    public static Order forCustomer(String customer, TradeBuilder... builders) {
        Order order = new Order();
        order.setCustomer(customer);
        Stream.of(builders).forEach(b -> order.addTrade(b.trade));
        return order;
    }

    public static TradeBuilder buy(Consumer<TradeBuilder> consumer) {
        return buildTrade(consumer, Trade.Type.BUY);
    }

    public static TradeBuilder sell(Consumer<TradeBuilder> consumer) {
        return buildTrade(consumer, Trade.Type.SELL);
    }

    private static TradeBuilder buildTrade(Consumer<TradeBuilder> consumer, Trade.Type buy) {
        TradeBuilder builder = new TradeBuilder();
        builder.trade.setType(buy);
        consumer.accept(builder);

        Order order = forCustomer("BigBank",
                buy(t -> t.quantity(80)
                        .stock("IBM")
                        .on("NYSE")
                        .at(125.00)),
                sell(t -> t.quantity(50).stock("GOOGLE").on("NASDAQ").at(125.00)));

        return builder;
    }
}

class Tax {
    public static double regional(double value) {
        return value * 1;
    }

    public static double general(double value) {
        return value * 1.3;
    }

    public static double surcharge(double value) {
        return value * 1.05;
    }

    public static double calculate(Order order, boolean useRegional, boolean useGeneral, boolean useSurcharge) {
        var value = order.getValue();
        if (useRegional) {
            return Tax.regional(value);
        }
        if (useGeneral) {
            return Tax.general(value);
        }
        if (useSurcharge) {
            return Tax.surcharge(value);
        }
        return value;
    }
}

class TaxCalculator {
    private boolean useRegional;
    private boolean useGeneral;
    private boolean useSurcharge;

    public TaxCalculator withTaxRegional() {
        useRegional = true;
        return this;
    }

    public TaxCalculator withTaxGeneral() {
        useGeneral = true;
        return this;
    }

    public TaxCalculator withTaxSurcharge() {
        useSurcharge = true;
        return this;
    }

    public double calculate(Order order) {
        return Tax.calculate(order, useRegional, useGeneral, useSurcharge);
    }
}

class myTimes implements Observer {

    @Override
    public void notify(String tweet) {
        if (tweet != null & tweet.contains("money")) {
            System.out.println("Breaking news in NY!" + tweet);
        }

    }
}

class Guardian implements Observer {
    public void notify(String tweet) {
        if (tweet != null && tweet.contains("queen")) {
            System.out.println("Yet more news from London... " + tweet);
        }
    }
}

class LeMonde implements Observer {
    public void notify(String tweet) {
        if (tweet != null && tweet.contains("wine")) {
            System.out.println("Today cheese, wine and news! " + tweet);
        }
    }
}

class Feed implements Subject {
    private final List<Observer> observers = new ArrayList<>();

    public void registerObserver(Observer o) {
        this.observers.add(o);
    }

    public void notifyObservers(String tweet) {
        observers.forEach(o -> o.notify(tweet));
    }
}