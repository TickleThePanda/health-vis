package uk.co.ticklethepanda.health.weight;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.co.ticklethepanda.health.weight.domain.entities.Weight;
import uk.co.ticklethepanda.health.weight.dtos.prediction.PredictedWeightLossDto;
import uk.co.ticklethepanda.health.weight.dtos.prediction.PredictedWeightLossToTargetDto;
import uk.co.ticklethepanda.health.weight.dtos.log.WeightForDayDto;
import uk.co.ticklethepanda.utility.web.Transformer;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.util.Comparator.comparingDouble;

@Controller
@RequestMapping(value = "/health/weight/prediction")
public class WeightPredictionController {

    private final WeightService weightService;

    private final static Transformer<Weight, WeightForDayDto> WEIGHT_TRANSFORMER =
            WeightTransformers.WEIGHT_TO_WEIGHT_DTO;

    public WeightPredictionController(
            @Autowired WeightService weightService) {
        this.weightService = weightService;
    }

    @GetMapping
    @ResponseBody
    public PredictedWeightLossDto predictWeightLoss() {
        List<Weight> weights = weightService.getAllWeight();

        return predictWeightLoss(weights);

    }

    @GetMapping(params = {"since"})
    @ResponseBody
    public PredictedWeightLossDto predictWeightLoss(
            @RequestParam("since") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<Weight> weights = weightService.getWeightWithinDateRange(date, null);

        return predictWeightLoss(weights);

    }

    private PredictedWeightLossDto predictWeightLoss(List<Weight> weights) {
        Weight heaviest = weights.stream()
                .max(comparingDouble(Weight::getAverage))
                .get();
        Weight lightest = weights.stream()
                .min(comparingDouble(Weight::getAverage))
                .get();
        Weight latest = weightService.getMostRecent();

        double daysBetween = ChronoUnit.DAYS.between(heaviest.getDate(), lightest.getDate());

        double lossPerDay = (heaviest.getAverage() - latest.getAverage()) / daysBetween;

        double expectedTimeToTarget =
                (latest.getAverage() - weightService.getWeightTarget())
                        / lossPerDay;

        double expectedTimeToIntermedate =
                (latest.getAverage() - weightService.getIntermediateWeightTargetForWeight(latest))
                        / lossPerDay;

        return new PredictedWeightLossDto(
                WEIGHT_TRANSFORMER.transform(heaviest),
                WEIGHT_TRANSFORMER.transform(lightest),
                WEIGHT_TRANSFORMER.transform(latest),
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
