package uk.co.ticklethepanda.health.weight;

import java.time.LocalDate;

/**
 *
 */
public class WeightForDayDto {

    public Double weightPm;
    public LocalDate date;
    public Double weightAm;

    public WeightForDayDto(LocalDate date, Double weightAm, Double weightPm) {
        this.date = date;
        this.weightAm = weightAm;
        this.weightPm = weightPm;
    }
}
