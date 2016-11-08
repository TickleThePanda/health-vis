package uk.co.ticklethepanda.fitbit;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class IntradayActivityRange implements Iterable<IntradayActivity> {

    private final List<IntradayActivity> activities;

    public IntradayActivityRange() {
        this.activities = new ArrayList<>();
    }

    public IntradayActivityRange(List<IntradayActivity> activity) {
        this.activities = new ArrayList<>(activity);
    }

    public boolean contains(LocalDate date) {
        for (final IntradayActivity activity : this) {
            if (activity.getDate().equals(date)) {
                return true;
            }
        }
        return false;
    }

    public IntradayMinuteActivitySeries getAverageDayActivity() {
        return this.collectGroupDayActivity(
                Collectors.averagingDouble(IntradayMinuteActivity::getStepCount));
    }

    public IntradayMinuteActivitySeries getCumulativeDayActivity() {
        return this.collectGroupDayActivity(
                Collectors.summingDouble(IntradayMinuteActivity::getStepCount));
    }

    public Double getTotalSteps() {
        return this.collectDayActivity(
                Collectors.summingDouble(IntradayMinuteActivity::getStepCount));
    }

    public IntradayActivityRange getWhereDayOfWeek(DayOfWeek dayOfWeek) {
        return this.getWherePredicate(a -> a.getDate().getDayOfWeek().equals(dayOfWeek));
    }

    public IntradayActivityRange getWhereMonth(Month month) {
        return this.getWherePredicate(a -> a.getDate().getMonth().equals(month));
    }

    @Override
    public Iterator<IntradayActivity> iterator() {
        return this.activities.iterator();
    }

    private <E> E collectDayActivity(Collector<IntradayMinuteActivity, ?, E> collector) {
        return this.activities.parallelStream()
                .flatMap(
                        l -> l.getIntradayMinuteActivitySeries().getElements().parallelStream())
                .collect(collector);
    }

    private IntradayMinuteActivitySeries collectGroupDayActivity(
            Collector<IntradayMinuteActivity, ?, Double> collector) {
        return IntradayMinuteActivitySeries.fromMap(this.collectDayActivity(
                Collectors.groupingBy(IntradayMinuteActivity::getTime, collector)));
    }

    private IntradayActivityRange getWherePredicate(
            Predicate<? super IntradayActivity> predicate) {
        return new IntradayActivityRange(this.activities.parallelStream().filter(predicate)
                .collect(Collectors.toList()));
    }

    public List<IntradayActivity> getItems() {
        return this.activities;
    }
}