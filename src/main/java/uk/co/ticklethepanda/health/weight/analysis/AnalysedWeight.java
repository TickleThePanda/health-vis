package uk.co.ticklethepanda.health.weight.analysis;

import java.time.LocalDate;

public class AnalysedWeight {

    private final LocalDate date;
    private final double weight;

    public AnalysedWeight(LocalDate date, double weight) {
        this.date = date;
        this.weight = weight;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnalysedWeight that = (AnalysedWeight) o;

        if (Double.compare(that.weight, weight) != 0) return false;
        return date != null ? date.equals(that.date) : that.date == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = date != null ? date.hashCode() : 0;
        temp = Double.doubleToLongBits(weight);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
