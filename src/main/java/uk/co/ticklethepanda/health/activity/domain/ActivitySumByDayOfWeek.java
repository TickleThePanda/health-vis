package uk.co.ticklethepanda.health.activity.domain;

import java.time.DayOfWeek;

public class ActivitySumByDayOfWeek extends TemporalEntry<DayOfWeek, Double> {

    public ActivitySumByDayOfWeek(DayOfWeek week, double sum) {
        super(week, sum);
    }

    public ActivitySumByDayOfWeek(int week, double sum) {
        this(DayOfWeek.of(week), sum);
    }

}
