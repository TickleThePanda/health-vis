package uk.co.ticklethepanda.health.weight;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PredictedWeight {

    private final Double value;
    private final LocalDate date;

    /**
     * @param date  The date.
     * @param value The average value for the day.
     */
    public PredictedWeight(LocalDate date, Double value) {
        this.date = date;
        this.value = value;
    }

    public static List<PredictedWeight> calculateBasicAverage(List<Weight> weights) {
        List<PredictedWeight> predictedWeights = new ArrayList<>();

        for (Weight weight : weights) {
            if (weight.isFullDay()) {
                predictedWeights.add(new PredictedWeight(weight.getDate(), weight.getAverage()));
            } else if (weight.hasOnlyAmEntry()) {
                predictedWeights.add(new PredictedWeight(weight.getDate(), weight.getWeightAm()));
            } else if (weight.hasOnlyPmEntry()) {
                predictedWeights.add(new PredictedWeight(weight.getDate(), weight.getWeightPm()));
            }
        }
        return predictedWeights;
    }

    public static List<PredictedWeight> predictWeights(List<Weight> weights) {

        Map<LocalDate, Weight> dateWeightMap = new HashMap<>();

        for (Weight weight : weights) {
            dateWeightMap.put(weight.getDate(), weight);
        }

        Double overnightDiffAverage = calculateOvernightDiffAverage(weights, dateWeightMap);
        Double duringDayDiffAverage = calcualteDayDiff(weights);

        List<PredictedWeight> predictedWeights = new ArrayList<>();

        for (Weight todaysWeight : weights) {

            if (todaysWeight.isFullDay()) {
                double predictedWeight = (todaysWeight.getWeightAm() + todaysWeight.getWeightPm()) / 2.0;
                predictedWeights.add(new PredictedWeight(todaysWeight.getDate(), predictedWeight));

            } else if (todaysWeight.hasOnlyPmEntry()) {
                Weight yesterdaysWeight = dateWeightMap.get(todaysWeight.getDate().minusDays(1));

                Double predictedWeight = predictValue(
                        yesterdaysWeight != null ? yesterdaysWeight.getWeightPm() : null,
                        todaysWeight.getWeightPm(),
                        overnightDiffAverage != null ? -overnightDiffAverage : null,
                        duringDayDiffAverage
                );

                predictedWeights.add(new PredictedWeight(todaysWeight.getDate(), predictedWeight));

            } else if (todaysWeight.hasOnlyAmEntry()) {
                Weight tomorrowsWeight = dateWeightMap.get(todaysWeight.getDate().plusDays(1));

                Double predictedWeight = predictValue(
                        tomorrowsWeight != null ? tomorrowsWeight.getWeightAm() : null,
                        todaysWeight.getWeightAm(),
                        overnightDiffAverage,
                        duringDayDiffAverage != null ? -duringDayDiffAverage : null
                );

                predictedWeights.add(new PredictedWeight(todaysWeight.getDate(), predictedWeight));
            }
        }

        return predictedWeights;
    }

    private static Double predictValue(Double before, Double after,
                                       Double diffBefore, Double diffAfter) {

        // if everything has a value, use it all
        if (before != null && after != null) {
            if (diffBefore != null && diffAfter != null) {
                return (before - (diffBefore / 2.0) + after - (diffAfter / 2.0)) / 2.0;
            }
        }

        // not everything has a value so look at the value before or the value after
        if (before != null && diffBefore != null) {
            return before - (diffBefore / 2.0);
        }

        if (after != null && diffAfter != null) {
            return after - (diffAfter / 2.0);
        }

        // it turns out that the diffs are null
        if (before != null) {
            return before;
        }

        if (after != null) {
            return after;
        }

        return null;
    }

    private static Double calculateOvernightDiffAverage(List<Weight> weights, Map<LocalDate, Weight> dateWeightMap) {
        double diffSum = 0;
        int diffCount = 0;

        for (Weight todaysWeight : weights) {
            Weight yesterdaysWeight = dateWeightMap.get(todaysWeight.getDate().minusDays(1));
            if (yesterdaysWeight != null
                    && todaysWeight.getWeightAm() != null
                    && yesterdaysWeight.getWeightPm() != null) {

                diffCount++;
                diffSum += todaysWeight.getWeightAm() - yesterdaysWeight.getWeightPm();
            }
        }

        if (diffCount == 0) {
            return null;
        }

        return diffSum / (double) diffCount;
    }

    private static Double calcualteDayDiff(List<Weight> weights) {
        double diffSum = 0;
        int diffCount = 0;

        for (Weight weight : weights) {
            if (weight.isFullDay()) {
                diffCount++;
                diffSum += weight.getWeightPm() - weight.getWeightAm();
            }
        }

        if (diffCount == 0) {
            return null;
        }

        return diffSum / (double) diffCount;
    }

    public Double getValue() {
        return value;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PredictedWeight that = (PredictedWeight) o;

        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        return date != null ? date.equals(that.date) : that.date == null;

    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PredictedWeight{" +
                "value=" + value +
                ", date=" + date +
                '}';
    }
}
