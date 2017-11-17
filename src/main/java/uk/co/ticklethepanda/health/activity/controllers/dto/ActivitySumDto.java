package uk.co.ticklethepanda.health.activity.controllers.dto;

import java.time.LocalDate;

/**
 * Created by panda on 05/07/2017.
 */
public class ActivitySumDto {

    private final double sum;
    private final LocalDate since;

    public ActivitySumDto(double sum, LocalDate since) {
        this.sum = sum;
        this.since = since;
    }

    public double getSum() {
        return sum;
    }

    public LocalDate getSince() {
        return since;
    }
}
