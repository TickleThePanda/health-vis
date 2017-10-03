package uk.co.ticklethepanda.health.weight;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.co.ticklethepanda.health.weight.domain.entities.EntryPeriod;
import uk.co.ticklethepanda.health.weight.domain.entities.Weight;
import uk.co.ticklethepanda.health.weight.dtos.log.WeightForDayDto;
import uk.co.ticklethepanda.health.weight.dtos.log.WeightForPeriodDto;
import uk.co.ticklethepanda.health.weight.dtos.log.WeightValueDto;
import uk.co.ticklethepanda.utility.web.Transformer;

import java.time.LocalDate;
import java.util.List;

import static uk.co.ticklethepanda.health.weight.WeightTransformers.WEIGHT_TO_WEIGHT_DTO;
import static uk.co.ticklethepanda.health.weight.WeightTransformers.transformToPeriod;

@Controller
@RequestMapping(value = "/health/weight/log")
public class WeightLogController {

    private final WeightService weightService;

    private final static Transformer<Weight, WeightForDayDto> WEIGHT_TRANSFORMER =
            WeightTransformers.WEIGHT_TO_WEIGHT_DTO;

    public WeightLogController(
            @Autowired WeightService weightService) {
        this.weightService = weightService;
    }

    @GetMapping
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


}
