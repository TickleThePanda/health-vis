package uk.co.ticklethepanda.health.activity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.co.ticklethepanda.health.activity.dto.DayActivityDto;
import uk.co.ticklethepanda.health.activity.dto.transformers.DayActivityEntityToDto;
import uk.co.ticklethepanda.health.activity.local.ActivityService;
import uk.co.ticklethepanda.health.activity.local.MinuteActivity;
import uk.co.ticklethepanda.utility.web.Transformer;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.Month;
import java.util.*;

/**
 * @author Lovingly hand crafted by the ISIS Business Applications Team
 */
@Controller
@RequestMapping(value = "/health/activity")
public class ActivityController {

    private static final Logger log = LogManager.getLogger();


    private final ActivityService activityService;

    private final Transformer<Collection<MinuteActivity>, DayActivityDto> dayActivityEntityToDto
            = new DayActivityEntityToDto();
    public static final Comparator<MinuteActivity> MINUTE_ACTIVITY_COMPARATOR_BY_TIME = (a, b) -> a.getTime().compareTo(b.getTime());
    private final ActivityChartService activityChartService;


    public ActivityController(@Autowired ActivityService activityService,
                              @Autowired ActivityChartService activityChartService) {
        this.activityService = activityService;
        this.activityChartService = activityChartService;
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
        return activityChartService.getAverageDayImage();
    }

    @RequestMapping(value = "/average/day", params = {"img", "aggregate=weekday"}, produces = "image/png")
    @ResponseBody
    public byte[] getAverageDayByWeekdayImage() throws IOException {
        return activityChartService.getAverageDayByWeekdayImage();
    }

    @RequestMapping(value = "/average/day", params = {"img", "aggregate=month"}, produces = "image/png")
    @ResponseBody
    public byte[] getAverageDayByMonthImage() throws IOException {
        return activityChartService.getAverageDayByMonthImage();
    }

}
