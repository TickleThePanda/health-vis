package uk.co.ticklethepanda.fitbit;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActivityForDate implements Iterable<MinuteActivity> {

    private static class DateStatistics {

	@SerializedName("dateTime")
	@Expose
	private String dateTime;

	private transient LocalDate date;

	public DateStatistics(LocalDate date) {
	    this.date = date;
	}

	public LocalDate getDate() {
	    if (date == null) {
		date = LocalDate.parse(dateTime);
	    }
	    return date;
	}

    }

    private static final int MINUTES_IN_A_DAY = 60 * 24;

    @SerializedName("activities-steps")
    @Expose
    private List<DateStatistics> dayStatistics = new ArrayList<DateStatistics>();

    @SerializedName("activities-steps-intraday")
    @Expose
    private MinuteActivitySeries minuteActivitySeries;

    public ActivityForDate(LocalDate date, MinuteActivitySeries intradaySet) {
	dayStatistics.add(new DateStatistics(date));
	this.minuteActivitySeries = intradaySet;
    }

    public LocalDate getDate() {
	return dayStatistics.get(0).getDate();
    }

    /**
     * 
     * @return The activitiesLogStepsIntraday
     */
    public MinuteActivitySeries getMinuteActivitySeries() {
	return minuteActivitySeries;
    }

    public boolean isFullDay() {
	return minuteActivitySeries.getElements().size() == MINUTES_IN_A_DAY;
    }

    @Override
    public Iterator<MinuteActivity> iterator() {
	return this.minuteActivitySeries.iterator();
    }
}