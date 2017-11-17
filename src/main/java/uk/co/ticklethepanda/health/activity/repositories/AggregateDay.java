package uk.co.ticklethepanda.health.activity.repositories;

import uk.co.ticklethepanda.health.activity.services.MinuteActivity;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class AggregateDay {

    private final static int MINUTES_IN_DAY = 24 * 60;

    private Long[] activitySumByMinute;
    private Integer[] activityCountByMinute;
    private Long sum = 0L;

    AggregateDay() {
        activitySumByMinute = new Long[MINUTES_IN_DAY];
        activityCountByMinute = new Integer[MINUTES_IN_DAY];

        Arrays.fill(activitySumByMinute, 0L);
        Arrays.fill(activityCountByMinute, 0);
    }

    void add(LocalTime time, Long steps) {
        int index = calculateIndex(time);
        activitySumByMinute[index] += steps;
        activityCountByMinute[index]++;
        sum += steps;
    }

    void remove(LocalTime time, Long steps) {
        int index = calculateIndex(time);
        activitySumByMinute[index] -= steps;
        sum -= steps;
        activityCountByMinute[index]--;
    }

    List<MinuteActivity> getAverageForEachMinute() {
        List<MinuteActivity> activities = new ArrayList<>();
        for(int i = 0; i < MINUTES_IN_DAY; i++) {
            LocalTime time = LocalTime.of(i / 60, i % 60);
            double average = (double) activitySumByMinute[i]
                    / (double) activityCountByMinute[i];
            activities.add(new MinuteActivity(time, average));
        }
        return activities;
    }

    Long convertToSum() {
        return sum;
    }

    private int calculateIndex(LocalTime time) {
        return time.getHour() * 60 + time.getMinute();
    }
}
