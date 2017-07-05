package uk.co.ticklethepanda.health.weight;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.time.LocalDate;

@Controller
@RequestMapping(value = "/health/weight", params = {"img"})
public class WeightChartController {

    private final WeightChartService weightChartService;

    public WeightChartController(
            @Autowired WeightChartService weightChartService) {
        this.weightChartService = weightChartService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = "image/png")
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

    @RequestMapping(method = RequestMethod.GET, params = {"recent"}, produces = "image/png")
    @ResponseBody
    public byte[] getRecentWeightChart() throws IOException {
        return weightChartService.getRecentWeightChart();
    }

    @RequestMapping(method = RequestMethod.GET, params = {"recent", "no-prediction"}, produces = "image/png")
    @ResponseBody
    public byte[] getRecentWeightChartWithNoPrediction() throws IOException {
        return weightChartService.getRecentWeightChartWithNoPrediction();
    }

}
