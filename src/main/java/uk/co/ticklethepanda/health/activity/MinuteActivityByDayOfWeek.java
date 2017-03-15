package uk.co.ticklethepanda.health.activity;


import java.time.DayOfWeek;
import java.time.LocalTime;

public class MinuteActivityByDayOfWeek implements MinuteActivityFacet<DayOfWeek> {

    private final DayOfWeek dayOfWeek;
    private final MinuteActivity minuteActivity;

    public MinuteActivityByDayOfWeek(DayOfWeek dayOfWeek, LocalTime time, double averageSteps) {
        this.dayOfWeek = dayOfWeek;
        this.minuteActivity = new MinuteActivity(time, averageSteps);
    }

    public MinuteActivityByDayOfWeek(int dayOfWeek, LocalTime time, double averageSteps) {
        this.dayOfWeek = DayOfWeek.of(dayOfWeek);
        this.minuteActivity = new MinuteActivity(time, averageSteps);
    }

    @Override
    public DayOfWeek getFacet() {
        return dayOfWeek;
    }

    @Override
    public MinuteActivity getActivity() {
        return minuteActivity;
    }
}
