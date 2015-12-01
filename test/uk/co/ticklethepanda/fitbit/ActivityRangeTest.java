package uk.co.ticklethepanda.fitbit;

import static org.junit.Assert.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

public class ActivityRangeTest {

    @Test
    public void getTotalSteps_blankActivityRange_noSteps() {
	ActivityCollection range = new ActivityCollection();
	assertEquals("Expected no steps", range.getTotalSteps(), 0.0, 0.0);
    }

    @Test
    public void getTotalSteps_activityRange_correctNumberOfSteps() {
	final double numberOfSteps = 8.0;
	final double numberOfMinutes = 9.0;
	final double numberOfDays = 10.0;
	final double expectedSteps = numberOfSteps * numberOfMinutes * numberOfDays;

	MinuteActivity mintueActivity = new MinuteActivity(LocalTime.MIDNIGHT,
		numberOfSteps);

	List<MinuteActivity> minuteActivities = new ArrayList<>();
	for (int i = 0; i < numberOfMinutes; i++) {
	    minuteActivities.add(mintueActivity);
	}
	List<ActivityForDate> days = new ArrayList<ActivityForDate>();
	for (int i = 0; i < numberOfDays; i++) {
	    days.add(new ActivityForDate(LocalDate.now(),
		    new MinuteActivitySeries(minuteActivities)));
	}
	ActivityCollection range = new ActivityCollection(days);

	assertEquals("expected total of steps was not " + expectedSteps,
		expectedSteps, range.getTotalSteps(), 0.0);
    }

    @Test
    public void getAverageDayActivity_activityRange_correctAverageSteps() {
	final double numberOfSteps = 8.0;
	final double numberOfMinutes = 9.0;
	final double numberOfDays = 10.0;
	final double expectedAverageSteps = numberOfSteps;

	List<MinuteActivity> minuteActivities = new ArrayList<>();
	for (int i = 0; i < numberOfMinutes; i++) {
	    minuteActivities.add(
		    new MinuteActivity(LocalTime.MIDNIGHT.plusMinutes(i), numberOfSteps));
	}

	List<ActivityForDate> days = new ArrayList<ActivityForDate>();
	for (int i = 0; i < numberOfDays; i++) {
	    days.add(new ActivityForDate(LocalDate.now(),
		    new MinuteActivitySeries(minuteActivities)));
	}

	MinuteActivitySeries series = new ActivityCollection(days)
		.getAverageDayActivity();

	for (int i = 0; i < numberOfMinutes; i++) {
	    assertEquals("average step count was not correct",
		    expectedAverageSteps, series
			    .getByLocalTime(LocalTime.MIDNIGHT.plusMinutes(i)).getStepCount(),
		    0.0);
	}
    }

    @Test
    public void getCumulativeDayActivity_activityRange_correctCumulativeSteps() {
	final double numberOfSteps = 8.0;
	final double numberOfMinutes = 9.0;
	final double numberOfDays = 10.0;
	final double expectedCumulativeSteps = numberOfSteps * numberOfDays;

	List<MinuteActivity> minuteActivities = new ArrayList<>();
	for (int i = 0; i < numberOfMinutes; i++) {
	    minuteActivities.add(
		    new MinuteActivity(LocalTime.MIDNIGHT.plusMinutes(i), numberOfSteps));
	}

	List<ActivityForDate> days = new ArrayList<ActivityForDate>();
	for (int i = 0; i < numberOfDays; i++) {
	    days.add(new ActivityForDate(LocalDate.now(),
		    new MinuteActivitySeries(minuteActivities)));
	}

	MinuteActivitySeries series = new ActivityCollection(days)
		.getCumulativeDayActivity();

	for (int i = 0; i < numberOfMinutes; i++) {
	    assertEquals("average step count was not correct",
		    expectedCumulativeSteps, series
			    .getByLocalTime(LocalTime.MIDNIGHT.plusMinutes(i)).getStepCount(),
		    0.0);
	}
    }

    @Test
    public void getWhereDay_activityRange_correctDaysContained() {
	final double numberOfDays = 10.0;
	final DayOfWeek dow = DayOfWeek.SATURDAY;

	LocalDate baseDate = LocalDate.now();

	List<ActivityForDate> days = new ArrayList<ActivityForDate>();
	for (int i = 0; i < numberOfDays; i++) {
	    days.add(new ActivityForDate(baseDate.plusDays(i),
		    new MinuteActivitySeries()));
	}

	ActivityCollection range = new ActivityCollection(days)
		.getWhereDayOfWeek(dow);

	for (int i = 0; i < numberOfDays; i++) {
	    LocalDate date = baseDate.plusDays(i);
	    if (date.getDayOfWeek().equals(dow)) {
		assertTrue(date.toString() + " was not contained in the activity range",
			range.contains(date));
	    }
	}
    }

    @Test
    public void getWhereDay_activityRange_correctDaysNotContained() {
	final double numberOfDays = 50.0;
	final DayOfWeek dow = DayOfWeek.SATURDAY;

	LocalDate baseDate = LocalDate.now();

	List<ActivityForDate> days = new ArrayList<ActivityForDate>();
	for (int i = 0; i < numberOfDays; i++) {
	    days.add(new ActivityForDate(baseDate.plusDays(i),
		    new MinuteActivitySeries()));
	}

	ActivityCollection range = new ActivityCollection(days)
		.getWhereDayOfWeek(dow);

	for (int i = 0; i < numberOfDays; i++) {
	    LocalDate date = baseDate.plusDays(i);
	    if (!date.getDayOfWeek().equals(dow)) {
		assertFalse("unexpected DayActivity was contained in ActivityRange",
			range.contains(date));
	    }
	}
    }

    @Test
    public void getWhereMonth_activityRange_correctMonthsContained() {
	final double numberOfMonths = 35.0;
	final Month selectedMonth = Month.JULY;

	LocalDate baseDate = LocalDate.now();

	List<ActivityForDate> days = new ArrayList<ActivityForDate>();
	for (int i = 0; i < numberOfMonths; i++) {
	    days.add(new ActivityForDate(baseDate.plusMonths(i),
		    new MinuteActivitySeries()));
	}

	ActivityCollection range = new ActivityCollection(days)
		.getWhereMonth(selectedMonth);

	for (int i = 0; i < numberOfMonths; i++) {
	    LocalDate date = baseDate.plusDays(i);
	    if (date.getMonth().equals(selectedMonth)) {
		assertTrue("expected DayActivity was not contained in ActivityRange",
			range.contains(date));
	    }
	}
    }

    @Test
    public void getWhereMonth_activityRange_correctMonthsNotContained() {
	final double numberOfMonths = 35.0;
	final Month selectedMonth = Month.JULY;

	LocalDate baseDate = LocalDate.now();

	List<ActivityForDate> days = new ArrayList<ActivityForDate>();
	for (int i = 0; i < numberOfMonths; i++) {
	    days.add(new ActivityForDate(baseDate.plusMonths(i),
		    new MinuteActivitySeries()));
	}

	ActivityCollection range = new ActivityCollection(days)
		.getWhereMonth(selectedMonth);

	for (int i = 0; i < numberOfMonths; i++) {
	    LocalDate date = baseDate.plusDays(i);
	    if (!date.getMonth().equals(selectedMonth)) {
		assertFalse("unexpected DayActivity was contained in ActivityRange",
			range.contains(date));
	    }
	}
    }

}
