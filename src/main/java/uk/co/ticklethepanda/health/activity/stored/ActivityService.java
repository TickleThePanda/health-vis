package uk.co.ticklethepanda.health.activity.stored;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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
        List<MinuteActivityFacet<Month>> facets = repo.getAverageDayByMonth();

        Map<Month, List<MinuteActivity>> monthsToActvivites = convertToMap(facets);

        return monthsToActvivites;
    }

    @Cacheable
    public Map<DayOfWeek, List<MinuteActivity>> getAverageDayByWeekday() {
        List<MinuteActivityFacet<DayOfWeek>> facets = repo.getAverageDayByWeekday();

        Map<DayOfWeek, List<MinuteActivity>> monthsToActvivites = convertToMap(facets);

        return monthsToActvivites;
    }

    private <T extends TemporalAccessor> Map<T, List<MinuteActivity>> convertToMap(List<MinuteActivityFacet<T>> facets) {
        Map<T, List<MinuteActivity>> activitiesFacets = new TreeMap<>();

        for (MinuteActivityFacet<T> m : facets) {
            T facet = m.getFacet();

            if (!activitiesFacets.containsKey(facet)) {
                activitiesFacets.put(facet, new ArrayList<>());
            }

            activitiesFacets.get(facet).add(m.getActivity());
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
}
