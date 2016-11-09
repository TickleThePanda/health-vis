package uk.co.ticklethepanda.activity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.co.ticklethepanda.activity.dto.DayActivityDto;
import uk.co.ticklethepanda.activity.dto.transformers.DayActivityEntityToDto;
import uk.co.ticklethepanda.activity.fitbit.DaoException;
import uk.co.ticklethepanda.activity.local.ActivityService;
import uk.co.ticklethepanda.activity.local.DayActivity;
import uk.co.ticklethepanda.activity.local.MinuteActivity;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.*;
import static java.util.stream.Collectors.*;

/**
 * @author Lovingly hand crafted by the ISIS Business Applications Team
 */
@Controller
@RequestMapping(value = "/health/activity")
public class ActivityController {

    private final ActivityService activityService;

    private final Transformer<DayActivity, DayActivityDto> dayActivityEntityToDto
            = new DayActivityEntityToDto();

    public ActivityController(@Autowired ActivityService activityService) {
        this.activityService = activityService;
    }

    @RequestMapping(value = "/average/day")
    @ResponseBody
    public DayActivityDto getAverageDay() {
        List<DayActivity> activities = activityService.getAllActivity();

        Set<MinuteActivity> activityForDay = activities.stream()
                .flatMap(a -> a.getMinuteActivityEntities().stream())
                .collect(groupingBy(MinuteActivity::getTime,
                        averagingInt(MinuteActivity::getSteps)))
                .entrySet().stream()
                .map(e -> new MinuteActivity(e.getKey(), e.getValue().intValue()))
                .collect(toSet());

        return dayActivityEntityToDto.transform(new DayActivity(null, activityForDay));
    }

    @RequestMapping(value = "/average/day/by/weekdays")
    @ResponseBody
    public Map<DayOfWeek, DayActivityDto> getDataByWeekday() {
        List<DayActivity> activities = activityService.getAllActivity();

        Map<DayOfWeek, DayActivityDto> daysToActivity = new HashMap<>();

        for(DayOfWeek dayOfWeek : DayOfWeek.values()) {
            Set<MinuteActivity> activityForDay = activities.stream()
                    .filter(a -> a.getDate().getDayOfWeek().equals(dayOfWeek))
                    .flatMap(a -> a.getMinuteActivityEntities().stream())
                    .collect(groupingBy(MinuteActivity::getTime,
                            averagingInt(MinuteActivity::getSteps)))
                    .entrySet().stream()
                    .map(e -> new MinuteActivity(e.getKey(), e.getValue().intValue()))
                    .collect(toSet());

            daysToActivity.put(dayOfWeek,
                    dayActivityEntityToDto.transform(new DayActivity(null, activityForDay)));
        }

        return daysToActivity;
    }


    @RequestMapping(value = "/average/day/by/months")
    @ResponseBody
    public Map<Month, DayActivityDto> getDataByMonths() {
        List<DayActivity> activities = activityService.getAllActivity();

        Map<Month, DayActivityDto> daysToActivity = new HashMap<>();

        for(Month month : Month.values()) {
            Set<MinuteActivity> activityForDay = activities.stream()
                    .filter(a -> a.getDate().getMonth().equals(month))
                    .flatMap(a -> a.getMinuteActivityEntities().stream())
                    .collect(groupingBy(MinuteActivity::getTime,
                            averagingInt(MinuteActivity::getSteps)))
                    .entrySet().stream()
                    .map(e -> new MinuteActivity(e.getKey(), e.getValue().intValue()))
                    .collect(toSet());

            daysToActivity.put(month,
                    dayActivityEntityToDto.transform(new DayActivity(null, activityForDay)));
        }

        return daysToActivity;
    }
}
