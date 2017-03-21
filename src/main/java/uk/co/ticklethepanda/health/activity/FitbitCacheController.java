package uk.co.ticklethepanda.health.activity;

import com.google.api.client.http.HttpRequestFactory;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 */
@Controller
@RequestMapping(value = "/health/fitbit")
public class FitbitCacheController {

    private static final Logger logger = LogManager.getLogger();

    public static final int HOURLY = 1000 * 60 * 60;
    public static final int IMMEDIATE = 0;
    public static final int FORCE_UPDATE_THRESHOLD_DAYS = 14;

    private static final long MONTHLY = 1000L * 60L * 60L * 24L * 28L;

    private static final String SCOPE = "activity profile";
    private static final int EXPIRATION_TIME = 2592000;

    private final UserCredentialManager credentialManager;

    private final List<LocalDate> cacheQueue = new LinkedList<>();

    private ActivityService activityService;

    public FitbitCacheController(@Autowired UserCredentialManager userCredentialManager,
                                 @Autowired ActivityService activityService) {
        this.credentialManager = userCredentialManager;
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

    @Scheduled(fixedRate = MONTHLY, initialDelay = MONTHLY)
    @RequestMapping(value = "/cache", method = RequestMethod.DELETE)
    @ResponseBody
    public String addAllToCacheQueue() throws IOException, DaoException {
        HttpRequestFactory requestFactory = getHttpRequestFactory();

        FitbitUserRepo userRepo = new FitbitUserRepo(requestFactory);

        LocalDate firstDate = userRepo.getAuthorisedUser().getMemberSince();
        LocalDate today = LocalDate.now();
        for (LocalDate date : new LocalDateRange(firstDate, today)) {
            if (!cacheQueue.contains(date)) {
                cacheQueue.add(date);
            }
        }
        return "cache cleared";
    }

    @RequestMapping(value = "/cache/queue")
    @ResponseBody
    public List<LocalDate> getCacheQueue() {
        return cacheQueue;
    }

    @Scheduled(fixedRate = HOURLY, initialDelay = IMMEDIATE)
    public void updateCacheQueue() throws IOException, DaoException {

        HttpRequestFactory requestFactory = getHttpRequestFactory();

        FitbitUserRepo userRepo = new FitbitUserRepo(requestFactory);

        LocalDate firstDate = userRepo.getAuthorisedUser().getMemberSince();
        LocalDate today = LocalDate.now();

        for (LocalDate date : new LocalDateRange(firstDate, today)) {
            if (!activityService.hasCompleteEntry(date)
                    || date.isAfter(today.minusDays(FORCE_UPDATE_THRESHOLD_DAYS))) {
                if (!cacheQueue.contains(date)) {
                    cacheQueue.add(date);
                }
            }
        }
    }

    @RequestMapping(value = "/cache", method = RequestMethod.PATCH)
    @ResponseBody
    public String triggerCacheFitbitData() throws IOException, DaoException {
        cacheFitbitData();
        return "caching triggered";
    }

    @Scheduled(cron = "0 1 * * * *")
    @Async
    public void cacheFitbitData() throws IOException, DaoException {

        logger.info("refreshing cache");

        HttpRequestFactory requestFactory = getHttpRequestFactory();

        FitbitIntradayActivityRepo intradayActivityDao = new FitbitIntradayActivityRepo(requestFactory);

        DayActivityFitbitToEntity transformer = new DayActivityFitbitToEntity();

        ListIterator<LocalDate> listIterator = cacheQueue.listIterator();
        while (listIterator.hasNext()) {
            LocalDate date = listIterator.next();
            logger.info("getting activity from fitbit for " + date.toString());
            FitbitIntradayActivity activity = intradayActivityDao.getDayActivity(date);
            logger.info("replacing activity for " + date.toString());
            activityService.replaceActivities(transformer.transform(activity));

            listIterator.remove();
        }

    }

    private HttpRequestFactory getHttpRequestFactory() throws IOException {
        logger.info("refreshing token");
        credentialManager.getCredentialsForUser("me").refreshToken();
        logger.info("refreshed token");

        return credentialManager.getRequestFactoryForMe();
    }

}
