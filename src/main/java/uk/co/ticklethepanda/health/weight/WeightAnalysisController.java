package uk.co.ticklethepanda.health.weight;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.co.ticklethepanda.health.weight.analysis.AnalysedWeight;
import uk.co.ticklethepanda.health.weight.analysis.WeightAnalysisEngine;
import uk.co.ticklethepanda.health.weight.domain.entities.Weight;
import uk.co.ticklethepanda.health.weight.dtos.analysis.WeightAnalysisForDateDto;
import uk.co.ticklethepanda.health.weight.dtos.log.WeightForDayDto;
import uk.co.ticklethepanda.utility.web.Transformer;

import java.time.LocalDate;
import java.util.List;

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

    @GetMapping(params = {"since"})
    @ResponseBody
    public List<WeightAnalysisForDateDto> getWeightAnalysis(
            @RequestParam("since") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate since
    ) {
        List<Weight> weights = weightService.getWeightWithinDateRange(since, null);

        List<AnalysedWeight> analysedWeights = weightAnalysisEngine.analyse(weights);

        return WEIGHT_TRANSFORMER.transformList(analysedWeights);
    }

    @GetMapping(params = {"recent"})
    @ResponseBody
    public List<WeightAnalysisForDateDto> getRecentWeightAnalysis() {
        List<Weight> weights = weightService.getWeightWithinDateRange(LocalDate.now().minusMonths(1) , null);

        List<AnalysedWeight> analysedWeights = weightAnalysisEngine.analyse(weights);

        return WEIGHT_TRANSFORMER.transformList(analysedWeights);
    }
}
