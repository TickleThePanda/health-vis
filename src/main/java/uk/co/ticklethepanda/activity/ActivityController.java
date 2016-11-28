package uk.co.ticklethepanda.activity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.co.ticklethepanda.activity.dto.DayActivityDto;
import uk.co.ticklethepanda.activity.dto.transformers.DayActivityEntityToDto;
import uk.co.ticklethepanda.activity.local.ActivityService;
import uk.co.ticklethepanda.activity.local.MinuteActivity;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lovingly hand crafted by the ISIS Business Applications Team
 */
@Controller
@RequestMapping(value = "/health/activity")
public class ActivityController {

    private static final Logger log = LogManager.getLogger();
    public static final int AXIS_HEIGHT = 48;
    public static final int CHART_PADDING = 3;

    private final ActivityService activityService;

    private final Transformer<Collection<MinuteActivity>, DayActivityDto> dayActivityEntityToDto
            = new DayActivityEntityToDto();
    public static final Comparator<MinuteActivity> MINUTE_ACTIVITY_COMPARATOR_BY_TIME = (a, b) -> a.getTime().compareTo(b.getTime());
    private byte[] dayImage;
    private byte[] dayByWeekdayImage;
    private byte[] dayByMonthImage;

    public ActivityController(@Autowired ActivityService activityService) {
        this.activityService = activityService;
    }

