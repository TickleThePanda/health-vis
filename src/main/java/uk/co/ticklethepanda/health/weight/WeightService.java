package uk.co.ticklethepanda.health.weight;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Service
@Transactional
public class WeightService {

    private static final Logger LOG = LogManager.getLogger();

    @Autowired
    private EntityManager entityManager;

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
}
