package uk.co.ticklethepanda.fitbit.activity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.scribe.builder.ServiceBuilder;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import uk.co.ticklethepanda.fitbit.caching.CacheLayerException;
import uk.co.ticklethepanda.fitbit.webapi.DaoException;
import uk.co.ticklethepanda.fitbit.webapi.FitbitApi;
import uk.co.ticklethepanda.fitbit.webapi.UserAndClientTokens;
import uk.co.ticklethepanda.utility.LocalDateRange;

public class IntradayActivityDaoWebApi implements IntradayActivityDao {

  private static class RateLimitStatus {

    private static long parseWaitTime(Response response) {
      return Long.parseLong(response.getHeader(RETRY_AFTER_HEADER)) * ONE_SECOND
          + ONE_SECOND;
    }

    @Expose
    private final int hourlyLimit;
    @Expose
    private final int remainingHits;

    @Expose
    private final LocalDate resetTime;

    private RateLimitStatus(int hourlyLimit, int remainingHits,
        LocalDate resetTime) {
      this.hourlyLimit = hourlyLimit;
      this.remainingHits = remainingHits;
      this.resetTime = resetTime;
    }

    public int getRemainingHits() {
      return remainingHits;
    }

    public boolean hasRemainingHits() {
      return remainingHits > 0;
    }
  }

  private static final Logger logger = LogManager.getLogger();

  private static final String RETRY_AFTER_HEADER = "Retry-After";

  private final static String ACTIVITIES_URL = FitbitApi.BASE_URL
      + "/user/-/activities/steps/date/%/1d.json";

  private final static String CLIENT_ACCESS_URL = FitbitApi.BASE_URL
      + "/account/clientAndViewerRateLimitStatus.json";

  private final static DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
      .appendPattern("yyyy-MM-dd").toFormatter();

  private final static Gson GSON = new GsonBuilder()
      .excludeFieldsWithoutExposeAnnotation().create();

  private static final int ONE_SECOND = 1000;

  private final IntradayActivityCacheLayer activityCache = new IntradayActivityCacheLayer();

  private final OAuthService service;

  private final UserAndClientTokens tokens;

  private final FitbitApi fitbitApi;

  public IntradayActivityDaoWebApi(final UserAndClientTokens tokens) {
    this.tokens = tokens;
    this.fitbitApi = new FitbitApi();

    String clientKey = tokens.getClientToken().getToken();
    String clientSecret = tokens.getClientToken().getSecret();

    this.service = new ServiceBuilder().apiKey(clientKey).apiSecret(clientSecret).provider(fitbitApi).build();
  }

  @Override
  public IntradayActivity getDayActivity(LocalDate date) throws DaoException {

    IntradayActivity value = null;
    try {
      logger.debug("getting values for date " + date.toString() + " from cache.");
      value = activityCache.getValue(date);
    } catch (CacheLayerException e) {
      throw new DaoException("Could not day activity from cache", e);
    }

    if (value == null || !value.isFullDay()) {
      logger.debug("getting values for date " + date.toString() + " from web.");
      value = retrieveOnlineIntradayData(date);
      try {
        logger.debug("saving value for date " + date.toString() + " to cache.");
        activityCache.save(value);
      } catch (CacheLayerException e) {
        throw new DaoException("Could not save value to cache", e);
      }
    }
    return value;

  }

  @Override
  public IntradayActivityRange getIntradayActivityRange(LocalDate start, LocalDate end)
      throws DaoException {
    List<IntradayActivity> range = new ArrayList<IntradayActivity>();
    for (LocalDate date : new LocalDateRange(start, end)) {
      range.add(this.getDayActivity(date));
    }
    return new IntradayActivityRange(range);
  }

  public boolean isAvailable() {
    boolean available = false;

    Response response = null;
    try {
      response = createRemainingRequest().send();
    } catch (OAuthException e) {
      available = false;
    }

    RateLimitStatus status = GSON.fromJson(response.getBody(),
        RateLimitStatus.class);

    if (status.hasRemainingHits()) {
      available = true;
    }

    return available;
  }

  @Override
  public void saveDayActivity(IntradayActivity activity) throws DaoException {
    throw new DaoException("Cannot upload DayActivity to fitbit",
        new UnsupportedOperationException(
            "Cannot upload Day Activity to fitbit"));
  }

  private OAuthRequest createRemainingRequest() {
    OAuthRequest remainingRequest = new OAuthRequest(
        Verb.GET, CLIENT_ACCESS_URL);

    service.signRequest(tokens.getUserToken(), remainingRequest);
    remainingRequest.setConnectTimeout(10, TimeUnit.SECONDS);
    remainingRequest.setReadTimeout(10, TimeUnit.SECONDS);

    return remainingRequest;
  }

  private OAuthRequest createRequestForDate(LocalDate date) {
    final OAuthRequest request = new OAuthRequest(Verb.GET,
        ACTIVITIES_URL.replace("%", DATE_FORMATTER.format(date)));

    service.signRequest(tokens.getUserToken(), request);
    return request;
  }

  private boolean isSecondResponseRequired(Response response) throws DaoException {

    if (response.getHeader(RETRY_AFTER_HEADER) != null) {
      long waitTime = RateLimitStatus.parseWaitTime(response);
      try {
        Thread.sleep(waitTime);
      } catch (InterruptedException e) {
        throw new DaoException("Could not wait for response.", e);
      }
      return true;
    }
    return false;
  }

  private IntradayActivity retrieveOnlineIntradayData(LocalDate date) throws DaoException {
    Response response = this.createRequestForDate(date).send();

    if (isSecondResponseRequired(response)) {
      response = this.createRequestForDate(date).send();
    }

    return GSON.fromJson(response.getBody(), IntradayActivity.class);
  }
}
