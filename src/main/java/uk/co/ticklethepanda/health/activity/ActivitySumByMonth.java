package uk.co.ticklethepanda.health.activity;

import java.time.Month;

public class ActivitySumByMonth implements ActivitySumFacet<Month> {

    private final Month month;
    private final double sum;

    public ActivitySumByMonth(Month month, double sum) {
        this.month = month;
        this.sum = sum;
    }

    public ActivitySumByMonth(int month, double sum) {
        this(Month.of(month), sum);
    }

    @Override
    public Month getFacet() {
        return month;
    }

    @Override
    public double getSum() {
        return sum;
    }
}
