package uk.co.ticklethepanda.health.activity.fitbit.activity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalTime;

public class FitbitMinuteActivity {

    @SerializedName("time")
    @Expose
    private final LocalTime time;

    @SerializedName("value")
    @Expose
    private final Double numberSteps;

    public FitbitMinuteActivity(LocalTime time, Double value) {
        this.time = time;
        this.numberSteps = value;
    }

    /**
     * @return The value
     */
    public Double getStepCount() {
        return this.numberSteps;
    }

    public LocalTime getTime() {
        return this.time;
    }

    @Override
    public String toString() {
        return "FitbitMinuteActivity [timeString=" + this.time.toString() + ", numberSteps="
                + this.numberSteps + "]\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FitbitMinuteActivity that = (FitbitMinuteActivity) o;

        if (time != null ? !time.equals(that.time) : that.time != null) return false;
        return numberSteps != null ? numberSteps.equals(that.numberSteps) : that.numberSteps == null;

    }

    @Override
    public int hashCode() {
        int result = time != null ? time.hashCode() : 0;
        result = 31 * result + (numberSteps != null ? numberSteps.hashCode() : 0);
        return result;
    }

}