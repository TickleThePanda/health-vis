package uk.co.ticklethepanda.health.activity.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.co.ticklethepanda.health.activity.services.ActivityService;
import uk.co.ticklethepanda.health.activity.services.MinuteActivity;
import uk.co.ticklethepanda.health.activity.controllers.dto.MinuteActivityDto;
import uk.co.ticklethepanda.health.activity.controllers.dto.ActivitySumDto;
import uk.co.ticklethepanda.utility.web.Transformer;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
@Controller
@RequestMapping(value = "/health/activity")
public class ActivityController {

    private static final Logger log = LogManager.getLogger();

    private final Transformer<List<MinuteActivity>, List<MinuteActivityDto>> transformer
            = t -> t.stream()
            .map(a -> new MinuteActivityDto(a.getTime(), a.getSteps()))
            .collect(Collectors.toList());

    private final ActivityService activityService;

    public ActivityController(@Autowired ActivityService activityService) {
        this.activityService = activityService;
    }

    @RequestMapping(params = {"average", "by=minute"})
    @ResponseBody
    public List<MinuteActivityDto> getAverageDay() {
        return transformer.transform(activityService.getAverageDay());
    }

    @RequestMapping(params = {"average", "by=minute", "facet=weekday"})
    @ResponseBody
    public Map<DayOfWeek, List<MinuteActivityDto>> getDataByWeekday() {
        return transformer.transformMap(activityService.getAverageDayByWeekday());
    }

    @RequestMapping(params = {"average", "by=minute", "facet=month"})
    @ResponseBody
    public Map<Month, List<MinuteActivityDto>> getDataByMonths() {
        return transformer.transformMap(activityService.getAverageDayByMonth());
    }

    @RequestMapping(params = {"average", "by=day", "facet=weekday"})
    @ResponseBody
    public Map<DayOfWeek, Long> getSumByWeekday() {
        return activityService.getSumByDayOfWeek();
    }

    @RequestMapping(params = {"average", "by=day", "facet=month"})
    @ResponseBody
    public Map<Month, Long> getSumByMonth() {
        return activityService.getSumByMonth();
    }

    @RequestMapping(params = {"sum"})
    @ResponseBody
    public ActivitySumDto getSum() {
        double sumOfSteps = activityService.getSumOfSteps();
        LocalDate date = activityService.getFirstDate();

        return new ActivitySumDto(sumOfSteps, date);
    }

}
