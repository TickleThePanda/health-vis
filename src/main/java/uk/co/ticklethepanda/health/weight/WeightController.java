package uk.co.ticklethepanda.health.weight;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.co.ticklethepanda.health.weight.domain.entities.EntryPeriod;
import uk.co.ticklethepanda.health.weight.domain.entities.Weight;
import uk.co.ticklethepanda.health.weight.dtos.*;
import uk.co.ticklethepanda.utility.web.Transformer;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.util.Comparator.comparingDouble;
import static uk.co.ticklethepanda.health.weight.WeightTransformers.WEIGHT_TO_WEIGHT_DTO;
import static uk.co.ticklethepanda.health.weight.WeightTransformers.transformToPeriod;

@Controller
@RequestMapping(value = "/health/weight")
public class WeightController {

    private final WeightService weightService;
    private final WeightChartService weightChartService;

    private final static Transformer<Weight, WeightForDayDto> WEIGHT_TRANSFORMER =
            WeightTransformers.WEIGHT_TO_WEIGHT_DTO;

    public WeightController(
            @Autowired WeightService weightService,
            @Autowired WeightChartService weightChartService) {
        this.weightService = weightService;
        this.weightChartService = weightChartService;
    }

    @GetMapping()
    @ResponseBody
    public List<WeightForDayDto> getWeight() {
        return WEIGHT_TO_WEIGHT_DTO.transformList(weightService.getAllWeight());
    }

    @PutMapping(value = "/{date}/{period}")
    @ResponseBody
    public WeightForPeriodDto saveWeightForDate(
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable("period") EntryPeriod entryPeriod,
            @RequestBody WeightValueDto weightInput) {
        Weight weight = weightService.createWeightEntryForPeriod(date, entryPeriod, weightInput.weight);
        weightChartService.triggerCache();
        return transformToPeriod(weight, entryPeriod);
    }

    @GetMapping(value = "/{date}/{period}")
    @ResponseBody
    public WeightForPeriodDto saveWeightForDate(
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable("period") EntryPeriod entryPeriod) {
        Weight weight = weightService.getWeightForDate(date);
        return transformToPeriod(weight, entryPeriod);
    }

    @GetMapping(value = "/prediction")
    @ResponseBody
    public PredictedWeightLossDto predictWeightLoss() {
        List<Weight> weights = weightService.getAllWeight();

        return predictWeightLoss(weights);

    }

    @GetMapping(value = "/prediction", params = {"since"})
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
