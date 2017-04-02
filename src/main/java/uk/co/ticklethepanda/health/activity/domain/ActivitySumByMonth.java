package uk.co.ticklethepanda.health.activity.domain;

import java.time.Month;

public class ActivitySumByMonth extends TemporalEntry<Month, Double> {

    public ActivitySumByMonth(Month month, double sum) {
        super(month, sum);
    }

    public ActivitySumByMonth(int month, double sum) {
        this(Month.of(month), sum);
    }

}
