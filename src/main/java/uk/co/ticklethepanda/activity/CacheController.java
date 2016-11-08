package uk.co.ticklethepanda.activity;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.co.ticklethepanda.activity.fitbit.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.Callable;

/**
 * Created by panda on 07/11/2016.
 */
@Controller
@RequestMapping(value = "/health/fitbit")
public class CacheController {

    private static final String SCOPE = "activity";

    private static final int EXPIRATION_TIME = 2592000;
    private static final Gson GSON = new Gson();

    private final String baseUri;

    private final UserCredentialManager credentialManager;

    private final LocalDate firstDay;

    public CacheController(@Autowired UserCredentialManager userCredentialManager,
                           @Value("${baseUri}") String baseUri,
                           @Value("${activity.date.start}") String firstDay) {
        this.credentialManager = userCredentialManager;
        this.baseUri = baseUri;
        this.firstDay = LocalDate.parse(firstDay);
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    @ResponseBody
    public RateLimitStatus fitbitStatus() throws IOException {
        RateLimitStatusRepositoryFitbit repo = new RateLimitStatusRepositoryFitbit(credentialManager.getRequestFactoryForMe());

        return repo.getRateLimitStatus();
    }

    @RequestMapping(value = "/callback", method = RequestMethod.GET)
    public String fitbitAuthCallback(@RequestParam("code") String code) throws IOException {
        credentialManager.addVerifiedUser("me", code);
        return "redirect:/health/fitbit/status";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String fitbitLogin() throws IOException {
        String redirect = FitbitApi.AUTHORIZE_URL
                + "?response_type=code"
                + "&client_id=" + credentialManager.getClientCredentials().getId()
                + "&scope=" + SCOPE
                + "&expires_in=" + EXPIRATION_TIME;
        return "redirect:" + redirect;
    }

    @RequestMapping(value = "/cache")
    public Callable<Void> triggerCacheCheck() throws IOException, DaoException {
        return () -> {cacheFitbitData(); return null;};
    }

    @Scheduled(fixedRate = 1000 * 60 * 60, initialDelay = 0)
    public void cacheFitbitData() throws IOException, DaoException {
        FitbitIntradayActivityRepoFitbit intradayActivityDao = new FitbitIntradayActivityRepoFitbit(credentialManager.getRequestFactoryForMe());

        intradayActivityDao.getIntradayActivityRange(firstDay, LocalDate.now());
    }

}
