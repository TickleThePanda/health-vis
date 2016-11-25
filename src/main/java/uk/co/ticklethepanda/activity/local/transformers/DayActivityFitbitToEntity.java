package uk.co.ticklethepanda.activity.local.transformers;

import uk.co.ticklethepanda.activity.Transformer;
import uk.co.ticklethepanda.activity.fitbit.FitbitIntradayActivity;
import uk.co.ticklethepanda.activity.local.MinuteActivity;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 *
 */
public class DayActivityFitbitToEntity implements Transformer<FitbitIntradayActivity, Set<MinuteActivity>> {
    @Override
    public Set<MinuteActivity> transform(FitbitIntradayActivity input) {

        return input.getIntradayMinuteActivitySeries()
                .getElements()
                .stream()
                .map(fa -> new MinuteActivity(
                        input.getDate(),
                        fa.getTime(),
                        fa.getStepCount()))
                .collect(toSet());
    }
}
