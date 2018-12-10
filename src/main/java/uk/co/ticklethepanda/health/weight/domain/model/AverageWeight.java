package uk.co.ticklethepanda.health.weight.domain.model;

import java.time.LocalDate;

public class AverageWeight {

    private final LocalDate periodStart;
    private final LocalDate periodEnd;

    private final double average;
    private final int count;

    public AverageWeight(LocalDate periodStart, LocalDate periodEnd, double average, int count) {

        this.average = average;
        this.count = count;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public double getAverage() {
        return average;
    }

    public int getCount() {
        return count;
    }
}
