package uk.co.ticklethepanda.activity.fitbit;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class FitbitIntradayActivityRange implements Iterable<FitbitIntradayActivity> {

    private final List<FitbitIntradayActivity> activities;

    public FitbitIntradayActivityRange() {
        this.activities = new ArrayList<>();
    }

    public FitbitIntradayActivityRange(List<FitbitIntradayActivity> activity) {
        this.activities = new ArrayList<>(activity);
    }

    public boolean contains(LocalDate date) {
        for (final FitbitIntradayActivity activity : this) {
            if (activity.getDate().equals(date)) {
                return true;
            }
        }
        return false;
    }

    public FitbitMinuteActivitySeries getAverageDayActivity() {
        return this.collectGroupDayActivity(
                Collectors.averagingDouble(FitbitMinuteActivity::getStepCount));
    }

    public FitbitMinuteActivitySeries getCumulativeDayActivity() {
        return this.collectGroupDayActivity(
                Collectors.summingDouble(FitbitMinuteActivity::getStepCount));
    }

    public Double getTotalSteps() {
        return this.collectDayActivity(
                Collectors.summingDouble(FitbitMinuteActivity::getStepCount));
    }

    public FitbitIntradayActivityRange getWhereDayOfWeek(DayOfWeek dayOfWeek) {
        return this.getWherePredicate(a -> a.getDate().getDayOfWeek().equals(dayOfWeek));
    }

    public FitbitIntradayActivityRange getWhereMonth(Month month) {
        return this.getWherePredicate(a -> a.getDate().getMonth().equals(month));
    }

    @Override
    public Iterator<FitbitIntradayActivity> iterator() {
        return this.activities.iterator();
    }

    private <E> E collectDayActivity(Collector<FitbitMinuteActivity, ?, E> collector) {
        return this.activities.parallelStream()
                .flatMap(
                        l -> l.getIntradayMinuteActivitySeries().getElements().parallelStream())
                .collect(collector);
    }

    private FitbitMinuteActivitySeries collectGroupDayActivity(
            Collector<FitbitMinuteActivity, ?, Double> collector) {
        return FitbitMinuteActivitySeries.fromMap(this.collectDayActivity(
                Collectors.groupingBy(FitbitMinuteActivity::getTime, collector)));
    }

    private FitbitIntradayActivityRange getWherePredicate(
            Predicate<? super FitbitIntradayActivity> predicate) {
        return new FitbitIntradayActivityRange(this.activities.parallelStream().filter(predicate)
                .collect(Collectors.toList()));
    }

    public List<FitbitIntradayActivity> getItems() {
        return this.activities;
    }
}