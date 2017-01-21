package uk.co.ticklethepanda.health.weight;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static uk.co.ticklethepanda.health.weight.WeightTransformers.*;

@Controller
@RequestMapping(value = "/health/weight")
public class WeightController {

    private final WeightService weightService;
    private final WeightChartService weightChartService;

    public WeightController(
            @Autowired WeightService weightService,
            @Autowired WeightChartService weightChartService) {
        this.weightService = weightService;
        this.weightChartService = weightChartService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<WeightForDayDto> getWeight() {
        return weightService
                .getAllWeight()
                .stream()
                .map(WEIGHT_TO_WEIGHT_DTO::transform)
                .collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET, params = {"img", "recent"}, produces = "image/png")
    @ResponseBody
    public byte[] getRecentWeightChart() throws IOException {
        return weightChartService.getRecentWeightChart();
    }

    @RequestMapping(method = RequestMethod.GET, params = {"img"}, produces = "image/png")
    @ResponseBody
    public byte[] getWeightChart() throws IOException {
        return weightChartService.getWeightChart();
    }

    @RequestMapping(value = "/{date}/{period}", method = RequestMethod.PUT)
    @ResponseBody
    public WeightForPeriodDto saveWeightForDate(
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable("period") EntryPeriod entryPeriod,
            @RequestBody WeightValueDto weightInput) {
        Weight weight = weightService.createWeightEntryForPeriod(date, entryPeriod, weightInput.weight);
        return transformToPeriod(weight, entryPeriod);
    }

    @RequestMapping(value = "/{date}/{period}", method = RequestMethod.GET)
    @ResponseBody
    public WeightForPeriodDto saveWeightForDate(
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable("period") EntryPeriod entryPeriod) {
        Weight weight = weightService.getWeightForDate(date);
        return transformToPeriod(weight, entryPeriod);
    }

    private WeightForPeriodDto transformToPeriod(Weight weight, EntryPeriod entryPeriod) {
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
