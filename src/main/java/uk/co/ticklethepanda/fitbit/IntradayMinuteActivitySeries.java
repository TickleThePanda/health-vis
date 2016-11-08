package uk.co.ticklethepanda.fitbit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IntradayMinuteActivitySeries implements Iterable<IntradayMinuteActivity> {

    @SerializedName("dataset")
    @Expose
    private List<IntradayMinuteActivity> dataset;

    public IntradayMinuteActivitySeries() {
        this.dataset = new ArrayList<>();
    }

    public IntradayMinuteActivitySeries(List<IntradayMinuteActivity> activity) {
        this.dataset = new ArrayList<>(activity);
    }

    public static IntradayMinuteActivitySeries fromMap(Map<LocalTime, Double> map) {
        return new IntradayMinuteActivitySeries(
                map.keySet().stream().map(a -> new IntradayMinuteActivity(a, map.get(a)))
                        .sorted((a, b) -> a.getTime().compareTo(b.getTime()))
                        .collect(Collectors.toList()));
    }

    public IntradayMinuteActivity getByLocalTime(LocalTime plusMinutes) {
        return this.dataset.stream().filter(a -> a.getTime().equals(plusMinutes))
                .findFirst().get();
    }

    /**
     * @return The dataset
     */
    public List<IntradayMinuteActivity> getElements() {
        return this.dataset;
    }

    public Double getTotalSteps() {
        return this.dataset.stream().mapToDouble(IntradayMinuteActivity::getStepCount).sum();
    }

    @Override
    public Iterator<IntradayMinuteActivity> iterator() {
        return this.dataset.iterator();
    }

    /**
     * @param dataset The dataset
     */
    public void setDataset(List<IntradayMinuteActivity> dataset) {
        this.dataset = dataset;
    }

}