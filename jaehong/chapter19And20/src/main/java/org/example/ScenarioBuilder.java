package org.example;

import java.util.ArrayDeque;
import java.util.Queue;

public class ScenarioBuilder {

    private final Queue<Scenario> scenarios = new ArrayDeque<>();

    public static ScenarioBuilder start() {
        return new ScenarioBuilder();
    }


    public void run() {

        while (!scenarios.isEmpty()) {
            var scenario = scenarios.poll();
            try {
                scenario.run();
            } catch (Exception exception) {
                scenario.errorAction()
                        .ifPresentOrElse(scenarios::offer, () -> {
                        });
            }

        }

    }

    public ScenarioBuilder next(Scenario scenario) {
        scenarios.add(scenario);
        return this;
    }
}
