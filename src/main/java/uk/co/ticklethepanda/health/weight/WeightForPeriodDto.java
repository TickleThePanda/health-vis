package uk.co.ticklethepanda.health.weight;

import java.time.LocalDate;

public class WeightForPeriodDto {
    public LocalDate localDate;
    public EntryPeriod entryPeriod;
    public Double weight;

    public WeightForPeriodDto(LocalDate localDate, EntryPeriod entryPeriod, Double weight) {
        this.localDate = localDate;
        this.entryPeriod = entryPeriod;
        this.weight = weight;
    }
}
