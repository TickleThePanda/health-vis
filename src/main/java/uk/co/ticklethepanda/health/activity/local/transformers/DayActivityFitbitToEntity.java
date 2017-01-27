package uk.co.ticklethepanda.health.activity.local.transformers;

import uk.co.ticklethepanda.health.activity.fitbit.activity.FitbitIntradayActivity;
import uk.co.ticklethepanda.health.activity.local.MinuteActivity;
import uk.co.ticklethepanda.utility.web.Transformer;

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
