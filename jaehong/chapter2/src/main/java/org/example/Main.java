package org.example;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.example.Expression.Apple;
import org.example.Expression.Color;

public class Main {
    public static void main(String[] args) {
    }


}

class Expression {
    public static List<Apple> filterGreenApples(List<Apple> inventory, Color color) {
        List<Apple> result = new ArrayList<>();
        for (var apple : inventory) {
            if (Objects.equals(Color.GREEN, apple.getColor())) {
                result.add(apple);
            }
        }
        return result;
    }

    public static List<Apple> filterRedApples(List<Apple> inventory, Color color) {
        List<Apple> result = new ArrayList<>();
        for (var apple : inventory) {
            if (apple.getColor().equals(color)) {
                result.add(apple);
            }
        }
        return result;
    }

    public static List<Apple> filterApplesByWeight(List<Apple> inventory, int weight) {
        List<Apple> result = new ArrayList<>();
        for (var apple : inventory) {
            if (apple.getWeight() > weight) {
                result.add(apple);
            }
        }
        return result;
    }
    public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p){
        List<Apple> result = new ArrayList<>();
        for(var apple: inventory){
            if(p.test(apple)){
                result.add(apple);
            }
        }
        return result;
    }




    static class Apple {
        private Color color;
        private int weight;

        public Color getColor() {
            return color;
        }

        public int getWeight() {
            return weight;
        }
    }

    enum Color {
        RED, GREEN;
    }
}

interface ApplePredicate {
    boolean test(Apple apple);
}

class AppleHeavyWeightPredicate implements ApplePredicate {

    @Override
    public boolean test(Apple apple) {
        return apple.getWeight() > 150;
    }
}

class AppleGreenColorPredicate implements ApplePredicate {

    @Override
    public boolean test(Apple apple) {
        return Color.GREEN.equals(apple.getColor());
    }
}

interface ApplePrint {
    String getOutput(Apple apple);
}

class AppleWeightPrint implements ApplePrint{
    @Override
    public String getOutput(Apple apple) {
        return apple.getWeight() + " " + (apple.getWeight() > 150 ? "무거움" : "가벼움");
    }
}