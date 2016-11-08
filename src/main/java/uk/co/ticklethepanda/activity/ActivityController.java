package uk.co.ticklethepanda.activity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.co.ticklethepanda.activity.dto.DayActivityDto;
import uk.co.ticklethepanda.activity.dto.transformers.DayActivityEntityToDto;
import uk.co.ticklethepanda.activity.fitbit.DaoException;
import uk.co.ticklethepanda.activity.fitbit.FitbitIntradayActivityRepoFitbit;
import uk.co.ticklethepanda.activity.fitbit.UserCredentialManager;
import uk.co.ticklethepanda.activity.local.ActivityService;
import uk.co.ticklethepanda.activity.local.DayActivity;

import java.io.IOException;
import java.time.LocalDate;

/**
 * @author Lovingly hand crafted by the ISIS Business Applications Team
 */
@Controller
@RequestMapping(value = "/health/activity")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(@Autowired ActivityService activityService) {
        this.activityService = activityService;
    }

    @RequestMapping(value = "/{year}/{month}/{day}")
    @ResponseBody
    public DayActivityDto getFitbitDataForDay(@PathVariable("year") int year,
                                              @PathVariable("month") int month,
                                              @PathVariable("day") int day) throws IOException, DaoException {

        DayActivity activity = activityService.getActivityForDate(LocalDate.of(year, month, day));

        return new DayActivityEntityToDto().transform(activity);
    }
}
