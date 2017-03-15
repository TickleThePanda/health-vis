package uk.co.ticklethepanda.health.activity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.co.ticklethepanda.health.activity.dto.DayActivityDto;
import uk.co.ticklethepanda.health.activity.dto.transformers.DayActivityEntityToDto;
import uk.co.ticklethepanda.utility.web.Transformer;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.Month;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

@Controller
@RequestMapping(path = "/health/activity", params = "sum")
public class ActivitySumController {


    private static final Logger log = LogManager.getLogger();

    private final ActivityService activityService;

    private final Transformer<Collection<MinuteActivity>, DayActivityDto> dayActivityEntityToDto
            = new DayActivityEntityToDto();
    public static final Comparator<MinuteActivity> MINUTE_ACTIVITY_COMPARATOR_BY_TIME = (a, b) -> a.getTime().compareTo(b.getTime());
    private final ActivitySumChartService activitySumChartService;


    public ActivitySumController(@Autowired ActivityService activityService,
                                 @Autowired ActivitySumChartService activitySumChartService) {
        this.activityService = activityService;
        this.activitySumChartService = activitySumChartService;
    }

    @RequestMapping
    @ResponseBody
    public Double getSum() {
        return activityService.getSumOfSteps();
    }

    @RequestMapping(params = {"facet=weekday"})
    @ResponseBody
    public Map<DayOfWeek, Double> getSumByWeekday() {
        return activityService.getSumByDayOfWeek();
    }

    @RequestMapping(params = {"facet=month"})
    @ResponseBody
    public Map<Month, Double> getSumByMonth() {
        return activityService.getSumByMonth();
    }

    @RequestMapping(params = {"img", "facet=weekday"}, produces = "image/png")
    @ResponseBody
    public byte[] getSumDayByWeekdayImage() throws IOException {
        return activitySumChartService.getSumDayByWeekdayImage();
    }

    @RequestMapping(params = {"img", "facet=month"}, produces = "image/png")
    @ResponseBody
    public byte[] getSumDayByMonthImage() throws IOException {
        return activitySumChartService.getSumDayByMonthImage();
    }
}
