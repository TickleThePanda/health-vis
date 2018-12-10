package uk.co.ticklethepanda.health.weight.dtos.log;

import uk.co.ticklethepanda.health.weight.domain.model.EntryMeridiemPeriod;

import java.time.LocalDate;

public class WeightForMeridiemPeriodDto {
    public LocalDate localDate;
    public EntryMeridiemPeriod entryMeridiemPeriod;
    public Double weight;

    public WeightForMeridiemPeriodDto(LocalDate localDate, EntryMeridiemPeriod entryMeridiemPeriod, Double weight) {
        this.localDate = localDate;
        this.entryMeridiemPeriod = entryMeridiemPeriod;
        this.weight = weight;
    }
}
