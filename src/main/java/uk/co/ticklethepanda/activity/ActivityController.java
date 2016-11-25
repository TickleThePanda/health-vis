package uk.co.ticklethepanda.activity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.co.ticklethepanda.activity.dto.DayActivityDto;
import uk.co.ticklethepanda.activity.dto.transformers.DayActivityEntityToDto;
import uk.co.ticklethepanda.activity.local.ActivityService;
import uk.co.ticklethepanda.activity.local.MinuteActivity;

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

    public ActivityController(@Autowired ActivityService activityService) {
        this.activityService = activityService;
    }

    @RequestMapping(value = "/average/day")
    @ResponseBody
    public DayActivityDto getAverageDay() {
        return dayActivityEntityToDto.transform(activityService.getAverageDay());
    }

    @RequestMapping(value = "/average/day", params = "aggregate=weekday")
    @ResponseBody
    public Map<DayOfWeek, DayActivityDto> getDataByWeekday() {
        Map<DayOfWeek, Set<MinuteActivity>> entities = activityService.getAverageDayByWeekday();

        Map<DayOfWeek, DayActivityDto> dtos = new HashMap<>();
        entities.entrySet().forEach(e -> dtos.put(e.getKey(), dayActivityEntityToDto.transform(e.getValue())));

        return dtos;
    }

    @RequestMapping(value = "/average/day/by/month", params = "aggregate=month")
    @ResponseBody
    public Map<Month, DayActivityDto> getDataByMonths() {
        Map<Month, Set<MinuteActivity>> entities = activityService.getAverageDayByMonth();

        Map<Month, DayActivityDto> dtos = new HashMap<>();
        entities.entrySet().forEach(e -> dtos.put(e.getKey(), dayActivityEntityToDto.transform(e.getValue())));

        return dtos;
    }
}
