package uk.co.ticklethepanda.activity.fitbit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ActivityRangeTest {

  @Test
  public void getAverageDayActivity_activityRange_correctAverageSteps() {
    final double numberOfSteps = 8.0;
    final double numberOfMinutes = 9.0;
    final double numberOfDays = 10.0;

    final List<FitbitMinuteActivity> minuteActivities = new ArrayList<>();
    for (int i = 0; i < numberOfMinutes; i++) {
      minuteActivities.add(
          new FitbitMinuteActivity(LocalTime.MIDNIGHT.plusMinutes(i), numberOfSteps));
    }

    final List<FitbitIntradayActivity> days = new ArrayList<>();
    for (int i = 0; i < numberOfDays; i++) {
      days.add(new FitbitIntradayActivity(LocalDate.now(),
          new FitbitMinuteActivitySeries(minuteActivities)));
    }

    final FitbitMinuteActivitySeries series = new FitbitIntradayActivityRange(days)
        .getAverageDayActivity();

    for (int i = 0; i < numberOfMinutes; i++) {
      assertEquals("average step count was not correct",
              numberOfSteps, series
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

    final List<FitbitMinuteActivity> minuteActivities = new ArrayList<>();
    for (int i = 0; i < numberOfMinutes; i++) {
      minuteActivities.add(
          new FitbitMinuteActivity(LocalTime.MIDNIGHT.plusMinutes(i), numberOfSteps));
    }

    final List<FitbitIntradayActivity> days = new ArrayList<>();
    for (int i = 0; i < numberOfDays; i++) {
      days.add(new FitbitIntradayActivity(LocalDate.now(),
          new FitbitMinuteActivitySeries(minuteActivities)));
    }

    final FitbitMinuteActivitySeries series = new FitbitIntradayActivityRange(days)
        .getCumulativeDayActivity();

    for (int i = 0; i < numberOfMinutes; i++) {
      assertEquals("average step count was not correct",
          expectedCumulativeSteps, series
              .getByLocalTime(LocalTime.MIDNIGHT.plusMinutes(i)).getStepCount(),
          0.0);
    }
  }

  @Test
  public void getTotalSteps_activityRange_correctNumberOfSteps() {
    final double numberOfSteps = 8.0;
    final double numberOfMinutes = 9.0;
    final double numberOfDays = 10.0;
    final double expectedSteps = numberOfSteps * numberOfMinutes * numberOfDays;

    final FitbitMinuteActivity fitbitMinuteActivity = new FitbitMinuteActivity(LocalTime.MIDNIGHT,
        numberOfSteps);

    final List<FitbitMinuteActivity> minuteActivities = new ArrayList<>();
    for (int i = 0; i < numberOfMinutes; i++) {
      minuteActivities.add(fitbitMinuteActivity);
    }
    final List<FitbitIntradayActivity> days = new ArrayList<>();
    for (int i = 0; i < numberOfDays; i++) {
      days.add(new FitbitIntradayActivity(LocalDate.now(),
          new FitbitMinuteActivitySeries(minuteActivities)));
    }
    final FitbitIntradayActivityRange range = new FitbitIntradayActivityRange(days);

    assertEquals("expected total of steps was not " + expectedSteps,
        expectedSteps, range.getTotalSteps(), 0.0);
  }

  @Test
  public void getTotalSteps_blankActivityRange_noSteps() {
    final FitbitIntradayActivityRange range = new FitbitIntradayActivityRange();
    assertEquals("Expected no steps", range.getTotalSteps(), 0.0, 0.0);
  }

  @Test
  public void getWhereDay_activityRange_correctDaysContained() {
    final double numberOfDays = 10.0;
    final DayOfWeek dow = DayOfWeek.SATURDAY;

    final LocalDate baseDate = LocalDate.now();

    final List<FitbitIntradayActivity> days = new ArrayList<>();
    for (int i = 0; i < numberOfDays; i++) {
      days.add(new FitbitIntradayActivity(baseDate.plusDays(i),
          new FitbitMinuteActivitySeries()));
    }

    final FitbitIntradayActivityRange range = new FitbitIntradayActivityRange(days)
        .getWhereDayOfWeek(dow);

    for (int i = 0; i < numberOfDays; i++) {
      final LocalDate date = baseDate.plusDays(i);
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

    final LocalDate baseDate = LocalDate.now();

    final List<FitbitIntradayActivity> days = new ArrayList<>();
    for (int i = 0; i < numberOfDays; i++) {
      days.add(new FitbitIntradayActivity(baseDate.plusDays(i),
          new FitbitMinuteActivitySeries()));
    }

    final FitbitIntradayActivityRange range = new FitbitIntradayActivityRange(days)
        .getWhereDayOfWeek(dow);

    for (int i = 0; i < numberOfDays; i++) {
      final LocalDate date = baseDate.plusDays(i);
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

    final LocalDate baseDate = LocalDate.now();

    final List<FitbitIntradayActivity> days = new ArrayList<>();
    for (int i = 0; i < numberOfMonths; i++) {
      days.add(new FitbitIntradayActivity(baseDate.plusMonths(i),
          new FitbitMinuteActivitySeries()));
    }

    final FitbitIntradayActivityRange range = new FitbitIntradayActivityRange(days)
        .getWhereMonth(selectedMonth);

    for (int i = 0; i < numberOfMonths; i++) {
      final LocalDate date = baseDate.plusDays(i);
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

    final LocalDate baseDate = LocalDate.now();

    final List<FitbitIntradayActivity> days = new ArrayList<>();
    for (int i = 0; i < numberOfMonths; i++) {
      days.add(new FitbitIntradayActivity(baseDate.plusMonths(i),
          new FitbitMinuteActivitySeries()));
    }

    final FitbitIntradayActivityRange range = new FitbitIntradayActivityRange(days)
        .getWhereMonth(selectedMonth);

    for (int i = 0; i < numberOfMonths; i++) {
      final LocalDate date = baseDate.plusDays(i);
      if (!date.getMonth().equals(selectedMonth)) {
        assertFalse("unexpected DayActivity was contained in ActivityRange",
            range.contains(date));
      }
    }
  }

}
