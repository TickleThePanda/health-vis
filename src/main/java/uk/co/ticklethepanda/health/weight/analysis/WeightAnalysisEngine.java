package uk.co.ticklethepanda.health.weight.analysis;

import org.springframework.stereotype.Component;
import uk.co.ticklethepanda.health.weight.domain.entities.Weight;
import uk.co.ticklethepanda.health.weight.dtos.analysis.WeightAnalysisForDateDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WeightAnalysisEngine {

    public List<AnalysedWeight> analyse(List<Weight> weights) {
        Map<LocalDate, Weight> dateWeightMap = new HashMap<>();

        for (Weight weight : weights) {
            dateWeightMap.put(weight.getDate(), weight);
        }

        Double overnightDiffAverage = calculateOvernightDiffAverage(weights, dateWeightMap);
        Double duringDayDiffAverage = calcualteDayDiff(weights);

        List<AnalysedWeight> analysedWeight = new ArrayList<>();

        for (Weight todaysWeight : weights) {

            if (todaysWeight.isFullDay()) {
                double predictedWeight = (todaysWeight.getWeightAm() + todaysWeight.getWeightPm()) / 2.0;
                analysedWeight.add(new AnalysedWeight(todaysWeight.getDate(), predictedWeight));

            } else if (todaysWeight.hasOnlyPmEntry()) {
                Weight yesterdaysWeight = dateWeightMap.get(todaysWeight.getDate().minusDays(1));

                Double predictedWeight = predictValue(
                        yesterdaysWeight != null ? yesterdaysWeight.getWeightPm() : null,
                        todaysWeight.getWeightPm(),
                        overnightDiffAverage != null ? -overnightDiffAverage : null,
                        duringDayDiffAverage
                );

                analysedWeight.add(new AnalysedWeight(todaysWeight.getDate(), predictedWeight));

            } else if (todaysWeight.hasOnlyAmEntry()) {
                Weight tomorrowsWeight = dateWeightMap.get(todaysWeight.getDate().plusDays(1));

                Double predictedWeight = predictValue(
                        tomorrowsWeight != null ? tomorrowsWeight.getWeightAm() : null,
                        todaysWeight.getWeightAm(),
                        overnightDiffAverage,
                        duringDayDiffAverage != null ? -duringDayDiffAverage : null
                );

                analysedWeight.add(new AnalysedWeight(todaysWeight.getDate(), predictedWeight));
            }
        }

        return analysedWeight;
    }

    private Double predictValue(Double before, Double after,
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

    private Double calculateOvernightDiffAverage(List<Weight> weights, Map<LocalDate, Weight> dateWeightMap) {
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

    private Double calcualteDayDiff(List<Weight> weights) {
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

}
