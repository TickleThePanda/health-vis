package uk.co.ticklethepanda.fitbit.dao;

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

import uk.co.ticklethepanda.fitbit.ActivityCollection;
import uk.co.ticklethepanda.fitbit.ActivityForDate;
import uk.co.ticklethepanda.fitbit.UserAndClientTokens;
import uk.co.ticklethepanda.fitbit.dao.cache.CacheLayerException;
import uk.co.ticklethepanda.fitbit.dao.cache.DayActivityCacheLayer;
import uk.co.ticklethepanda.fitbit.time.LocalDateRange;

public class DayActivityDaoWebApi implements DayActivityDao {

    private static final Logger logger = LogManager.getLogger();

    private static final String RETRY_AFTER_HEADER = "Retry-After";

    private static class RateLimitStatus {

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

	private static long parseWaitTime(Response response) {
	    return Long.parseLong(response.getHeader(RETRY_AFTER_HEADER)) * ONE_SECOND
		    + ONE_SECOND;
	}
    }
    
    private final static String ACTIVITIES_URL = FitbitApi.BASE_URL
	    + "/user/-/activities/steps/date/%/1d.json";

    private final static String CLIENT_ACCESS_URL = FitbitApi.BASE_URL
	    + "/account/clientAndViewerRateLimitStatus.json";

    private final static DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
	    .appendPattern("yyyy-MM-dd").toFormatter();

    private final static Gson GSON = new GsonBuilder()
	    .excludeFieldsWithoutExposeAnnotation().create();

    private static final int ONE_SECOND = 1000;

    private final DayActivityCacheLayer activityCache = new DayActivityCacheLayer();

    private final OAuthService service;

    private final UserAndClientTokens tokens;

    private final FitbitApi fitbitApi;

    public DayActivityDaoWebApi(final UserAndClientTokens tokens) {
	this.tokens = tokens;
	this.fitbitApi = new FitbitApi();
	
	String clientKey = tokens.getClientToken().getToken();
	String clientSecret = tokens.getClientToken().getSecret();
	
	this.service = new ServiceBuilder().apiKey(clientKey).apiSecret(clientSecret).provider(fitbitApi).build();
    }

    @Override
    public ActivityCollection getActivityRange(LocalDate start, LocalDate end)
	    throws DAOException {
	List<ActivityForDate> range = new ArrayList<ActivityForDate>();
	for (LocalDate date : new LocalDateRange(start, end)) {
	    range.add(this.getDayActivity(date));
	}
	return new ActivityCollection(range);
    }

    @Override
    public ActivityForDate getDayActivity(LocalDate date) throws DAOException {

	ActivityForDate value = null;
	try {
	    logger.debug("getting values for date " + date.toString() + " from cache.");
	    value = activityCache.getValue(date);
	} catch (CacheLayerException e) {
	    throw new DAOException("Could not day activity from cache", e);
	}

	if (value == null || !value.isFullDay()) {
	    logger.debug("getting values for date " + date.toString() + " from web.");
	    value = retrieveOnlineIntradayData(date);
	    try {
		logger.debug("saving value for date " + date.toString() + " to cache.");
		activityCache.save(value);
	    } catch (CacheLayerException e) {
		throw new DAOException("Could not save value to cache", e);
	    }
	}
	return value;

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

    private boolean isSecondResponseRequired(Response response) throws DAOException {
	
	if (response.getHeader(RETRY_AFTER_HEADER) != null) {
	    long waitTime = RateLimitStatus.parseWaitTime(response);
	    try {
		Thread.sleep(waitTime);
	    } catch (InterruptedException e) {
		throw new DAOException("Could not wait for response.", e);
	    }
	    return true;
	}
	return false;
    }

    private ActivityForDate retrieveOnlineIntradayData(LocalDate date) throws DAOException {
	Response response = this.createRequestForDate(date).send();
	
	if (isSecondResponseRequired(response)) {
	    response = this.createRequestForDate(date).send();
	}
	
	return GSON.fromJson(response.getBody(), ActivityForDate.class);
    }

    private OAuthRequest createRequestForDate(LocalDate date) {
	final OAuthRequest request = new OAuthRequest(Verb.GET,
		ACTIVITIES_URL.replace("%", DATE_FORMATTER.format(date)));

	service.signRequest(tokens.getUserToken(), request);
	return request;
    }

    @Override
    public void saveDayActivity(ActivityForDate date) throws DAOException {
	throw new DAOException("Cannot upload DayActivity to fitbit",
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
}
