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
import uk.co.ticklethepanda.health.weight.analysis.AnalysedWeight;
import uk.co.ticklethepanda.health.weight.analysis.WeightAnalysisEngine;
import uk.co.ticklethepanda.health.weight.domain.entities.Weight;
import uk.co.ticklethepanda.health.weight.dtos.analysis.WeightAnalysisForDateDto;
import uk.co.ticklethepanda.utility.web.Transformer;

import java.time.LocalDate;
import java.util.List;

import static uk.co.ticklethepanda.health.weight.transformers.WeightTransformers.AVERAGE_WEIGHT_TO_DTO;

@Controller
@RequestMapping(value = "/health/weight")
public class WeightAnalysisController {

    private final static Transformer<AnalysedWeight, WeightAnalysisForDateDto> WEIGHT_TRANSFORMER =
            WeightTransformers.ANALYSED_WEIGHT_TO_DTO;

    private final WeightService weightService;
    private final WeightAnalysisEngine weightAnalysisEngine;

    public WeightAnalysisController(
            @Autowired WeightService weightService,
            @Autowired WeightAnalysisEngine weightAnalysisEngine) {
        this.weightService = weightService;
        this.weightAnalysisEngine = weightAnalysisEngine;
    }

    @GetMapping
    @ResponseBody
    public List<WeightAnalysisForDateDto> getWeightAnalysis() {
        List<Weight> weights = weightService.getAllWeight();

        List<AnalysedWeight> analysedWeights = weightAnalysisEngine.analyse(weights);

        return WEIGHT_TRANSFORMER.transformList(analysedWeights);
    }

    @GetMapping(params="period")
    @ResponseBody
    public List<AverageWeightDto> getWeightForPeriod(
            @RequestParam("period") int periodInDays
    ) {
        List<AverageWeight> averageWeightForPeriods =
                weightService.getAverageWeightForEachPeriod(periodInDays);

        return AVERAGE_WEIGHT_TO_DTO.transformList(averageWeightForPeriods);
    }
}
