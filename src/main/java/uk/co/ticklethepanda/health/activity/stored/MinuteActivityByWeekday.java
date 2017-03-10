package uk.co.ticklethepanda.health.activity.stored;


import java.time.DayOfWeek;
import java.time.LocalTime;

public class MinuteActivityByWeekday implements MinuteActivityFacet<DayOfWeek> {

    private final DayOfWeek dayOfWeek;
    private final MinuteActivity minuteActivity;

    public MinuteActivityByWeekday(DayOfWeek dayOfWeek, LocalTime time, double averageSteps) {
        this.dayOfWeek = dayOfWeek;
        this.minuteActivity = new MinuteActivity(time, averageSteps);
    }

    public MinuteActivityByWeekday(int dayOfWeek, LocalTime time, double averageSteps) {
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
