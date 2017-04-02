package uk.co.ticklethepanda.health.weight;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.util.List;

@Service
public class WeightService {

    private static final Logger LOG = LogManager.getLogger();

    private WeightRepo weightRepo;

    public WeightService(@Autowired WeightRepo weightRepo) {
        this.weightRepo = weightRepo;
    }

    public List<Weight> getAllWeight() {
        return weightRepo.findAll();
    }

    public List<Weight> getAllWeightWithEntries() {
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
}
