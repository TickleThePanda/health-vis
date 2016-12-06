package uk.co.ticklethepanda.health.activity.fitbit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FitbitMinuteActivitySeries implements Iterable<FitbitMinuteActivity> {

    @SerializedName("dataset")
    @Expose
    private List<FitbitMinuteActivity> dataset;

    public FitbitMinuteActivitySeries() {
        this.dataset = new ArrayList<>();
    }

    public FitbitMinuteActivitySeries(List<FitbitMinuteActivity> activity) {
        this.dataset = new ArrayList<>(activity);
    }

    public FitbitMinuteActivity getByLocalTime(LocalTime plusMinutes) {
        return this.dataset.stream().filter(a -> a.getTime().equals(plusMinutes))
                .findFirst().get();
    }

    /**
     * @return The dataset
     */
    public List<FitbitMinuteActivity> getElements() {
        return this.dataset;
    }

    public Double getTotalSteps() {
        return this.dataset.stream().mapToDouble(FitbitMinuteActivity::getStepCount).sum();
    }

    @Override
    public Iterator<FitbitMinuteActivity> iterator() {
        return this.dataset.iterator();
    }

    /**
     * @param dataset The dataset
     */
    public void setDataset(List<FitbitMinuteActivity> dataset) {
        this.dataset = dataset;
    }

}