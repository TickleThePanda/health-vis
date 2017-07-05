package uk.co.ticklethepanda.health.weight.dtos;

import java.time.LocalDate;

public class WeightForDayDto {

    public LocalDate date;
    public Double weightAm;
    public Double weightPm;

    public WeightForDayDto(LocalDate date, Double weightAm, Double weightPm) {
        this.date = date;
        this.weightAm = weightAm;
        this.weightPm = weightPm;
    }
}
