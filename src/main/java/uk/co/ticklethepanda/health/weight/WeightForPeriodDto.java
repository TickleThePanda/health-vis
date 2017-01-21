package uk.co.ticklethepanda.health.weight;

import java.time.LocalDate;

public class WeightForPeriodDto {
    public LocalDate localDate;
    public EntryPeriod entryPeriod;
    public Double weightValue;

    public WeightForPeriodDto(LocalDate localDate, EntryPeriod entryPeriod, Double weightValue) {
        this.localDate = localDate;
        this.entryPeriod = entryPeriod;
        this.weightValue = weightValue;
    }
}
