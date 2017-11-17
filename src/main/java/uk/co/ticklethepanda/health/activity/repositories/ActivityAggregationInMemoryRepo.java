package uk.co.ticklethepanda.health.activity.repositories;

import org.springframework.stereotype.Component;
import uk.co.ticklethepanda.health.activity.services.MinuteActivity;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;

@Component
public class ActivityAggregationInMemoryRepo {

    private Map<DayOfWeek, AggregateDay> aggregateDayByWeekday;
    private Map<Month, AggregateDay> aggregateDayByMonth;
    private AggregateDay aggregateDay;

    public ActivityAggregationInMemoryRepo() {
        aggregateDayByMonth = new HashMap<>();
        aggregateDayByWeekday = new HashMap<>();

        aggregateDay= new AggregateDay();

        for (Month month : Month.values()) {
            aggregateDayByMonth.put(month, new AggregateDay());
        }

        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            aggregateDayByWeekday.put(dayOfWeek, new AggregateDay());
        }
    }

    public List<MinuteActivity> getAverageDay() {
        return aggregateDay.getAverageForEachMinute();
    }

    public Map<DayOfWeek, List<MinuteActivity>> getAverageDayByWeekday() {
        return convertMapToAverageDays(aggregateDayByWeekday);
    }

    public Map<DayOfWeek, Long> getSumOfStepsByDayOfWeek() {
        return convertMapToSumDays(aggregateDayByWeekday);
    }

    public Map<Month, List<MinuteActivity>> getAggregateDayByMonth() {
        return convertMapToAverageDays(aggregateDayByMonth);
    }

    public Map<Month, Long> getSumOfStepsByMonth() {
        return convertMapToSumDays(aggregateDayByMonth);
    }

    public void add(ActivityEntity datedMinuteActivity) {
        Month month = datedMinuteActivity.getDate().getMonth();
        DayOfWeek dayOfWeek = datedMinuteActivity.getDate().getDayOfWeek();

        LocalTime time = datedMinuteActivity.getTime();

        long stepsDelta = datedMinuteActivity.getSteps();

        aggregateDay.add(time, stepsDelta);
        aggregateDayByMonth.get(month).add(time, stepsDelta);
        aggregateDayByWeekday.get(dayOfWeek).add(time, stepsDelta);
    }

    public void remove(ActivityEntity datedMinuteActivity) {
        Month month = datedMinuteActivity.getDate().getMonth();
        DayOfWeek dayOfWeek = datedMinuteActivity.getDate().getDayOfWeek();

        LocalTime time = datedMinuteActivity.getTime();

        long stepsDelta = -datedMinuteActivity.getSteps();

        aggregateDay.remove(time, stepsDelta);
        aggregateDayByMonth.get(month).remove(time, stepsDelta);
        aggregateDayByWeekday.get(dayOfWeek).remove(time, stepsDelta);
    }

    public void addAll(Collection<ActivityEntity> entities) {
        entities.forEach(this::add);
    }

    private <T> Map<T, List<MinuteActivity>> convertMapToAverageDays(Map<T, AggregateDay> o) {
        Map<T, List<MinuteActivity>> n = new HashMap<>();

        for(T t : o.keySet()) {

            List<MinuteActivity> minuteActivities = o.get(t).getAverageForEachMinute();
            n.put(t, minuteActivities);
        }
        return n;
    }

    private <T> Map<T, Long> convertMapToSumDays(Map<T, AggregateDay> o) {
        Map<T, Long> n = new HashMap<>();

        for(T t : o.keySet()) {
            Long minuteActivities = o.get(t).convertToSum();
            n.put(t, minuteActivities);
        }
        return n;
    }

}
