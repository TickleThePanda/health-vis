package uk.co.ticklethepanda.health.activity.stored;


import java.time.LocalTime;
import java.time.Month;

public class MinuteActivityByMonth implements MinuteActivityFacet<Month> {

    private final Month month;
    private final MinuteActivity minuteActivity;

    public MinuteActivityByMonth(Month month, LocalTime time, double averageSteps) {
        this.month = month;
        this.minuteActivity = new MinuteActivity(time, averageSteps);
    }

    public MinuteActivityByMonth(int month, LocalTime time, double averageSteps) {
        this.month = Month.of(month);
        this.minuteActivity = new MinuteActivity(time, averageSteps);
    }

    @Override
    public Month getFacet() {
        return month;
    }

    @Override
    public MinuteActivity getActivity() {
        return minuteActivity;
    }
}
