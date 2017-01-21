package uk.co.ticklethepanda.health.weight;

import uk.co.ticklethepanda.utility.web.Transformer;

public class WeightTransformers {

    public static final Transformer<Weight, WeightForPeriodDto> WEIGHT_TO_WEIGHT_PERIOD_AM_DTO =
            (input) -> new WeightForPeriodDto(input.getDate(), EntryPeriod.AM, input.getWeightAm());

    public static final Transformer<Weight, WeightForPeriodDto> WEIGHT_TO_WEIGHT_PERIOD_PM_DTO =
            (input) -> new WeightForPeriodDto(input.getDate(), EntryPeriod.PM, input.getWeightPm());

    public static final Transformer<Weight, WeightForDayDto> WEIGHT_TO_WEIGHT_DTO =
            (input) -> new WeightForDayDto(input.getDate(), input.getWeightAm(), input.getWeightPm());
}
