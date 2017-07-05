package uk.co.ticklethepanda.health.weight;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.co.ticklethepanda.health.weight.domain.entities.EntryPeriod;
import uk.co.ticklethepanda.health.weight.domain.entities.Weight;
import uk.co.ticklethepanda.health.weight.domain.repositories.WeightRepo;

import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.util.List;

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

    public Weight createWeightEntryForPeriod(LocalDate date, EntryPeriod entryPeriod, Double weightValue) {
        Weight weight = weightRepo.findByDate(date);
        if (weight == null) {
            weight = new Weight(date, null, null);
        }

        switch (entryPeriod) {
            case AM:
                weight.setWeightAm(weightValue);
                break;
            case PM:
                weight.setWeightPm(weightValue);
                break;
            default:
                throw new InvalidParameterException();
        }

        weightRepo.save(weight);

        return weight;
    }

    public Weight getMostRecent() {
        return weightRepo.findLatest();
    }

    public double getWeightTarget() {
        return weightTarget;
    }

    public double getIntermediateWeightTargetForWeight(Weight weight) {
        double steps = Math.floor((weight.getAverage() - weightTarget) / weightTargetIntermediateStep);

        return weightTarget + weightTargetIntermediateStep * steps;
    }

    public List<Weight> getWeightWithinDateRange(LocalDate start, LocalDate end) {
        return weightRepo.findWithinDateRange(start, end);
    }
}
