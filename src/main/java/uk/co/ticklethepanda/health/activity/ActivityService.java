package uk.co.ticklethepanda.health.activity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.co.ticklethepanda.health.activity.domain.repositories.ActivityRepo;
import uk.co.ticklethepanda.health.activity.domain.entities.MinuteActivity;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.TemporalAccessor;
import java.util.*;

/**
 *
 */
@Service
public class ActivityService {

    private static Logger logger = LogManager.getLogger();

    public static final int MINUTES_IN_A_DAY = 60 * 24;

    @Autowired
    private ActivityRepo repo;

    public List<MinuteActivity> getAllActivity() {
        return repo.findAll();
    }

    public Collection<MinuteActivity> getActivityForDate(LocalDate date) {
        return repo.findByDate(date);
    }

    private MinuteActivity getActivityForTimeAndDate(LocalDate date, LocalTime time) {
        return repo.findByDateAndTime(date, time);
    }

    public boolean hasCompleteEntry(LocalDate date) {
        return repo.countByDate(date) == MINUTES_IN_A_DAY;
    }

    public void saveActivity(MinuteActivity newActivity) {
        MinuteActivity previouslyPersistedActivity =
                getActivityForTimeAndDate(newActivity.getDate(), newActivity.getTime());

        if (previouslyPersistedActivity == null) {
            previouslyPersistedActivity = newActivity;
        } else {
            previouslyPersistedActivity.setSteps(newActivity.getSteps());
        }

        repo.save(previouslyPersistedActivity);
    }

    public List<MinuteActivity> getAverageDay() {
        return repo.getAverageDay();
    }

    public List<MinuteActivity> getAverageDayForRange(LocalDate startDate, LocalDate endDate) {
        return repo.getAverageDayBetweenDates(startDate, endDate);
    }

    @Cacheable
    public Map<Month, List<MinuteActivity>> getAverageDayByMonth() {
        List<Map.Entry<Month, MinuteActivity>> facets = repo.getAverageDayByMonth();

        Map<Month, List<MinuteActivity>> monthsToActvivites = convertToMap(facets);

        return monthsToActvivites;
    }

    @Cacheable
    public Map<DayOfWeek, List<MinuteActivity>> getAverageDayByWeekday() {
        List<Map.Entry<DayOfWeek, MinuteActivity>> facets = repo.getAverageDayByWeekday();

        Map<DayOfWeek, List<MinuteActivity>> monthsToActvivites = convertToMap(facets);

        return monthsToActvivites;
    }

    private <T extends TemporalAccessor> Map<T, List<MinuteActivity>> convertToMap(List<Map.Entry<T, MinuteActivity>> facets) {
        Map<T, List<MinuteActivity>> activitiesFacets = new TreeMap<>();

        for (Map.Entry<T, MinuteActivity> m : facets) {
            T facet = m.getKey();

            if (!activitiesFacets.containsKey(facet)) {
                activitiesFacets.put(facet, new ArrayList<>());
            }

            activitiesFacets.get(facet).add(m.getValue());
        }
        return activitiesFacets;
    }

    public void replaceActivities(Collection<MinuteActivity> newActivity) {
        for (MinuteActivity minuteActivity : newActivity) {
            saveActivity(minuteActivity);
        }
    }

    public LocalDate getFirstDate() {
        return repo.getEarliestDateOfActivity();
    }

    public Map<Month, Double> getSumByMonth() {

        Map<Month, Double> activityByMonth = new TreeMap<>();

        List<Map.Entry<Month, Double>> sumOfStepsByMonth = repo.getSumOfStepsByMonth();

        for (Map.Entry<Month, Double> activitySumFacet : sumOfStepsByMonth) {
            activityByMonth.put(activitySumFacet.getKey(), activitySumFacet.getValue());
        }

        return activityByMonth;
    }

    public Map<DayOfWeek, Double> getSumByDayOfWeek() {

        Map<DayOfWeek, Double> activityByDayOfWeek = new TreeMap<>();

        List<Map.Entry<DayOfWeek, Double>> sumOfStepsByDayOfWeek = repo.getSumOfStepsByDayOfWeek();

        for (Map.Entry<DayOfWeek, Double> activitySumFacet : sumOfStepsByDayOfWeek) {
            activityByDayOfWeek.put(activitySumFacet.getKey(), activitySumFacet.getValue());
        }

        return activityByDayOfWeek;
    }

    public Double getSumOfSteps() {
        return repo.getSumOfSteps();
    }

    public Double getSumOfStepsBetween(LocalDate start, LocalDate end) {
        return repo.getSumOfStepsBetween(start, end);
    }
}
