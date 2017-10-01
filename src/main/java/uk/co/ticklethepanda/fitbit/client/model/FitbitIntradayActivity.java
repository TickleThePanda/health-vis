package uk.co.ticklethepanda.fitbit.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FitbitIntradayActivity implements Iterable<FitbitMinuteActivity> {

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

    @Override
    public Iterator<FitbitMinuteActivity> iterator() {
        return this.intradayMinuteActivitySeries.iterator();
    }

    @Override
    public String toString() {
        return "FitbitIntradayActivity{" +
                "dayStatistics=" + dayStatistics.toString() +
                ", intradayMinuteActivitySeries=" + intradayMinuteActivitySeries.toString() +
                '}';
    }

    private static class DateStatistics {

        @SerializedName("dateTime")
        @Expose
        private LocalDate date;

        public DateStatistics(LocalDate date) {
            this.date = date;
        }

        public LocalDate getDate() {
            return this.date;
        }

        @Override
        public String toString() {
            return "DateStatistics{" +
                    "date=" + date.toString() +
                    '}';
        }
    }
}