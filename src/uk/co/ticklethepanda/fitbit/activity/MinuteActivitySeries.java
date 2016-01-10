package uk.co.ticklethepanda.fitbit.activity;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MinuteActivitySeries implements Iterable<MinuteActivity> {

  public static MinuteActivitySeries fromMap(Map<LocalTime, Double> map) {
    return new MinuteActivitySeries(
        map.keySet().stream().map(a -> new MinuteActivity(a, map.get(a)))
            .sorted((a, b) -> a.getTime().compareTo(b.getTime()))
            .collect(Collectors.toList()));
  }

  @SerializedName("dataset")
  @Expose
  private List<MinuteActivity> dataset;

  public MinuteActivitySeries() {
    dataset = new ArrayList<MinuteActivity>();
  }

  public MinuteActivitySeries(List<MinuteActivity> activity) {
    dataset = new ArrayList<MinuteActivity>(activity);
  }

  public MinuteActivity getByLocalTime(LocalTime plusMinutes) {
    return dataset.stream().filter(a -> a.getTime().equals(plusMinutes))
        .findFirst().get();
  }

  /**
   * 
   * @return The dataset
   */
  public List<MinuteActivity> getElements() {
    return dataset;
  }

  public Double getTotalSteps() {
    return dataset.stream().mapToDouble(MinuteActivity::getStepCount).sum();
  }

  public Iterator<MinuteActivity> iterator() {
    return dataset.iterator();
  }

  /**
   * 
   * @param dataset
   *          The dataset
   */
  public void setDataset(List<MinuteActivity> dataset) {
    this.dataset = dataset;
  }

}