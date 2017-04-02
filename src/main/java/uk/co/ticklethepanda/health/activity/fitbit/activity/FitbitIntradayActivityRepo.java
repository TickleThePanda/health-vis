package uk.co.ticklethepanda.health.activity.fitbit.activity;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ticklethepanda.health.activity.fitbit.DaoException;
import uk.co.ticklethepanda.health.activity.fitbit.FitbitApi;
import uk.co.ticklethepanda.health.activity.fitbit.ratelimit.RateLimitStatus;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class FitbitIntradayActivityRepo {

    private static final Logger logger = LogManager.getLogger();

    private final static String ACTIVITIES_URL = FitbitApi.BASE_URL
            + "/user/-/activities/steps/date/%/1d.json";

    private final static String CLIENT_ACCESS_URL = FitbitApi.BASE_URL
            + "/account/clientAndViewerRateLimitStatus.json";

    private final static DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd").toFormatter();

    private final static Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                    LocalDate.parse(json.getAsString()))
            .registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>) (json, typeOfT, context) ->
                    LocalTime.parse(json.getAsString()))
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    private final HttpRequestFactory requestFactory;

    public FitbitIntradayActivityRepo(HttpRequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    public FitbitIntradayActivity getDayActivity(LocalDate date) throws DaoException {
        final GenericUrl url = new GenericUrl(ACTIVITIES_URL.replace("%", DATE_FORMATTER.format(date)));

        try {
            final HttpResponse response = this.requestFactory.buildGetRequest(url).execute();
            return GSON.fromJson(response.parseAsString(), FitbitIntradayActivity.class);

        } catch (final IOException e) {
            throw new DaoException(e);
        }
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

}
