package uk.co.ticklethepanda.activity.fitbit;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
        return new FitbitMinuteActivitySeries(
                this.activities.parallelStream()
                        .flatMap(l -> l.getIntradayMinuteActivitySeries().getElements().parallelStream())
                        .collect(Collectors.groupingBy(FitbitMinuteActivity::getTime,
                                Collectors.averagingDouble(FitbitMinuteActivity::getStepCount)))
                        .keySet().stream().map(
                                a -> new FitbitMinuteActivity(a, this.activities.parallelStream().flatMap(l1 -> l1.getIntradayMinuteActivitySeries().getElements().parallelStream())
                                                .collect(Collectors.groupingBy(FitbitMinuteActivity::getTime, Collectors.averagingDouble(FitbitMinuteActivity::getStepCount))).get(a)))
                        .sorted((a, b) -> a.getTime().compareTo(b.getTime()))
                        .collect(Collectors.toList()));
    }

    public FitbitMinuteActivitySeries getCumulativeDayActivity() {
        return new FitbitMinuteActivitySeries(
                this.activities.parallelStream()
                        .flatMap(
                                l -> l.getIntradayMinuteActivitySeries().getElements().parallelStream())
                        .collect(Collectors.groupingBy(FitbitMinuteActivity::getTime, Collectors.summingDouble(FitbitMinuteActivity::getStepCount))).keySet().stream().map(a -> new FitbitMinuteActivity(a, this.activities.parallelStream()
                                .flatMap(
                                        l1 -> l1.getIntradayMinuteActivitySeries().getElements().parallelStream())
                                .collect(Collectors.groupingBy(FitbitMinuteActivity::getTime, Collectors.summingDouble(FitbitMinuteActivity::getStepCount))).get(a)))
                        .sorted((a, b) -> a.getTime().compareTo(b.getTime()))
                        .collect(Collectors.toList()));
    }

    public Double getTotalSteps() {
        return this.activities.parallelStream()
                .flatMap(
                        l -> l.getIntradayMinuteActivitySeries().getElements().parallelStream())
                .collect(Collectors.summingDouble(FitbitMinuteActivity::getStepCount));
    }

    @Override
    public Iterator<FitbitIntradayActivity> iterator() {
        return this.activities.iterator();
    }

    public List<FitbitIntradayActivity> getItems() {
        return this.activities;
    }
}