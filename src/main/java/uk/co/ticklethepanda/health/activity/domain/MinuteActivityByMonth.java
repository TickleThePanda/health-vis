package uk.co.ticklethepanda.health.activity.domain;

import java.time.LocalTime;
import java.time.Month;

public class MinuteActivityByMonth extends TemporalEntry<Month, MinuteActivity> {

    public MinuteActivityByMonth(Month month, MinuteActivity minuteActivity) {
        super(month, minuteActivity);
    }

    public MinuteActivityByMonth(int month, LocalTime time, double averageSteps) {
        this(Month.of(month), new MinuteActivity(time, averageSteps));
    }

}
