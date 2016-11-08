package uk.co.ticklethepanda.activity.local.transformers;

import uk.co.ticklethepanda.activity.Transformer;
import uk.co.ticklethepanda.activity.local.DayActivity;
import uk.co.ticklethepanda.activity.local.MinuteActivity;
import uk.co.ticklethepanda.activity.fitbit.FitbitIntradayActivity;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by panda on 08/11/2016.
 */
public class DayActivityFitbitToEntity implements Transformer<FitbitIntradayActivity, DayActivity> {
    @Override
    public DayActivity transform(FitbitIntradayActivity input) {
        DayActivity dayActivity = new DayActivity();
        dayActivity.setDate(input.getDate());

        Set<MinuteActivity> activities = new TreeSet<>((a, b) -> a.getTime().compareTo(b.getTime()));
        input.getIntradayMinuteActivitySeries().getElements().forEach(fitbitMinuteActivity -> {
            MinuteActivity minuteActivity = new MinuteActivity();
            minuteActivity.setSteps(fitbitMinuteActivity.getStepCount().intValue());
            minuteActivity.setTime(fitbitMinuteActivity.getTime());
            minuteActivity.setDayActivity(dayActivity);

            activities.add(minuteActivity);
        });

        dayActivity.setMinuteActivityEntities(activities);

        return dayActivity;
    }
}
