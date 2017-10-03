package uk.co.ticklethepanda.health.weight.dtos.analysis;

import java.time.LocalDate;

public class WeightAnalysisForDateDto {

    private final LocalDate date;
    private final Double weight;

    /**
     * @param date  The date.
     * @param weight The average weight for the day.
     */
    public WeightAnalysisForDateDto(LocalDate date, Double weight) {
        this.date = date;
        this.weight = weight;
    }

    public Double getWeight() {
        return weight;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WeightAnalysisForDateDto that = (WeightAnalysisForDateDto) o;

        if (weight != null ? !weight.equals(that.weight) : that.weight != null) return false;
        return date != null ? date.equals(that.date) : that.date == null;

    }

    @Override
    public int hashCode() {
        int result = weight != null ? weight.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WeightAnalysisForDateDto{" +
                "weight=" + weight +
                ", date=" + date +
                '}';
    }
}
