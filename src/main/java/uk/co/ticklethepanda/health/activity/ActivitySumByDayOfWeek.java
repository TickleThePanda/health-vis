package uk.co.ticklethepanda.health.activity;

import java.time.DayOfWeek;

public class ActivitySumByDayOfWeek implements ActivitySumFacet<DayOfWeek> {

    private final DayOfWeek week;
    private final double sum;

    public ActivitySumByDayOfWeek(DayOfWeek week, double sum) {
        this.week = week;
        this.sum = sum;
    }

    public ActivitySumByDayOfWeek(int week, double sum) {
        this(DayOfWeek.of(week), sum);
    }

    @Override
    public DayOfWeek getFacet() {
        return week;
    }

    @Override
    public double getSum() {
        return sum;
    }
}
