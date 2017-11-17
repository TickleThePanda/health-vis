package uk.co.ticklethepanda.health.activity.services;

import java.time.LocalTime;

public class MinuteActivity {
    private final LocalTime time;
    private final double steps;

    public MinuteActivity(LocalTime time, double steps) {
        this.time = time;
        this.steps = steps;
    }

    public LocalTime getTime() {
        return time;
    }

    public double getSteps() {
        return steps;
    }

}