    @Scheduled(fixedDelay = 1000*60, initialDelay = 0)
    public void cacheDayImage() throws IOException {
        LocalDate today = LocalDate.now();

        List<Double> yData = activityService.getAverageDay()
                .stream()
                .map(a -> a.getSteps())
                .collect(Collectors.toList());

        List<Date> xData = activityService.getAverageDay().stream()
                .map(a -> Date.from(a.getTime().atDate(today).toInstant(ZoneOffset.UTC)))
                .collect(Collectors.toList());

        XYChart chart = new XYChartBuilder()
                .width(1000)
                .height(500)
                .xAxisTitle("Time of Day")
                .yAxisTitle("Steps")
                .theme(Styler.ChartTheme.GGPlot2)
                .build();

        Font font = chart.getStyler().getAxisTickLabelsFont();

        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setAxisTickLabelsFont(font.deriveFont(
                Collections.singletonMap(
                        TextAttribute.WEIGHT, TextAttribute.WEIGHT_LIGHT)));
        chart.getStyler().setDatePattern("HH:mm");
        chart.getStyler().setChartPadding(CHART_PADDING);

        XYSeries series = chart.addSeries("data", xData, yData);

        series.setMarker(SeriesMarkers.NONE);
        
        BufferedImage bufferedImage = new BufferedImage(chart.getWidth(), chart.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        chart.paint(graphics2D, chart.getWidth(), chart.getHeight());
        
        this.dayImage = convertImageToPngByteArray(bufferedImage);
    }

    @Scheduled(fixedDelay = 1000*60, initialDelay = 0)
    public void cacheDayByWeekdayImage() throws IOException {
        Map<DayOfWeek, List<MinuteActivity>> entities = activityService.getAverageDayByWeekday();

        this.dayByWeekdayImage = convertImageToPngByteArray(createFacetedChart(entities));
    }

    @Scheduled(fixedDelay = 1000*60, initialDelay = 0)
    public void cacheDayByMonthImage() throws IOException {

        Map<Month, List<MinuteActivity>> entities = activityService.getAverageDayByMonth();

        this.dayByMonthImage = convertImageToPngByteArray(createFacetedChart(entities));
    }

    private BufferedImage createFacetedChart(Map<?, List<MinuteActivity>> entities) {
        LocalDate today = LocalDate.now();

        int chartWidth = 1000;
        int chartHeight = 150;

        BufferedImage imageSet = new BufferedImage(chartWidth, chartHeight * entities.size() + AXIS_HEIGHT, BufferedImage.TYPE_INT_RGB);

        Graphics2D imageSetGraphics = imageSet.createGraphics();

        int i = 0;

        double max = entities.entrySet()
                .stream()
                .flatMap(e -> e.getValue().stream())
                .collect(Collectors.toList())
                .stream()
                .map(a -> a.getSteps())
                .reduce(Math::max)
                .get();

        for (Map.Entry<?, List<MinuteActivity>> entry : entities.entrySet()) {

            boolean last = entities.size() == i + 1;

            List<Date> xData = entry.getValue().stream()
                    .map(a -> Date.from(a.getTime().atDate(today).toInstant(ZoneOffset.UTC)))
                    .collect(Collectors.toList());
            List<Double> yData = entry.getValue().stream()
                    .map(a -> a.getSteps())
                    .collect(Collectors.toList());

            String title;
            if(entry.getKey() instanceof Month) {
                title = ((Month) entry.getKey()).getDisplayName(TextStyle.FULL, Locale.UK);
            } else if(entry.getKey() instanceof DayOfWeek) {
                title = ((DayOfWeek) entry.getKey()).getDisplayName(TextStyle.FULL, Locale.UK);
            } else {
                title = entry.getKey().toString();
            }

            XYChart chart = new XYChartBuilder()
                    .width(chartWidth)
                    .height(chartHeight + (last ? AXIS_HEIGHT : 0))
                    .xAxisTitle(last ? "Time of Day" : null)
                    .yAxisTitle("Steps")
                    .title(title)
                    .theme(Styler.ChartTheme.GGPlot2)
                    .build();

            Font font = chart.getStyler().getAxisTickLabelsFont();

            chart.getStyler().setLegendVisible(false);
            chart.getStyler().setAxisTickLabelsFont(font.deriveFont(
                    Collections.singletonMap(
                            TextAttribute.WEIGHT, TextAttribute.WEIGHT_LIGHT)));
            chart.getStyler().setDatePattern("HH:mm");
            chart.getStyler().setYAxisMax(max);
            chart.getStyler().setYAxisTicksVisible(true);
            chart.getStyler().setXAxisTicksVisible(last);
            chart.getStyler().setChartPadding(CHART_PADDING);

            XYSeries series = chart.addSeries("data", xData, yData);

            series.setMarker(SeriesMarkers.NONE);
            series.setLineColor(Color.getHSBColor((float)i/(float)entities.size(), 0.6f, 0.85f));

            BufferedImage individualImage = new BufferedImage(chartWidth, chartHeight + (last ? AXIS_HEIGHT : 0), BufferedImage.TYPE_INT_RGB);

            Graphics2D individualGraphics = individualImage.createGraphics();
            chart.paint(individualGraphics, chart.getWidth(), chart.getHeight());
            individualGraphics.dispose();

            imageSetGraphics.drawImage(individualImage, 0, i * chartHeight, null);
            i++;
        }

        imageSetGraphics.dispose();
        return imageSet;
    }

    @RequestMapping(value = "/average/day")
    @ResponseBody
    public DayActivityDto getAverageDay() {
        return dayActivityEntityToDto.transform(activityService.getAverageDay());
    }

    @RequestMapping(value = "/average/day", params = "aggregate=weekday")
    @ResponseBody
    public Map<DayOfWeek, DayActivityDto> getDataByWeekday() {
        Map<DayOfWeek, List<MinuteActivity>> entities = activityService.getAverageDayByWeekday();

        Map<DayOfWeek, DayActivityDto> dtos = new HashMap<>();
        entities.entrySet().forEach(e -> dtos.put(e.getKey(), dayActivityEntityToDto.transform(e.getValue())));

        return dtos;
    }

    @RequestMapping(value = "/average/day", params = "aggregate=month")
    @ResponseBody
    public Map<Month, DayActivityDto> getDataByMonths() {
        Map<Month, List<MinuteActivity>> entities = activityService.getAverageDayByMonth();

        Map<Month, DayActivityDto> dtos = new HashMap<>();
        entities.entrySet().forEach(e -> dtos.put(e.getKey(), dayActivityEntityToDto.transform(e.getValue())));

        return dtos;
    }

    @RequestMapping(value = "/average/day", params = {"img"}, produces = "image/png")
    @ResponseBody
    public byte[] getAverageDayImage() throws IOException {
        if(dayImage == null) {
            cacheDayImage();
        }

        return dayImage;
    }

    @RequestMapping(value = "/average/day", params = {"img", "aggregate=weekday"}, produces = "image/png")
    @ResponseBody
    public byte[] getAverageDayByWeekdayImage() throws IOException {
        if(dayByWeekdayImage == null) {
            cacheDayByWeekdayImage();
        }

        return dayByWeekdayImage;
    }

    @RequestMapping(value = "/average/day", params = {"img", "aggregate=month"}, produces = "image/png")
    @ResponseBody
    public byte[] getAverageDayByMonthImage() throws IOException {
        if(dayByMonthImage == null) {
            cacheDayByMonthImage();
        }
        return dayByMonthImage;
    }


    private byte[] convertImageToPngByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ColorModel cm = image.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        BufferedImage imageClone = new BufferedImage(cm, raster, isAlphaPremultiplied, null);

        Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("png");
        ImageWriter writer = iter.next();

        writer.setOutput(ImageIO.createImageOutputStream(stream));
        IIOImage ioImage = new IIOImage(imageClone, null, null);
        writer.write(null, ioImage, null);
        writer.dispose();

        return stream.toByteArray();
    }
}
