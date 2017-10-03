package uk.co.ticklethepanda.health.weight;

import uk.co.ticklethepanda.health.weight.analysis.AnalysedWeight;
import uk.co.ticklethepanda.health.weight.domain.entities.EntryPeriod;
import uk.co.ticklethepanda.health.weight.domain.entities.Weight;
import uk.co.ticklethepanda.health.weight.dtos.analysis.WeightAnalysisForDateDto;
import uk.co.ticklethepanda.health.weight.dtos.log.WeightForDayDto;
import uk.co.ticklethepanda.health.weight.dtos.log.WeightForPeriodDto;
import uk.co.ticklethepanda.utility.web.Transformer;

public class WeightTransformers {

    public static final Transformer<Weight, WeightForPeriodDto> WEIGHT_TO_WEIGHT_PERIOD_AM_DTO =
            (input) -> new WeightForPeriodDto(input.getDate(), EntryPeriod.AM, input.getWeightAm());

    public static final Transformer<Weight, WeightForPeriodDto> WEIGHT_TO_WEIGHT_PERIOD_PM_DTO =
            (input) -> new WeightForPeriodDto(input.getDate(), EntryPeriod.PM, input.getWeightPm());

    public static final Transformer<Weight, WeightForDayDto> WEIGHT_TO_WEIGHT_DTO =
            (input) -> new WeightForDayDto(input.getDate(), input.getWeightAm(), input.getWeightPm());

    public static final Transformer<AnalysedWeight, WeightAnalysisForDateDto> ANALYSED_WEIGHT_TO_DTO =
            (input) -> new WeightAnalysisForDateDto(input.getDate(), input.getWeight());

    public static WeightForPeriodDto transformToPeriod(Weight weight, EntryPeriod entryPeriod) {
        switch (entryPeriod) {
            case AM:
                return WEIGHT_TO_WEIGHT_PERIOD_AM_DTO.transform(weight);
            case PM:
                return WEIGHT_TO_WEIGHT_PERIOD_PM_DTO.transform(weight);
            default:
                throw new IllegalArgumentException(
                        "period and date where \"/health/weight/{date}/{period}\" cannot be null");
        }
    }
}
