package uk.co.ticklethepanda.health.weight;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static uk.co.ticklethepanda.health.weight.WeightTransformers.WEIGHT_TO_WEIGHT_DTO;
import static uk.co.ticklethepanda.health.weight.WeightTransformers.transformToPeriod;

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
        return WEIGHT_TO_WEIGHT_DTO.transformList(weightService.getAllWeight());
    }

    @RequestMapping(method = RequestMethod.GET, params = {"img", "recent"}, produces = "image/png")
    @ResponseBody
    public byte[] getRecentWeightChart() throws IOException {
        return weightChartService.getRecentWeightChart();
    }

    @RequestMapping(method = RequestMethod.GET, params = {"img", "recent", "no-prediction"}, produces = "image/png")
    @ResponseBody
    public byte[] getRecentWeightChartWithNoPrediction() throws IOException {
        return weightChartService.getRecentWeightChartWithNoPrediction();
    }

    @RequestMapping(method = RequestMethod.GET, params = {"img"}, produces = "image/png")
    @ResponseBody
    public byte[] getWeightChart(
            @RequestParam(value = "after", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate startDate,
            @RequestParam(value = "before", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate endDate)
            throws IOException {

        if (startDate == null && endDate == null) {
            return weightChartService.getWeightChart();
        }

        return weightChartService.getChartBetweenDates(startDate, endDate);
    }

    @RequestMapping(value = "/{date}/{period}",
            method = RequestMethod.PUT)
    @ResponseBody
    public WeightForPeriodDto saveWeightForDate(
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable("period") EntryPeriod entryPeriod,
            @RequestBody WeightValueDto weightInput) {
        Weight weight = weightService.createWeightEntryForPeriod(date, entryPeriod, weightInput.weight);
        weightChartService.triggerCache();
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

}
