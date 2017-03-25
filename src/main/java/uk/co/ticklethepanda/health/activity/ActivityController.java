package uk.co.ticklethepanda.health.activity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.co.ticklethepanda.health.activity.domain.MinuteActivity;
import uk.co.ticklethepanda.health.activity.dto.DayActivityDto;
import uk.co.ticklethepanda.health.activity.dto.transformers.DayActivityEntityToDto;
import uk.co.ticklethepanda.utility.web.Transformer;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Controller
@RequestMapping(value = "/health/activity")
public class ActivityController {

    private static final Logger log = LogManager.getLogger();

    private final Transformer<List<MinuteActivity>, DayActivityDto> dayActivityEntityToDto
            = new DayActivityEntityToDto();
    ActivityService activityService;

    public ActivityController(@Autowired ActivityService activityService) {
        this.activityService = activityService;
    }

    @RequestMapping
    @ResponseBody
    public DayActivityDto getAverageDay() {
        return dayActivityEntityToDto.transform(activityService.getAverageDay());
    }

    @RequestMapping(params = {"average", "by=minute", "facet=weekday"})
    @ResponseBody
    public Map<DayOfWeek, DayActivityDto> getDataByWeekday() {
        return dayActivityEntityToDto
                .transformMap(activityService.getAverageDayByWeekday());
    }

    @RequestMapping(params = {"average", "by=minute", "facet=month"})
    @ResponseBody
    public Map<Month, DayActivityDto> getDataByMonths() {
        return dayActivityEntityToDto
                .transformMap(activityService.getAverageDayByMonth());
    }

    @RequestMapping(params = {"average", "by=day", "facet=weekday"})
    @ResponseBody
    public Map<DayOfWeek, Double> getSumByWeekday() {
        return activityService.getSumByDayOfWeek();
    }

    @RequestMapping(params = {"average", "by=day", "facet=month"})
    @ResponseBody
    public Map<Month, Double> getSumByMonth() {
        return activityService.getSumByMonth();
    }

    @RequestMapping(params = {"sum"})
    @ResponseBody
    public Double getSum() {
        return activityService.getSumOfSteps();
    }

}
