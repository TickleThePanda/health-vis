package uk.co.ticklethepanda.health.activity.controllers.transformers;

import uk.co.ticklethepanda.fitbit.client.model.FitbitIntradayActivity;
import uk.co.ticklethepanda.health.activity.repositories.ActivityEntity;
import uk.co.ticklethepanda.utility.web.Transformer;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 *
 */
public class DayActivityFitbitToEntity implements Transformer<FitbitIntradayActivity, Set<ActivityEntity>> {
    @Override
    public Set<ActivityEntity> transform(FitbitIntradayActivity input) {

        return input.getIntradayMinuteActivitySeries()
                .getElements()
                .stream()
                .map(fa -> new ActivityEntity(
                        input.getDate(),
                        fa.getTime(),
                        fa.getStepCount()))
                .collect(toSet());
    }
}
