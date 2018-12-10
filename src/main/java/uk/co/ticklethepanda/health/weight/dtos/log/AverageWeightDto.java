package uk.co.ticklethepanda.health.weight.dtos.log;

import java.time.LocalDate;

public class AverageWeightDto {
    private final LocalDate start;
    private final LocalDate end;
    private final int count;
    private final double average;

    public AverageWeightDto(LocalDate start, LocalDate end, int count, double average) {
        this.start = start;
        this.end = end;
        this.count = count;
        this.average = average;
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public int getCount() {
        return count;
    }

    public double getAverage() {
        return average;
    }
}
