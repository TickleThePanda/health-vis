package uk.co.ticklethepanda.health.activity.controllers.transformers;

import uk.co.ticklethepanda.health.activity.repositories.ActivityEntity;
import uk.co.ticklethepanda.health.activity.controllers.dto.DayActivityDto;
import uk.co.ticklethepanda.health.activity.controllers.dto.MinuteActivityDto;
import uk.co.ticklethepanda.utility.web.Transformer;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class DayActivityEntityToDto implements Transformer<List<ActivityEntity>, DayActivityDto> {
    @Override
    public DayActivityDto transform(List<ActivityEntity> input) {
        LocalDate localDate = ActivityEntity.representsOneDay(input)
                ? input.stream().findFirst().get().getDate()
                : null;

        List<MinuteActivityDto> activities = input
                .stream()
                .map(mae -> new MinuteActivityDto(
                        mae.getTime(),
                        mae.getSteps()))
                .sorted(Comparator.comparing(a -> a.time))
                .collect(Collectors.toList());

        return new DayActivityDto(localDate, activities);
    }
}
