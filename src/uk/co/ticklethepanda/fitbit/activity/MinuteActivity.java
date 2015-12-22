package uk.co.ticklethepanda.fitbit.activity;

import java.time.LocalTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MinuteActivity {

    @SerializedName("time")
    @Expose
    private String timeString;

    @SerializedName("value")
    @Expose
    private Double numberSteps;

    public LocalTime getTime() {
	return LocalTime.parse(timeString);
    }

    public MinuteActivity(LocalTime time, Double value) {
	this.timeString = time.toString();
	this.numberSteps = value;
    }

    /**
     * 
     * @return The value
     */
    public Double getStepCount() {
	return numberSteps;
    }

    @Override
    public String toString() {
	return "MinuteActivity [timeString=" + timeString + ", numberSteps="
		+ numberSteps + "]\n";
    }

}