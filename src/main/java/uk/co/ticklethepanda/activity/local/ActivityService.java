package uk.co.ticklethepanda.activity.local;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.jpa.criteria.expression.function.AggregationFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import javax.validation.constraints.Min;
import java.time.LocalDate;

/**
 * Created by panda on 08/11/2016.
 */
@Component
public class ActivityService {

    public static final int MINUTES_IN_A_DAY = 60 * 24;

    @Autowired
    private EntityManager entityManager;
    private Logger logger = LogManager.getLogger();

    @Transactional
    public DayActivity getActivityForDate(LocalDate date) {
        try {
            return entityManager.createNamedQuery("findDayActivityByDate", DayActivity.class)
                    .setParameter("date", date)
                    .getSingleResult();
        } catch (NoResultException exception) {
            return null;
        }
    }

    @Transactional
    public boolean hasCompleteEntry(LocalDate date) {
        DayActivity dayActivity = this.getActivityForDate(date);
        if(dayActivity != null
                && dayActivity.getMinuteActivityEntities() != null
                && dayActivity.getMinuteActivityEntities().size() == MINUTES_IN_A_DAY) {
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public void replaceActivityWith(DayActivity newActivity) {
        DayActivity currentActivity = getActivityForDate(newActivity.getDate());
        if(currentActivity != null) {
            currentActivity.setMinuteActivityEntities(newActivity.getMinuteActivityEntities());
            currentActivity.getMinuteActivityEntities().forEach(minuteActivity -> minuteActivity.setDayActivity(currentActivity));

            Session session = entityManager.unwrap(Session.class);
            Transaction tx = session.beginTransaction();
            int count = 0;
            for(MinuteActivity minuteActivity : currentActivity.getMinuteActivityEntities()) {
                session.save(minuteActivity);
                if(++count % 50 == 0) {
                    session.flush();
                    session.clear();
                }
            }

            tx.commit();
            session.close();
            entityManager.persist(currentActivity);
        } else {
            entityManager.persist(newActivity);
        }

    }

}
