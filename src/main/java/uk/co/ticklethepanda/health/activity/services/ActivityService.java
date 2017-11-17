package uk.co.ticklethepanda.health.activity.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import uk.co.ticklethepanda.health.activity.services.events.activity.CreatedActivityEvent;
import uk.co.ticklethepanda.health.activity.services.events.activity.UpdatedActivityEvent;
import uk.co.ticklethepanda.health.activity.repositories.ActivityEntity;
import uk.co.ticklethepanda.health.activity.repositories.ActivityAggregationInMemoryRepo;
import uk.co.ticklethepanda.health.activity.repositories.ActivityRepo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;

/**
 *
 */
@Service
public class ActivityService {

    public static final int MINUTES_IN_A_DAY = 60 * 24;

    @Autowired
    private ActivityRepo activityRepo;

    @Autowired
    private ActivityAggregationInMemoryRepo activityAggregationRepo;

    @Autowired
    private ApplicationEventPublisher publisher;

    private ActivityEntity getActivityForTimeAndDate(LocalDate date, LocalTime time) {
        return activityRepo.findByDateAndTime(date, time);
    }

    public boolean hasCompleteEntry(LocalDate date) {
        return activityRepo.countByDate(date) == MINUTES_IN_A_DAY;
    }

    public void saveActivity(ActivityEntity newActivity) {
        ActivityEntity previouslyActivity =
                getActivityForTimeAndDate(newActivity.getDate(), newActivity.getTime());

        final boolean create = previouslyActivity == null;

        if(!create) {
            activityRepo.delete(previouslyActivity);
        }

        activityRepo.save(newActivity);

        if (create) {
            publisher.publishEvent(new CreatedActivityEvent(newActivity));
        } else {
            publisher.publishEvent(new UpdatedActivityEvent(previouslyActivity, newActivity));
        }
    }

    public List<MinuteActivity> getAverageDay() {
        return activityAggregationRepo.getAverageDay();
    }

    public Map<Month, List<MinuteActivity>> getAverageDayByMonth() {
        return activityAggregationRepo.getAggregateDayByMonth();
    }

    public Map<DayOfWeek, List<MinuteActivity>> getAverageDayByWeekday() {
        return activityAggregationRepo.getAverageDayByWeekday();
    }

    public void replaceActivities(Collection<ActivityEntity> newActivity) {
        for (ActivityEntity datedMinuteActivity : newActivity) {
            saveActivity(datedMinuteActivity);
        }
    }

    public LocalDate getFirstDate() {
        return activityRepo.getMinDate();
    }

    public Map<Month, Long> getSumByMonth() {

        return activityAggregationRepo.getSumOfStepsByMonth();
    }

    public Map<DayOfWeek, Long> getSumByDayOfWeek() {

        return activityAggregationRepo.getSumOfStepsByDayOfWeek();
    }

    public Double getSumOfSteps() {
        return activityRepo.getSumOfSteps();
    }

}
