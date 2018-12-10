package uk.co.ticklethepanda.health.weight.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.co.ticklethepanda.health.weight.WeightService;
import uk.co.ticklethepanda.health.weight.domain.model.AverageWeight;
import uk.co.ticklethepanda.health.weight.dtos.log.AverageWeightDto;
import uk.co.ticklethepanda.health.weight.transformers.WeightTransformers;
import uk.co.ticklethepanda.health.weight.domain.entities.Weight;
import uk.co.ticklethepanda.health.weight.dtos.prediction.PredictedWeightLossDto;
import uk.co.ticklethepanda.health.weight.dtos.prediction.PredictedWeightLossToTargetDto;
import uk.co.ticklethepanda.health.weight.dtos.log.WeightForDayDto;
import uk.co.ticklethepanda.utility.web.Transformer;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingDouble;

@Controller
@RequestMapping(value = "/health/weight/prediction")
public class WeightPredictionController {

    private final WeightService weightService;

    private final static Transformer<Weight, WeightForDayDto> WEIGHT_TRANSFORMER =
            WeightTransformers.WEIGHT_TO_WEIGHT_DTO;
    private final static Transformer<AverageWeight, AverageWeightDto> AVERAGE_WEIGHT_TRANSFORMER =
            WeightTransformers.AVERAGE_WEIGHT_TO_DTO;

    public WeightPredictionController(
            @Autowired WeightService weightService) {
        this.weightService = weightService;
    }

    @GetMapping
    @ResponseBody
    public PredictedWeightLossDto predictWeightLoss() {
        List<AverageWeight> weights = weightService.getAverageWeightForEachPeriod(7);

        return predictWeightLoss(weights);

    }

    @GetMapping(params = {"since"})
    @ResponseBody
    public PredictedWeightLossDto predictWeightLoss(
            @RequestParam("since") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<AverageWeight> weights = weightService.getAverageWeightForEachPeriodInRange(7, date, null);

        return predictWeightLoss(weights);

    }

    private PredictedWeightLossDto predictWeightLoss(List<AverageWeight> weights) {
        AverageWeight heaviest = weights.stream()
                .max(comparingDouble(AverageWeight::getAverage))
                .get();
        AverageWeight lightest = weights.stream()
                .min(comparingDouble(AverageWeight::getAverage))
                .get();
        AverageWeight latest = weights.stream()
                .max(comparing(AverageWeight::getPeriodStart))
                .get();

        double daysBetween = ChronoUnit.DAYS.between(heaviest.getPeriodStart(), lightest.getPeriodStart());

        double lossPerDay = (heaviest.getAverage() - latest.getAverage()) / daysBetween;

        double expectedTimeToTarget =
                (latest.getAverage() - weightService.getWeightTarget())
                        / lossPerDay;

        double expectedTimeToIntermedate =
                (latest.getAverage() - weightService.getIntermediateWeightTargetForWeight(latest))
                        / lossPerDay;

        return new PredictedWeightLossDto(
                AVERAGE_WEIGHT_TRANSFORMER.transform(heaviest),
                AVERAGE_WEIGHT_TRANSFORMER.transform(lightest),
                AVERAGE_WEIGHT_TRANSFORMER.transform(latest),
                lossPerDay,
                new PredictedWeightLossToTargetDto(
                        weightService.getWeightTarget(),
                        expectedTimeToTarget
                ),
                new PredictedWeightLossToTargetDto(
                        weightService.getIntermediateWeightTargetForWeight(latest),
                        expectedTimeToIntermedate
                )
        );
    }
}
