package uk.co.ticklethepanda.health.weight;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.http.client.fluent.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class WeightService {

    private static final Logger LOG = LogManager.getLogger();

    private EntityManager entityManager;

    public WeightService(@Autowired EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void createWeightEntry(Weight weight) {
        LOG.info("{} {} {}", weight.getDate(), weight.getWeightAm(), weight.getWeightPm());

        List<Weight> oldWeightItems = entityManager.createNamedQuery("weight.findByDate", Weight.class)
                .setParameter("date", weight.getDate())
                .getResultList();

        LOG.debug("Results: {}", () -> oldWeightItems.toString());

        if(oldWeightItems.size() == 0) {
            entityManager.persist(weight);
        } else {
            assert oldWeightItems.size() == 1;
            Weight oldWeightEntry = oldWeightItems.get(0);
            oldWeightEntry.setWeightAm(weight.getWeightAm());
            oldWeightEntry.setWeightPm(weight.getWeightPm());
            entityManager.merge(oldWeightEntry);
        }
    }

    public List<Weight> getAllWeight() {
        return entityManager.createNamedQuery("weight.findAll", Weight.class).getResultList();
    }

    public List<Weight> getAllWeightWithEntries() {
        return entityManager.createNamedQuery("weight.findWithEntries", Weight.class).getResultList();
    }

    public Weight getWeightForDate(LocalDate date) {
        List<Weight> results = entityManager
                .createNamedQuery("weight.findByDate", Weight.class)
                .setParameter("date", date)
                .getResultList();
        if(!results.isEmpty()) {
            return results.get(0);
        } else {
            return null;
        }
    }

    public Weight createWeightEntryForPeriod(LocalDate date, EntryPeriod entryPeriod, Double weightValue) {
        Weight weight = this.getWeightForDate(date);
        if(weight == null) {
            weight = new Weight(date, null, null);
            entityManager.persist(weight);
        }

        switch (entryPeriod) {
            case AM:
                weight.setWeightAm(weightValue);
                break;
            case PM:
                weight.setWeightPm(weightValue);
        }

        entityManager.merge(weight);
        return weight;
    }
}
