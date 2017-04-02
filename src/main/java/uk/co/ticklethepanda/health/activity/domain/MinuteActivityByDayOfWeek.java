package uk.co.ticklethepanda.health.activity.domain;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class MinuteActivityByDayOfWeek extends TemporalEntry<DayOfWeek, MinuteActivity> {

    public MinuteActivityByDayOfWeek(DayOfWeek dayOfWeek, MinuteActivity minuteActivity) {
        super(dayOfWeek, minuteActivity);
    }

    public MinuteActivityByDayOfWeek(int dayOfWeek, LocalTime time, double averageSteps) {
        this(DayOfWeek.of(dayOfWeek), new MinuteActivity(time, averageSteps));
    }

}
