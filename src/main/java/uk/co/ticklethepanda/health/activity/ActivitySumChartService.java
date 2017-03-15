package uk.co.ticklethepanda.health.activity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.style.Styler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uk.co.ticklethepanda.health.ChartConfig;
import uk.co.ticklethepanda.utility.image.PngToByteArray;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ActivitySumChartService {

    private static final Logger log = LogManager.getLogger();

    ActivityService activityService;

    private byte[] sumDayByWeekdayImage;
    private byte[] sumDayByMonthImage;

    public ActivitySumChartService(@Autowired ActivityService activityService) {
        this.activityService = activityService;
    }

    @Scheduled(fixedRate = 1000 * 60, initialDelay = 1)
    public void cacheDayByWeekdayImage() throws IOException {
        log.info("caching activity sum by weekday chart");
        Map<DayOfWeek, Double> sumOfStepsByDayOfWeek = activityService.getSumByDayOfWeek();

        this.sumDayByWeekdayImage = PngToByteArray.convert(createChart(sumOfStepsByDayOfWeek));
        log.info("caching activity sum by weekday chart");
    }

    @Scheduled(fixedRate = 1000 * 60, initialDelay = 1)
    public void cacheDayByMonthImage() throws IOException {
        log.info("caching activity sum by month chart");
        Map<Month, Double> sumOfStepsByMonth = activityService.getSumByMonth();

        this.sumDayByMonthImage = PngToByteArray.convert(createChart(sumOfStepsByMonth));
        log.info("cached activity sum by month chart");
    }

    private BufferedImage createChart(Map<?, Double> sumOfStepsByMonth) {
        LocalDate today = LocalDate.now();

        List<Double> yData = sumOfStepsByMonth.entrySet()
                .stream()
                .map(a -> a.getValue())
                .collect(Collectors.toList());

        List<String> xData = sumOfStepsByMonth.entrySet()
                .stream()
                .map(a -> a.getKey().toString())
                .collect(Collectors.toList());

        CategoryChart chart = new CategoryChartBuilder()
                .width(1000)
                .height(500)
                .xAxisTitle("Month")
                .yAxisTitle("Steps")
                .theme(Styler.ChartTheme.GGPlot2)
                .build();

        Font font = chart.getStyler().getAxisTickLabelsFont();

        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setYAxisTicksVisible(false);
        chart.getStyler().setAxisTickLabelsFont(font.deriveFont(
                Collections.singletonMap(
                        TextAttribute.WEIGHT, TextAttribute.WEIGHT_LIGHT)));
        chart.getStyler().setDatePattern("HH:mm");
        chart.getStyler().setChartPadding(ChartConfig.CHART_PADDING);

        chart.addSeries("data", xData, yData);

        BufferedImage bufferedImage = new BufferedImage(chart.getWidth(), chart.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        chart.paint(graphics2D, chart.getWidth(), chart.getHeight());
        return bufferedImage;
    }

    public byte[] getSumDayByWeekdayImage() throws IOException {
        if (sumDayByWeekdayImage == null) {
            cacheDayByWeekdayImage();
        }
        return sumDayByWeekdayImage;
    }

    public byte[] getSumDayByMonthImage() throws IOException {
        if (sumDayByMonthImage == null) {
            cacheDayByMonthImage();
        }
        return sumDayByMonthImage;
    }
}
