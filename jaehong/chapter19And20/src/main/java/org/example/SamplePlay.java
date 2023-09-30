package org.example;

import java.util.function.Function;

public class SamplePlay {

    public static void main(String... args) {

        addCurry().apply(1).apply(2); // 3

        createUser()
                .apply(new Name())
                .apply(new Password());
    }

    public static Function<Integer, Function<Integer, Integer>> addCurry() {
        return a -> b -> a + b;
    }

    public static Function<Name, Function<Password, User>> createUser() {
        return name -> password -> new User(name, password);
    }

    public static class User {
        private final Name name;
        private final Password password;

        public User(Name name, Password password) {
            this.name = name;
            this.password = password;
        }
    }

    public static class Name {

    }

    public static class Password {

    }

}
