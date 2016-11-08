package uk.co.ticklethepanda.activity.dto.transformers;

import uk.co.ticklethepanda.activity.Transformer;
import uk.co.ticklethepanda.activity.dto.DayActivityDto;
import uk.co.ticklethepanda.activity.dto.MinuteActivityDto;
import uk.co.ticklethepanda.activity.local.DayActivity;

import java.util.TreeSet;

/**
 * Created by panda on 08/11/2016.
 */
public class DayActivityEntityToDto implements Transformer<DayActivity, DayActivityDto> {
    @Override
    public DayActivityDto transform(DayActivity input) {
        TreeSet<MinuteActivityDto> activities = new TreeSet<>((a, b) -> a.time.compareTo(b.time));

        input.getMinuteActivityEntities().forEach(mae -> {
            MinuteActivityDto ma = new MinuteActivityDto(
                    mae.getTime(),
                    mae.getSteps());

            activities.add(ma);
        });

        return new DayActivityDto(input.getDate(), activities);
    }
}
