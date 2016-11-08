package uk.co.ticklethepanda.fitbit;

import com.google.api.client.http.HttpRequestFactory;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.co.ticklethepanda.activity.dto.DayActivity;
import uk.co.ticklethepanda.activity.dto.transformers.DayActivityTransformer;

import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.Callable;

/**
 * Created by panda on 07/11/2016.
 */
@Controller
@EnableAutoConfiguration
@EnableAsync
@EnableScheduling
@ComponentScan("uk.co.ticklethepanda.fitbit")
public class ActivityController {

    private static final String SCOPE = "activity";

    private static final int EXPIRATION_TIME = 2592000;
    private static final Gson GSON = new Gson();

    private final String baseUri;

    private final UserCredentialManager credentialManager;

    private final LocalDate firstDay;

    public ActivityController(@Autowired UserCredentialManager userCredentialManager,
                              @Value("${baseUri}") String baseUri,
                              @Value("${activity.date.start}") String firstDay) {
        this.credentialManager = userCredentialManager;
        this.baseUri = baseUri;
        this.firstDay = LocalDate.parse(firstDay);
    }

    public static void main(String[] args) {
        SpringApplication.run(ActivityController.class);
    }

    @RequestMapping(value = "/health/fitbit/status", method = RequestMethod.GET)
    @ResponseBody
    public RateLimitStatus fitbitStatus() throws IOException {
        RateLimitStatusRepositoryFitbit repo = new RateLimitStatusRepositoryFitbit(getRequestFactoryForMe());

        return repo.getRateLimitStatus();
    }

    @RequestMapping(value = "/health/fitbit/callback", method = RequestMethod.GET)
    public String fitbitAuthCallback(@RequestParam("code") String code) throws IOException {
        credentialManager.addVerifiedUser("me", code);
        return "redirect:/health/fitbit/status";
    }

    @RequestMapping(value = "/health/fitbit/login", method = RequestMethod.GET)
    public String fitbitLogin() throws IOException {
        String redirect = FitbitApi.AUTHORIZE_URL
                + "?response_type=code"
                + "&client_id=" + credentialManager.getClientCredentials().getId()
                + "&scope=" + SCOPE
                + "&expires_in=" + EXPIRATION_TIME;
        return "redirect:" + redirect;
    }

    @RequestMapping(value = "/health/fitbit/cache")
    public Callable<Void> triggerCacheCheck() throws IOException, DaoException {
        return () -> {cacheFitbitData(); return null;};
    }

    @Scheduled(fixedRate = 1000 * 60 * 60, initialDelay = 0)
    public void cacheFitbitData() throws IOException, DaoException {
        IntradayActivityDaoWebApi intradayActivityDao = new IntradayActivityDaoWebApi(getRequestFactoryForMe());

        intradayActivityDao.getIntradayActivityRange(firstDay, LocalDate.now());
    }

    @RequestMapping(value = "/health/activity/{year}/{month}/{day}")
    @ResponseBody
    public DayActivity getFitbitDataForDay(@PathVariable("year") int year,
                                           @PathVariable("month") int month,
                                           @PathVariable("day") int day) throws IOException, DaoException {
        IntradayActivityDaoWebApi intradayActivityDao = new IntradayActivityDaoWebApi(getRequestFactoryForMe());

        return new DayActivityTransformer().transform(
                intradayActivityDao.getDayActivity(
                        LocalDate.of(year, month, day)));
    }

    private HttpRequestFactory getRequestFactoryForMe() throws IOException {
        return credentialManager.getHttpRequestFactory(credentialManager.getCredentialsForUser("me"));
    }
}
