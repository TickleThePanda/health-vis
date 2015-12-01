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


public class ActivityCollection implements Iterable<ActivityForDate> {

    private final List<ActivityForDate> activities;

    public ActivityCollection() {
	this.activities = new ArrayList<>();
    }

    public ActivityCollection(List<ActivityForDate> activity) {
	this.activities = new ArrayList<>(activity);
    }

    public boolean contains(LocalDate date) {
	for (ActivityForDate activity : this) {
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

    public ActivityCollection getWhereDayOfWeek(DayOfWeek dayOfWeek) {
	return getWherePredicate(a -> a.getDate().getDayOfWeek().equals(dayOfWeek));
    }

    public ActivityCollection getWhereMonth(Month month) {
	return getWherePredicate(a -> a.getDate().getMonth().equals(month));
    }

    @Override
    public Iterator<ActivityForDate> iterator() {
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

    private ActivityCollection getWherePredicate(
	    Predicate<? super ActivityForDate> predicate) {
	return new ActivityCollection(activities.parallelStream().filter(predicate)
		.collect(Collectors.toList()));
    }
}