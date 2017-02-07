package uk.co.ticklethepanda.health.weight;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

public class AveragedWeight {

    private final Double average;
    private final LocalDate date;

    /**
     * @param weight
     * @param diff   The average difference between the afternoon and the morning.
     */
    public AveragedWeight(Weight weight, Double diff) {
        this.date = weight.getDate();
        if (weight.getWeightAm() != null && weight.getWeightPm() != null) {
            this.average = (weight.getWeightAm() + weight.getWeightPm()) / 2.0;
        } else if (weight.getWeightAm() != null && weight.getWeightPm() == null) {
            this.average = (weight.getWeightAm() * 2 + diff) / 2.0;
        } else if (weight.getWeightAm() == null && weight.getWeightPm() != null) {
            this.average = (weight.getWeightPm() * 2 - diff) / 2.0;
        } else {
            this.average = null;
        }
    }

    public static List<AveragedWeight> calculateAverageWeighs(List<Weight> weights) {
        Double diff = weights.stream()
                .filter(w -> w.getWeightPm() != null && w.getWeightAm() != null)
                .mapToDouble(w -> w.getWeightPm() - w.getWeightAm())
                .average()
                .getAsDouble();

        return weights.stream()
                .map(w -> new AveragedWeight(w, diff))
                .sorted(comparing(AveragedWeight::getDate))
                .collect(Collectors.toList());
    }

    public Double getAverage() {
        return average;
    }

    public LocalDate getDate() {
        return date;
    }
}
