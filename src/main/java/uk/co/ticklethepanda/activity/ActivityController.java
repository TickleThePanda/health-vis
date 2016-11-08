package uk.co.ticklethepanda.activity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.co.ticklethepanda.activity.dto.DayActivity;
import uk.co.ticklethepanda.activity.dto.transformers.DayActivityTransformer;
import uk.co.ticklethepanda.activity.fitbit.DaoException;
import uk.co.ticklethepanda.activity.fitbit.FitbitIntradayActivityRepoFitbit;
import uk.co.ticklethepanda.activity.fitbit.UserCredentialManager;

import java.io.IOException;
import java.time.LocalDate;

/**
 * @author Lovingly hand crafted by the ISIS Business Applications Team
 */
@Controller
@RequestMapping(value = "/health/activity")
public class ActivityController {

    private final UserCredentialManager credentialManager;

    public ActivityController(@Autowired UserCredentialManager credentialManager) {
        this.credentialManager = credentialManager;
    }

    @RequestMapping(value = "/{year}/{month}/{day}")
    @ResponseBody
    public DayActivity getFitbitDataForDay(@PathVariable("year") int year,
                                           @PathVariable("month") int month,
                                           @PathVariable("day") int day) throws IOException, DaoException {

        FitbitIntradayActivityRepoFitbit intradayActivityDao = new FitbitIntradayActivityRepoFitbit(credentialManager
                .getRequestFactoryForMe());

        return new DayActivityTransformer().transform(
                intradayActivityDao.getDayActivity(
                        LocalDate.of(year, month, day)));
    }
}
