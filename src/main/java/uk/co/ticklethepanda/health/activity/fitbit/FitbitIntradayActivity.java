package uk.co.ticklethepanda.health.activity.fitbit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FitbitIntradayActivity implements Iterable<FitbitMinuteActivity> {

    private static final int MINUTES_IN_A_DAY = 60 * 24;
    @SerializedName("activities-steps")
    @Expose
    private final List<DateStatistics> dayStatistics = new ArrayList<>();
    @SerializedName("activities-steps-intraday")
    @Expose
    private final FitbitMinuteActivitySeries intradayMinuteActivitySeries;

    public FitbitIntradayActivity(LocalDate date, FitbitMinuteActivitySeries intradaySet) {
        this.dayStatistics.add(new DateStatistics(date));
        this.intradayMinuteActivitySeries = intradaySet;
    }

    public LocalDate getDate() {
        return this.dayStatistics.get(0).getDate();
    }

    /**
     * @return The activitiesLogStepsIntraday
     */
    public FitbitMinuteActivitySeries getIntradayMinuteActivitySeries() {
        return this.intradayMinuteActivitySeries;
    }

    public Double getTotalSteps() {
        return this.intradayMinuteActivitySeries.getTotalSteps();
    }

    public boolean isFullDay() {
        return this.intradayMinuteActivitySeries.getElements().size() == MINUTES_IN_A_DAY;
    }

    @Override
    public Iterator<FitbitMinuteActivity> iterator() {
        return this.intradayMinuteActivitySeries.iterator();
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