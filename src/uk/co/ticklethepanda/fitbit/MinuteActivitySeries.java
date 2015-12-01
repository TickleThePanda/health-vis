package uk.co.ticklethepanda.fitbit;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MinuteActivitySeries implements Iterable<MinuteActivity> {

    @SerializedName("dataset")
    @Expose
    private List<MinuteActivity> dataset;

    public MinuteActivitySeries(List<MinuteActivity> activity) {
	dataset = new ArrayList<MinuteActivity>(activity);
    }

    public MinuteActivitySeries() {
	dataset = new ArrayList<MinuteActivity>();
    }

    /**
     * 
     * @return The dataset
     */
    public List<MinuteActivity> getElements() {
	return dataset;
    }

    /**
     * 
     * @param dataset
     *            The dataset
     */
    public void setDataset(List<MinuteActivity> dataset) {
	this.dataset = dataset;
    }

    public Iterator<MinuteActivity> iterator() {
	return dataset.iterator();
    }

    public Double getTotalSteps() {
	return dataset.stream().mapToDouble(MinuteActivity::getStepCount).sum();
    }

    public static MinuteActivitySeries fromMap(Map<LocalTime, Double> map) {
	return new MinuteActivitySeries(
		map.keySet().stream().map(a -> new MinuteActivity(a, map.get(a)))
			.sorted((a, b) -> a.getTime().compareTo(b.getTime()))
			.collect(Collectors.toList()));
    }

    public MinuteActivity getByLocalTime(LocalTime plusMinutes) {
	return dataset.stream().filter(a -> a.getTime().equals(plusMinutes))
		.findFirst().get();
    }

}