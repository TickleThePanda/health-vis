package uk.co.ticklethepanda.fitbit.activity;

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
    for (IntradayActivity activity : this) {
      if (activity.getDate().equals(date)) {
        return true;
      }
    }
    return false;
  }

  public MinuteActivitySeries getAverageDayActivity() {
    return collectGroupDayActivity(
        Collectors.averagingDouble(MinuteActivity::getStepCount));
  }

  public MinuteActivitySeries getCumulativeDayActivity() {
    return collectGroupDayActivity(
        Collectors.summingDouble(MinuteActivity::getStepCount));
  }

  public Double getTotalSteps() {
    return collectDayActivity(
        Collectors.summingDouble(MinuteActivity::getStepCount));
  }

  public IntradayActivityRange getWhereDayOfWeek(DayOfWeek dayOfWeek) {
    return getWherePredicate(a -> a.getDate().getDayOfWeek().equals(dayOfWeek));
  }

  public IntradayActivityRange getWhereMonth(Month month) {
    return getWherePredicate(a -> a.getDate().getMonth().equals(month));
  }

  @Override
  public Iterator<IntradayActivity> iterator() {
    return activities.iterator();
  }

  private <E> E collectDayActivity(Collector<MinuteActivity, ?, E> collector) {
    return activities.parallelStream()
        .flatMap(
            l -> l.getMinuteActivitySeries().getElements().parallelStream())
        .collect(collector);
  }

  private MinuteActivitySeries collectGroupDayActivity(
      Collector<MinuteActivity, ?, Double> collector) {
    return MinuteActivitySeries.fromMap(this.collectDayActivity(
        Collectors.groupingBy(MinuteActivity::getTime, collector)));
  }

  private IntradayActivityRange getWherePredicate(
      Predicate<? super IntradayActivity> predicate) {
    return new IntradayActivityRange(activities.parallelStream().filter(predicate)
        .collect(Collectors.toList()));
  }
}