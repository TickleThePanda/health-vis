package uk.co.ticklethepanda.health.weight;

import java.time.LocalDate;

/**
 *
 */
public class WeightDto {

    public Double weightPm;
    public LocalDate date;
    public Double weightAm;

    public WeightDto(LocalDate date, Double weightAm, Double weightPm) {
        this.date = date;
        this.weightAm = weightAm;
        this.weightPm = weightPm;
    }
}
