package uk.co.ticklethepanda.health.weight;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.co.ticklethepanda.health.weight.domain.model.AverageWeight;
import uk.co.ticklethepanda.health.weight.domain.model.EntryMeridiemPeriod;
import uk.co.ticklethepanda.health.weight.domain.entities.Weight;
import uk.co.ticklethepanda.health.weight.domain.repositories.WeightRepo;
import uk.co.ticklethepanda.utility.date.LocalDateRange;

import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.*;
import static uk.co.ticklethepanda.utility.date.LocalDateRanges.*;
import static uk.co.ticklethepanda.utility.date.LocalDateRanges.From.from;
import static uk.co.ticklethepanda.utility.date.LocalDateRanges.Until.*;

@Service
public class WeightService {

    private static final Logger LOG = LogManager.getLogger();

    private WeightRepo weightRepo;
    private double weightTarget;
    private double weightTargetIntermediateStep;

    public WeightService(
            @Autowired WeightRepo weightRepo,
            @Value("${weight.target}") double weightTarget,
            @Value("${weight.target.intermediate.step}") double weightTargetIntermediateStep
    ) {
        this.weightRepo = weightRepo;
        this.weightTarget = weightTarget;
        this.weightTargetIntermediateStep = weightTargetIntermediateStep;
    }

    public List<Weight> getAllWeight() {
        return weightRepo.findWhereNotEmpty();
    }

    public Weight getWeightForDate(LocalDate date) {
        return weightRepo.findByDate(date);
    }

    public Weight createWeightEntryForPeriod(LocalDate date, EntryMeridiemPeriod entryMeridiemPeriod, Double weightValue) {
        Weight weight = weightRepo.findByDate(date);
        if (weight == null) {
            weight = new Weight(date, null, null);
        }

        switch (entryMeridiemPeriod) {
            case AM:
                weight.setWeightAm(weightValue);
                break;
            case PM:
                weight.setWeightPm(weightValue);
                break;
            default:
                throw new InvalidParameterException();
        }

        if(weight.hasNoEntries() && weight.getId() != null) {
            weightRepo.delete(weight.getId());
        } else {
            weightRepo.save(weight);
        }

        return weight;
    }

    public Weight getMostRecent() {
        return weightRepo.findLatest();
    }

    public double getWeightTarget() {
        return weightTarget;
    }

    public double getIntermediateWeightTargetForWeight(AverageWeight weight) {
        double steps = Math.floor((weight.getAverage() - weightTarget) / weightTargetIntermediateStep);

        return weightTarget + weightTargetIntermediateStep * steps;
    }

    public List<Weight> getWeightWithinDateRange(LocalDate start, LocalDate end) {
        return weightRepo.findWithinDateRange(start, end);
    }

    public List<AverageWeight> getAverageWeightForEachPeriodInRange(int periodInDays, LocalDate filterStart, LocalDate filterEnd) {
        List<Weight> weights = weightRepo.findWithinDateRange(filterStart, filterEnd);
        List<AverageWeight> averageWeights = new ArrayList<>();

        LocalDate beginning = weights.stream()
                .map(Weight::getDate)
                .min(Comparator.naturalOrder())
                .get();

        LocalDate end = LocalDate.now();

        for (LocalDateRange dateRange : every(periodInDays, DAYS, from(beginning), until(end))) {

            List<Weight> weightsInPeriod = weights.stream()
                    .filter(w -> dateRange.contains(w.getDate()))
                    .collect(Collectors.toList());

            if(weightsInPeriod.size() > 0) {

                double average = weightsInPeriod.stream()
                        .mapToDouble(Weight::getAverage)
                        .average()
                        .getAsDouble();

                int count = weightsInPeriod.size();

                averageWeights.add(
                        new AverageWeight(
                                dateRange.getStart(),
                                dateRange.getEnd().minusDays(1), // minus 1 day because range is exclusive
                                average,
                                count)
                );
            }

        }

        return averageWeights;

    }

    public List<AverageWeight> getAverageWeightForEachPeriod(int periodInDays) {
        return getAverageWeightForEachPeriodInRange(periodInDays, null, null);
    }
}
