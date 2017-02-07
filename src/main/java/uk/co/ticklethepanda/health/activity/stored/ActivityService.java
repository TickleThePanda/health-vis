package uk.co.ticklethepanda.health.activity.stored;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.Temporal;
import java.util.*;

/**
 *
 */
@Service
@Transactional
public class ActivityService {

    private static Logger logger = LogManager.getLogger();

    public static final int MINUTES_IN_A_DAY = 60 * 24;

    @Autowired
    private EntityManager entityManager;

    public List<MinuteActivity> getAllActivity() {
        return entityManager.createNamedQuery("findAll", MinuteActivity.class).getResultList();
    }

    @Transactional
    public Collection<MinuteActivity> getActivityForDate(LocalDate date) {
        try {
            return entityManager.createNamedQuery("findByDate", MinuteActivity.class)
                    .setParameter("date", date)
                    .getResultList();
        } catch (NoResultException exception) {
            return null;
        }
    }

    @Transactional
    private MinuteActivity getActivityForTimeAndDate(LocalDate date, LocalTime time) {
        try {
            return entityManager.createNamedQuery("findByDateAndTime", MinuteActivity.class)
                    .setParameter("date", date)
                    .setParameter("time", time)
                    .getSingleResult();
        } catch (NoResultException exception) {
            return null;
        }
    }

    @Transactional
    public boolean hasCompleteEntry(LocalDate date) {
        int countByDate = entityManager.createNamedQuery("countByDate", Number.class)
                .setParameter("date", date)
                .getSingleResult()
                .intValue();

        if(countByDate == MINUTES_IN_A_DAY) {
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public void replaceActivity(MinuteActivity newActivity) {
        MinuteActivity currentActivity =
                getActivityForTimeAndDate(newActivity.getDate(), newActivity.getTime());

        if(currentActivity != null) {
            currentActivity.setSteps(newActivity.getSteps());
            entityManager.persist(currentActivity);
        } else {
            entityManager.persist(newActivity);
        }
    }

    @Transactional
    @Cacheable
    public List<MinuteActivity> getAverageDay() {
        List<Object[]> entries = entityManager.createNamedQuery("getAverageDay", Object[].class).getResultList();
        return convertResultsForAverageDay(entries);
    }

    @Transactional
    @Cacheable
    public List<MinuteActivity> getAverageDaySinceDate(LocalDate date) {
        List<Object[]> entries = entityManager.createNamedQuery("getAverageDaySinceDate", Object[].class)
                .setParameter("date", date)
                .getResultList();
        return convertResultsForAverageDay(entries);
    }

    private List<MinuteActivity> convertResultsForAverageDay(List<Object[]> entries) {
        List<MinuteActivity> minuteActivities = new ArrayList<>();
        for(Object[] entry : entries) {

            LocalTime time = (LocalTime) entry[0];
            double steps = (double) entry[1];

            MinuteActivity minuteActivity = new MinuteActivity();
            minuteActivity.setTime(time);
            minuteActivity.setSteps(steps);

            minuteActivities.add(minuteActivity);
        }
        return minuteActivities;
    }

    @Transactional
    @Cacheable
    public Map<Month, List<MinuteActivity>> getAverageDayByMonth() {
        List<Object[]> entries = entityManager.createNamedQuery("getAverageDayByMonth", Object[].class).getResultList();

        Map<Month, List<MinuteActivity>> monthsToActvivites = new TreeMap<>();

        for(Object[] entry : entries) {
            int monthNumber = (int) entry[0];
            Month month = Month.of(monthNumber);

            LocalTime time = (LocalTime) entry[1];
            double steps = (double) entry[2];

            if(!monthsToActvivites.containsKey(month)) {
                monthsToActvivites.put(month, new ArrayList<>());
            }

            MinuteActivity minuteActivity = new MinuteActivity();
            minuteActivity.setTime(time);
            minuteActivity.setSteps(steps);

            monthsToActvivites.get(month).add(minuteActivity);
        }
        return monthsToActvivites;
    }

    @Transactional
    @Cacheable
    public Map<DayOfWeek, List<MinuteActivity>> getAverageDayByWeekday() {
        List<Object[]> entries = entityManager.createNamedQuery("getAverageDayByWeekday", Object[].class).getResultList();

        Map<DayOfWeek, List<MinuteActivity>> dayOfWeekToActivities = new TreeMap<>();

        for(Object[] entry : entries) {
            int dayOfWeekNumber = (int) entry[0];
            DayOfWeek dayOfWeek = DayOfWeek.of(dayOfWeekNumber + 1);
            LocalTime time = (LocalTime) entry[1];
            double steps = (double) entry[2];

            if(!dayOfWeekToActivities.containsKey(dayOfWeek)) {
                dayOfWeekToActivities.put(dayOfWeek, new ArrayList<>());
            }

            MinuteActivity minuteActivity = new MinuteActivity();
            minuteActivity.setTime(time);
            minuteActivity.setSteps(steps);

            dayOfWeekToActivities.get(dayOfWeek).add(minuteActivity);
        }
        return dayOfWeekToActivities;
    }

    public void replaceActivities(Collection<MinuteActivity> newActivity) {
        for(MinuteActivity minuteActivity : newActivity) {
            replaceActivity(minuteActivity);
        }
    }


    public List<MinuteActivity> getAverageDayForRange(LocalDate startDate, LocalDate endDate) {
        List<Object[]> entries = entityManager.createNamedQuery("getAverageDayBetweenDates", Object[].class)
                .setParameter("start", startDate)
                .setParameter("end", endDate)
                .getResultList();
        return convertResultsForAverageDay(entries);
    }

    public LocalDate getFirstDate() {
        return entityManager.createNamedQuery("getEarliestDateOfActivity", LocalDate.class).getSingleResult();
    }
}
