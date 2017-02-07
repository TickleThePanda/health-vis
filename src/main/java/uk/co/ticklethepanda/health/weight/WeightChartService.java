package uk.co.ticklethepanda.health.weight;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uk.co.ticklethepanda.health.ChartConfig;
import uk.co.ticklethepanda.utility.image.PngToByteArray;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static uk.co.ticklethepanda.health.weight.AveragedWeight.calculateAverageWeighs;

@Service
public class WeightChartService {

    private static final Logger LOG = LogManager.getLogger();

    private final WeightService weightService;
    private byte[] weightChart;
    private byte[] recentWeightChart;

    public WeightChartService(@Autowired WeightService weightService) {
        this.weightService = weightService;

    }

    public byte[] getWeightChart() throws IOException {
        if (weightChart == null) {
            cacheWeightChart();
        }

        return weightChart;
    }

    public byte[] getRecentWeightChart() throws IOException {
        if (recentWeightChart == null) {
            cacheRecentWeightChart();
        }

        return recentWeightChart;
    }

    public byte[] getChartBetweenDates(LocalDate start, LocalDate end) throws IOException {
        LOG.info("caching weight chart");

        List<AveragedWeight> weights = calculateAverageWeighs(weightService.getAllWeightWithEntries())
                .stream()
                .filter(w ->
                        (start == null || w.getDate().isAfter(start))
                                && (end == null || w.getDate().isBefore(end)))
                .collect(Collectors.toList());

        BufferedImage bufferedImage = createChart(weights);


        return PngToByteArray.convert(bufferedImage);
    }

    @Scheduled(fixedRate = 1000 * 60, initialDelay = 1)
    public void cacheRecentWeightChart() throws IOException {
        LOG.info("caching recent weight chart");
        LocalDate aMonthAgo = LocalDate.now().minusDays(30);

        List<AveragedWeight> weights = calculateAverageWeighs(weightService.getAllWeightWithEntries())
                .stream()
                .filter(w -> w.getDate().isAfter(aMonthAgo))
                .collect(Collectors.toList());

        BufferedImage bufferedImage = createChart(weights);

        this.recentWeightChart = PngToByteArray.convert(bufferedImage);
    }

    @Scheduled(fixedRate = 1000 * 60, initialDelay = 1)
    public void cacheWeightChart() throws IOException {
        LOG.info("caching weight chart");

        BufferedImage bufferedImage = createChart(
                calculateAverageWeighs(weightService.getAllWeightWithEntries()));


        this.weightChart = PngToByteArray.convert(bufferedImage);
    }

    private BufferedImage createChart(List<AveragedWeight> weights) {
        List<Double> yData = weights.stream()
                .map(w -> w.getAverage())
                .collect(Collectors.toList());

        List<Date> xData = weights.stream()
                .map(w -> Date.from(w.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .collect(Collectors.toList());

        XYChart chart = new XYChartBuilder()
                .width(1000)
                .height(500)
                .xAxisTitle("Date")
                .yAxisTitle("Weight (kg)")
                .theme(Styler.ChartTheme.GGPlot2)
                .build();

        Font font = chart.getStyler().getAxisTickLabelsFont();

        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setAxisTickLabelsFont(font.deriveFont(
                Collections.singletonMap(
                        TextAttribute.WEIGHT, TextAttribute.WEIGHT_LIGHT)));
        chart.getStyler().setDatePattern("YYYY-MM-dd");
        chart.getStyler().setChartPadding(ChartConfig.CHART_PADDING);
        chart.getStyler().setMarkerSize(4);

        XYSeries series = chart.addSeries("data", xData, yData);

        series.setMarker(SeriesMarkers.CIRCLE);

        BufferedImage bufferedImage = new BufferedImage(chart.getWidth(), chart.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        chart.paint(graphics2D, chart.getWidth(), chart.getHeight());
        return bufferedImage;
    }

}
