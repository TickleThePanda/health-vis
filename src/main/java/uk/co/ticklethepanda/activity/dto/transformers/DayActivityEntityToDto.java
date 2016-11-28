package uk.co.ticklethepanda.activity.dto.transformers;

import uk.co.ticklethepanda.activity.Transformer;
import uk.co.ticklethepanda.activity.dto.DayActivityDto;
import uk.co.ticklethepanda.activity.dto.MinuteActivityDto;
import uk.co.ticklethepanda.activity.local.MinuteActivity;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
public class DayActivityEntityToDto implements Transformer<Collection<MinuteActivity>, DayActivityDto> {
    @Override
    public DayActivityDto transform(Collection<MinuteActivity> input) {
        LocalDate localDate = MinuteActivity.representsOneDay(input)
                ? input.stream().findFirst().get().getDate()
                : null;

        List<MinuteActivityDto> activities = input
                .stream()
                .map(mae -> new MinuteActivityDto(
                        mae.getTime(),
                        mae.getSteps()))
                .sorted((a, b) -> a.time.compareTo(b.time))
                .collect(Collectors.toList());

        return new DayActivityDto(localDate, activities);
    }
}
