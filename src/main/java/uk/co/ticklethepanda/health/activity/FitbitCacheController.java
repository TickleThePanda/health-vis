package uk.co.ticklethepanda.health.activity;

import com.google.api.client.http.HttpRequestFactory;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;
import uk.co.ticklethepanda.health.activity.fitbit.DaoException;
import uk.co.ticklethepanda.health.activity.fitbit.FitbitApi;
import uk.co.ticklethepanda.health.activity.fitbit.UserCredentialManager;
import uk.co.ticklethepanda.health.activity.fitbit.activity.FitbitIntradayActivity;
import uk.co.ticklethepanda.health.activity.fitbit.activity.FitbitIntradayActivityRepo;
import uk.co.ticklethepanda.health.activity.fitbit.ratelimit.RateLimitStatus;
import uk.co.ticklethepanda.health.activity.fitbit.ratelimit.RateLimitStatusRepo;
import uk.co.ticklethepanda.health.activity.fitbit.user.FitbitUserRepo;
import uk.co.ticklethepanda.health.activity.transformers.DayActivityFitbitToEntity;
import uk.co.ticklethepanda.utility.date.LocalDateRange;

import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.Callable;

/**
 *
 */
@Controller
@RequestMapping(value = "/health/fitbit")
public class FitbitCacheController {

    public static final int HOURLY = 1000 * 60 * 60;
    public static final int IMMEDIATE = 0;

    private static final Logger logger = LogManager.getLogger();

    private static final String SCOPE = "activity profile";

    private static final int EXPIRATION_TIME = 2592000;

    private final String baseUri;

    private final UserCredentialManager credentialManager;

    private ActivityService activityService;

    public FitbitCacheController(@Autowired UserCredentialManager userCredentialManager,
                                 @Value("${baseUri}") String baseUri,
                                 @Autowired ActivityService activityService) {
        this.credentialManager = userCredentialManager;
        this.baseUri = baseUri;
        this.activityService = activityService;
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    @ResponseBody
    public RateLimitStatus fitbitStatus() throws IOException {
        RateLimitStatusRepo repo = new RateLimitStatusRepo(credentialManager.getRequestFactoryForMe());

        return repo.getRateLimitStatus();
    }

    @RequestMapping(value = "/callback", method = RequestMethod.GET)
    public RedirectView fitbitAuthCallback(@RequestParam("code") String code,
                                           HttpServletRequest request) throws IOException {
        credentialManager.addVerifiedUser("me", code);

        return new RedirectView("/health/fitbit/status");
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public RedirectView fitbitLogin() throws IOException {
        String redirect = FitbitApi.AUTHORIZE_URL
                + "?response_type=code"
                + "&client_id=" + credentialManager.getClientCredentials().getId()
                + "&scope=" + SCOPE
                + "&expires_in=" + EXPIRATION_TIME;
        return new RedirectView(redirect);
    }

    @RequestMapping(value = "/cache")
    public Callable<Void> triggerCacheCheck() throws IOException, DaoException {
        return () -> {
            cacheFitbitData();
            return null;
        };
    }

    @Scheduled(fixedRate = HOURLY, initialDelay = IMMEDIATE)
    public void cacheFitbitData() throws IOException, DaoException {

        logger.info("refreshing cache");

        logger.info("refreshing token");
        credentialManager.getCredentialsForUser("me").refreshToken();
        logger.info("refreshed token");

        HttpRequestFactory requestFactory = credentialManager.getRequestFactoryForMe();

        FitbitUserRepo userRepo = new FitbitUserRepo(requestFactory);

        LocalDate firstDate = userRepo.getAuthorisedUser().getMemberSince();

        FitbitIntradayActivityRepo intradayActivityDao = new FitbitIntradayActivityRepo(requestFactory);

        DayActivityFitbitToEntity transformer = new DayActivityFitbitToEntity();

        for (LocalDate date : new LocalDateRange(firstDate, LocalDate.now())) {
            if (!activityService.hasCompleteEntry(date)) {
                logger.info("getting activity from fitbit for " + date.toString());
                FitbitIntradayActivity activity = intradayActivityDao.getDayActivity(date);
                logger.info("replacing activity for " + date.toString());
                activityService.replaceActivities(transformer.transform(activity));
            } else {
                logger.info("skipping activity fitbit for " + date.toString() + " - already up to date");
            }
        }
        ;
        logger.info("refreshed cache");
    }

}
