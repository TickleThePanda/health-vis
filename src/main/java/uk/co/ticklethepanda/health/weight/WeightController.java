package uk.co.ticklethepanda.health.weight;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.http.client.fluent.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.co.ticklethepanda.health.ChartConfig;
import uk.co.ticklethepanda.utility.image.PngToByteArray;
import uk.co.ticklethepanda.utility.web.Transformer;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Controller
@RequestMapping(value = "/health/weight")
public class WeightController {

    private static final Logger LOG = LogManager.getLogger();
    private final WeightService weightService;
    private final String healthUrl;

    public WeightController(
            @Autowired WeightService weightService,
            @Value("${health.spreadsheet.url}") String healthUrl) {
        this.weightService = weightService;
        this.healthUrl = healthUrl;
    }

    @Scheduled(initialDelay = 0, fixedRate = 1000 * 60 * 60)
    public void cacheWeightData() throws IOException {
        LOG.info("caching weight data");
        String response = Request.Get(healthUrl)
                .execute()
                .returnContent()
                .asString();

        Reader in = new StringReader(response);
        CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader());
        List<CSVRecord> csvRecords = parser.getRecords();

        for(CSVRecord r : csvRecords) {
            String dateText = r.get("Date");
            String weightAmText = r.get("Weight AM");
            String weightPmText = r.get("Weight PM");

            LocalDate localDate = LocalDate.parse(dateText);
            Double weightAm = weightAmText.equals("") ? null : Double.parseDouble(weightAmText);
            Double weightPm = weightPmText.equals("") ? null : Double.parseDouble(weightPmText);

            weightService.createWeightEntry(new Weight(localDate, weightAm, weightPm));
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<WeightDto> getWeight() {
        return weightService
                .getAllWeight()
                .stream()
                .map(weight -> new WeightDto(weight.getDate(), weight.getWeightAm(), weight.getWeightPm()))
                .collect(Collectors.toList());
    }



    @RequestMapping(method = RequestMethod.GET, params = {"img"}, produces = "image/png")
    @ResponseBody
    public byte[] getWeightChart() throws IOException {
        LocalDate today = LocalDate.now();

        List<Double> yData = weightService.getAllWeight()
                .stream()
                .map(w -> {
                    Double weightAm = w.getWeightAm();
                    Double weightPm = w.getWeightPm();
                    if(weightAm != null && weightPm != null) {
                        return (weightAm + weightPm) / 2.0;
                    } else if (weightAm != null) {
                        return weightAm;
                    } else if (weightPm != null) {
                        return weightPm;
                    }
                    return null;
                })
                .collect(Collectors.toList());

        List<Date> xData = weightService.getAllWeight().stream()
                .map(w -> Date.from(w.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .collect(Collectors.toList());

        XYChart chart = new XYChartBuilder()
                .width(1000)
                .height(500)
                .xAxisTitle("Time of Day")
                .yAxisTitle("Steps")
                .theme(Styler.ChartTheme.GGPlot2)
                .build();

        Font font = chart.getStyler().getAxisTickLabelsFont();

        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setAxisTickLabelsFont(font.deriveFont(
                Collections.singletonMap(
                        TextAttribute.WEIGHT, TextAttribute.WEIGHT_LIGHT)));
        chart.getStyler().setDatePattern("HH:mm");
        chart.getStyler().setChartPadding(ChartConfig.CHART_PADDING);
        chart.getStyler().setMarkerSize(4);

        XYSeries series = chart.addSeries("data", xData, yData);

        series.setMarker(SeriesMarkers.CIRCLE);

        BufferedImage bufferedImage = new BufferedImage(chart.getWidth(), chart.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        chart.paint(graphics2D, chart.getWidth(), chart.getHeight());

        return PngToByteArray.convert(bufferedImage);
    }
}
