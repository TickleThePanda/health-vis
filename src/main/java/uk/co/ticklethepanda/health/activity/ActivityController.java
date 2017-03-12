package uk.co.ticklethepanda.health.activity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.co.ticklethepanda.health.activity.dto.DayActivityDto;
import uk.co.ticklethepanda.health.activity.dto.transformers.DayActivityEntityToDto;
import uk.co.ticklethepanda.utility.web.Transformer;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 *
 */
@Controller
@RequestMapping(value = "/health/average-activity")
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

    @RequestMapping("/")
    @ResponseBody
    public DayActivityDto getAverageDay() {
        return dayActivityEntityToDto.transform(activityService.getAverageDay());
    }

    @RequestMapping(params = {"facet=weekday"})
    @ResponseBody
    public Map<DayOfWeek, DayActivityDto> getDataByWeekday() {
        Map<DayOfWeek, List<MinuteActivity>> entities = activityService.getAverageDayByWeekday();

        Map<DayOfWeek, DayActivityDto> dtos = new HashMap<>();
        entities.entrySet().forEach(e -> dtos.put(e.getKey(), dayActivityEntityToDto.transform(e.getValue())));

        return dtos;
    }

    @RequestMapping(params = {"facet=month"})
    @ResponseBody
    public Map<Month, DayActivityDto> getDataByMonths() {
        Map<Month, List<MinuteActivity>> entities = activityService.getAverageDayByMonth();

        Map<Month, DayActivityDto> dtos = new HashMap<>();
        entities.entrySet().forEach(e -> dtos.put(e.getKey(), dayActivityEntityToDto.transform(e.getValue())));

        return dtos;
    }

    @RequestMapping(params = {"img"}, produces = "image/png")
    @ResponseBody
    public byte[] getAverageDayImage(
            @RequestParam(value = "after", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate startDate,
            @RequestParam(value = "before", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate endDate) throws IOException {
        if (startDate == null && endDate == null) {
            return activityChartService.getAverageDayImage();
        }

        checkPermissionForRange(startDate, endDate);

        return activityChartService.getAverageDayImageBetweenDates(startDate, endDate);
    }

    private void checkPermissionForRange(LocalDate startDate, LocalDate endDate) {
        final int MIN_NUMBER_OF_DAYS_APART = 30;

        if (startDate != null && endDate != null) {
            if (ChronoUnit.DAYS.between(startDate, endDate) < MIN_NUMBER_OF_DAYS_APART) {
                throw new AccessDeniedException("Specify dates (before and after) that are more than a month apart.");
            }
        }
        if (endDate == null && startDate != null) {
            if (ChronoUnit.DAYS.between(startDate, LocalDate.now()) < MIN_NUMBER_OF_DAYS_APART) {
                throw new AccessDeniedException("Specify a start date (after) that is more than a month earlier than now.");
            }
        }
        if (startDate == null && endDate != null) {
            if (ChronoUnit.DAYS.between(activityService.getFirstDate(), endDate) < MIN_NUMBER_OF_DAYS_APART) {
                throw new AccessDeniedException("Specify an end date (before) that is more than a month later than the first entry");
            }
        }
    }

    @RequestMapping(params = {"img", "recent"}, produces = "image/png")
    @ResponseBody
    public byte[] getAverageDayImageForTheLastMonth() throws IOException {
        return activityChartService.getAverageDayImageForLastMonth();
    }

    @RequestMapping(params = {"img", "facet=weekday"}, produces = "image/png")
    @ResponseBody
    public byte[] getAverageDayByWeekdayImage() throws IOException {
        return activityChartService.getAverageDayByWeekdayImage();
    }

    @RequestMapping(params = {"img", "facet=month"}, produces = "image/png")
    @ResponseBody
    public byte[] getAverageDayByMonthImage() throws IOException {
        return activityChartService.getAverageDayByMonthImage();
    }

}
