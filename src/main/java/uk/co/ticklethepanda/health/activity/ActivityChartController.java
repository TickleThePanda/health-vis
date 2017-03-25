package uk.co.ticklethepanda.health.activity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Controller
@RequestMapping(value = "/health/activity", params = {"average", "img"})
public class ActivityChartController {

    private final ActivityService activityService;
    private final ActivityChartService activityChartService;

    public ActivityChartController(
            @Autowired ActivityService activityService,
            @Autowired ActivityChartService activityChartService
    ) {
        this.activityService = activityService;
        this.activityChartService = activityChartService;
    }

    @RequestMapping(params = {"by=day", "facet=weekday"}, produces = "image/png")
    @ResponseBody
    public byte[] getSumDayByWeekdayImage() throws IOException {
        return activityChartService.getSumDayByWeekdayImage();
    }

    @RequestMapping(params = {"by=day", "facet=month"}, produces = "image/png")
    @ResponseBody
    public byte[] getSumDayByMonthImage() throws IOException {
        return activityChartService.getSumDayByMonthImage();
    }

    @RequestMapping(params = {"by=minute"}, produces = "image/png")
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

    @RequestMapping(params = {"by=minute", "recent"}, produces = "image/png")
    @ResponseBody
    public byte[] getAverageDayImageForTheLastMonth() throws IOException {
        return activityChartService.getAverageDayImageForLastMonth();
    }

    @RequestMapping(params = {"by=minute", "facet=weekday"}, produces = "image/png")
    @ResponseBody
    public byte[] getAverageDayByWeekdayImage() throws IOException {
        return activityChartService.getAverageDayByWeekdayImage();
    }

    @RequestMapping(params = {"by=minute", "facet=month"}, produces = "image/png")
    @ResponseBody
    public byte[] getAverageDayByMonthImage() throws IOException {
        return activityChartService.getAverageDayByMonthImage();
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

}
