package org.example;

import java.util.Optional;

public class Scenario {

    private final ScenarioAction scenarioAction;
    private Scenario errorAction;

    public Scenario(ScenarioAction scenarioAction) {
        this.scenarioAction = scenarioAction;
    }

    public static Scenario build(ScenarioAction supplier) {
        return new Scenario(supplier);
    }

    public Scenario onError(Scenario scenario) {
        this.errorAction = scenario;
        return this;
    }

    public void run() {
        this.scenarioAction.run();
    }

    public Optional<Scenario> errorAction() {
        return Optional.ofNullable(errorAction);
    }

    @FunctionalInterface
    public interface ScenarioAction {
        void run();
    }


}
