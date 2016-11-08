package uk.co.ticklethepanda.activity.fitbit;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ticklethepanda.activity.utility.LocalDateRange;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;

public class FitbitIntradayActivityRepoFitbit implements FitbitIntradayActivityRepo {

    private static final Logger logger = LogManager.getLogger();

    private final static String ACTIVITIES_URL = FitbitApi.BASE_URL
            + "/user/-/activities/steps/date/%/1d.json";

    private final static String CLIENT_ACCESS_URL = FitbitApi.BASE_URL
            + "/account/clientAndViewerRateLimitStatus.json";

    private final static DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd").toFormatter();

    private final static Gson GSON = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation().create();

    private final FitbitIntradayActivityCacheLayer activityCache = new FitbitIntradayActivityCacheLayer();

    private final HttpRequestFactory requestFactory;

    public FitbitIntradayActivityRepoFitbit(HttpRequestFactory requestFactory) {
        this.requestFactory = requestFactory;

    }

    @Override
    public FitbitIntradayActivity getDayActivity(LocalDate date) throws DaoException {

        FitbitIntradayActivity value = null;
        try {
            logger.info("getting values for date " + date.toString() + " from cache.");
            value = this.activityCache.getValue(date);
        } catch (final CacheLayerException e) {
            throw new DaoException("Could not day activity from cache", e);
        }

        if (value == null || !value.isFullDay()) {
            logger.info("getting values for date " + date.toString() + " from web.");
            value = this.retrieveOnlineIntradayData(date);
            try {
                logger.info("saving value for date " + date.toString() + " to cache.");
                this.activityCache.save(value);
            } catch (final CacheLayerException e) {
                throw new DaoException("Could not save value to cache", e);
            }
        }
        return value;

    }

    @Override
    public FitbitIntradayActivityRange getIntradayActivityRange(LocalDate start, LocalDate end)
            throws DaoException {
        logger.info("getting values for dates" + start.toString() + " to " + end.toString());
        final List<FitbitIntradayActivity> range = new ArrayList<>();
        for (final LocalDate date : new LocalDateRange(start, end)) {
            range.add(this.getDayActivity(date));
        }
        return new FitbitIntradayActivityRange(range);
    }

    public boolean isAvailable() {
        boolean available = false;

        final GenericUrl url = new GenericUrl(CLIENT_ACCESS_URL);

        HttpRequest request = null;
        try {
            request = this.requestFactory.buildGetRequest(url);
        } catch (final IOException e) {
            return false;
        }

        RateLimitStatus status = null;
        try {
            status = GSON.fromJson(request.execute().parseAsString(), RateLimitStatus.class);
        } catch (final IOException e) {
            return false;
        }

        if (status.hasRemainingHits()) {
            available = true;
        }

        return available;
    }

    public void saveDayActivity(FitbitIntradayActivity activity) throws DaoException {
        throw new DaoException("Cannot upload DayActivity to fitbit",
                new UnsupportedOperationException(
                        "Cannot upload Day Activity to fitbit"));
    }

    private FitbitIntradayActivity retrieveOnlineIntradayData(LocalDate date) throws DaoException {
        final GenericUrl url = new GenericUrl(ACTIVITIES_URL.replace("%", DATE_FORMATTER.format(date)));

        try {
            final HttpResponse response = this.requestFactory.buildGetRequest(url).execute();
            return GSON.fromJson(response.parseAsString(), FitbitIntradayActivity.class);

        } catch (final IOException e) {
            throw new DaoException(e);
        }
    }
}
