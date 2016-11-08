package uk.co.ticklethepanda.fitbit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IntradayActivity implements Iterable<MinuteActivity> {

    private static final int MINUTES_IN_A_DAY = 60 * 24;
    @SerializedName("activities-steps")
    @Expose
    private final List<DateStatistics> dayStatistics = new ArrayList<>();
    @SerializedName("activities-steps-intraday")
    @Expose
    private final MinuteActivitySeries minuteActivitySeries;

    public IntradayActivity(LocalDate date, MinuteActivitySeries intradaySet) {
        this.dayStatistics.add(new DateStatistics(date));
        this.minuteActivitySeries = intradaySet;
    }

    public LocalDate getDate() {
        return this.dayStatistics.get(0).getDate();
    }

    /**
     * @return The activitiesLogStepsIntraday
     */
    public MinuteActivitySeries getMinuteActivitySeries() {
        return this.minuteActivitySeries;
    }

    public Double getTotalSteps() {
        return this.minuteActivitySeries.getTotalSteps();
    }

    public boolean isFullDay() {
        return this.minuteActivitySeries.getElements().size() == MINUTES_IN_A_DAY;
    }

    @Override
    public Iterator<MinuteActivity> iterator() {
        return this.minuteActivitySeries.iterator();
    }

    private static class DateStatistics {

        @SerializedName("dateTime")
        @Expose
        private String dateTime;

        private transient LocalDate date;

        public DateStatistics(LocalDate date) {
            this.date = date;
        }

        public LocalDate getDate() {
            if (this.date == null) {
                this.date = LocalDate.parse(this.dateTime);
            }
            return this.date;
        }

    }
}