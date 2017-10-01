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
import uk.co.ticklethepanda.fitbit.client.FitbitClientException;
import uk.co.ticklethepanda.fitbit.client.FitbitApiConfig;
import uk.co.ticklethepanda.fitbit.client.FitbitUserCredentialManager;
import uk.co.ticklethepanda.fitbit.client.model.FitbitIntradayActivity;
import uk.co.ticklethepanda.fitbit.client.repos.FitbitIntradayActivityClient;
import uk.co.ticklethepanda.fitbit.client.model.RateLimitStatus;
import uk.co.ticklethepanda.fitbit.client.repos.RateLimitStatusClient;
import uk.co.ticklethepanda.fitbit.client.repos.FitbitUserClient;
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

    private static final long MONTHLY = 1000L * 60L * 60L * 24L * 28L;
    private static final int HOURLY = 1000 * 60 * 60;
    private static final int IMMEDIATE = 0;

    private static final int FORCE_UPDATE_THRESHOLD_DAYS = 14;

    private static final String SCOPE = "activity profile";
    private static final int EXPIRATION_TIME = 2592000;

    private final FitbitUserCredentialManager credentialManager;

    private final List<LocalDate> cacheQueue = new LinkedList<>();

    private ActivityService activityService;

    public FitbitCacheController(@Autowired FitbitUserCredentialManager fitbitUserCredentialManager,
                                 @Autowired ActivityService activityService) {
        this.credentialManager = fitbitUserCredentialManager;
        this.activityService = activityService;
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    @ResponseBody
    public RateLimitStatus fitbitStatus() throws IOException {
        RateLimitStatusClient repo = new RateLimitStatusClient(credentialManager.getHttpRequestFactoryForUser("me"));

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
        String redirect = FitbitApiConfig.AUTHORIZE_URL
                + "?response_type=code"
                + "&client_id=" + credentialManager.getClientCredentials().getId()
                + "&scope=" + SCOPE
                + "&expires_in=" + EXPIRATION_TIME;
        return new RedirectView(redirect);
    }

    @Scheduled(fixedRate = MONTHLY, initialDelay = MONTHLY)
    @RequestMapping(value = "/cache", method = RequestMethod.DELETE)
    @ResponseBody
    public String addAllToCacheQueue() throws IOException, FitbitClientException {
        HttpRequestFactory requestFactory = getHttpRequestFactory();

        FitbitUserClient userRepo = new FitbitUserClient(requestFactory);

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
    public void updateCacheQueue() throws IOException, FitbitClientException {

        HttpRequestFactory requestFactory = getHttpRequestFactory();

        FitbitUserClient userRepo = new FitbitUserClient(requestFactory);

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
    public String triggerCacheFitbitData() throws IOException, FitbitClientException {
        cacheFitbitData();
        return "caching triggered";
    }

    @Scheduled(cron = "0 0 * * * *")
    @Async
    public void cacheFitbitData() throws IOException, FitbitClientException {

        logger.info("refreshing cache");

        HttpRequestFactory requestFactory = getHttpRequestFactory();

        FitbitIntradayActivityClient intradayActivityDao = new FitbitIntradayActivityClient(requestFactory);

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

        return credentialManager.getHttpRequestFactory(credentialManager.getCredentialsForUser("me"));
    }

}
