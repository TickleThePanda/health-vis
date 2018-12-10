package uk.co.ticklethepanda.health.weight.transformers;

import uk.co.ticklethepanda.health.weight.analysis.AnalysedWeight;
import uk.co.ticklethepanda.health.weight.domain.model.AverageWeight;
import uk.co.ticklethepanda.health.weight.domain.model.EntryMeridiemPeriod;
import uk.co.ticklethepanda.health.weight.domain.entities.Weight;
import uk.co.ticklethepanda.health.weight.dtos.analysis.WeightAnalysisForDateDto;
import uk.co.ticklethepanda.health.weight.dtos.log.AverageWeightDto;
import uk.co.ticklethepanda.health.weight.dtos.log.WeightForDayDto;
import uk.co.ticklethepanda.health.weight.dtos.log.WeightForMeridiemPeriodDto;
import uk.co.ticklethepanda.utility.web.Transformer;

public class WeightTransformers {

    public static final Transformer<Weight, WeightForMeridiemPeriodDto> WEIGHT_TO_WEIGHT_PERIOD_AM_DTO =
            (input) -> new WeightForMeridiemPeriodDto(input.getDate(), EntryMeridiemPeriod.AM, input.getWeightAm());

    public static final Transformer<Weight, WeightForMeridiemPeriodDto> WEIGHT_TO_WEIGHT_PERIOD_PM_DTO =
            (input) -> new WeightForMeridiemPeriodDto(input.getDate(), EntryMeridiemPeriod.PM, input.getWeightPm());

    public static final Transformer<Weight, WeightForDayDto> WEIGHT_TO_WEIGHT_DTO =
            (input) -> new WeightForDayDto(input.getDate(), input.getWeightAm(), input.getWeightPm());

    public static final Transformer<AnalysedWeight, WeightAnalysisForDateDto> ANALYSED_WEIGHT_TO_DTO =
            (input) -> new WeightAnalysisForDateDto(input.getDate(), input.getWeight());

    public static WeightForMeridiemPeriodDto transformToPeriod(Weight weight, EntryMeridiemPeriod entryMeridiemPeriod) {
        switch (entryMeridiemPeriod) {
            case AM:
                return WEIGHT_TO_WEIGHT_PERIOD_AM_DTO.transform(weight);
            case PM:
                return WEIGHT_TO_WEIGHT_PERIOD_PM_DTO.transform(weight);
            default:
                throw new IllegalArgumentException(
                        "period and date where \"/health/weight/{date}/{period}\" cannot be null");
        }
    }

    public static final Transformer<AverageWeight, AverageWeightDto> AVERAGE_WEIGHT_TO_DTO =
            (input) -> new AverageWeightDto(
                    input.getPeriodStart(),
                    input.getPeriodEnd(),
                    input.getCount(),
                    input.getAverage());
}
