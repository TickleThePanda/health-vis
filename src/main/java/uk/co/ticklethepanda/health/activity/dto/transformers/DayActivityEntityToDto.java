package uk.co.ticklethepanda.health.activity.dto.transformers;

import uk.co.ticklethepanda.health.activity.domain.MinuteActivity;
import uk.co.ticklethepanda.health.activity.dto.DayActivityDto;
import uk.co.ticklethepanda.health.activity.dto.MinuteActivityDto;
import uk.co.ticklethepanda.utility.web.Transformer;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class DayActivityEntityToDto implements Transformer<List<MinuteActivity>, DayActivityDto> {
    @Override
    public DayActivityDto transform(List<MinuteActivity> input) {
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
