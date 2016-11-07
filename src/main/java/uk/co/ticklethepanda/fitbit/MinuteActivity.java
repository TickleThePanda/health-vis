package uk.co.ticklethepanda.fitbit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalTime;

public class MinuteActivity {

  @SerializedName("time")
  @Expose
  private final String timeString;

  @SerializedName("value")
  @Expose
  private final Double numberSteps;

  public MinuteActivity(LocalTime time, Double value) {
    this.timeString = time.toString();
    this.numberSteps = value;
  }

  /**
   *
   * @return The value
   */
  public Double getStepCount() {
    return this.numberSteps;
  }

  public LocalTime getTime() {
    return LocalTime.parse(this.timeString);
  }

  @Override
  public String toString() {
    return "MinuteActivity [timeString=" + this.timeString + ", numberSteps="
        + this.numberSteps + "]\n";
  }

}