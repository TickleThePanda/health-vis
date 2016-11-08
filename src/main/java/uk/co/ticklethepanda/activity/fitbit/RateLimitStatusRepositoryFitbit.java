package uk.co.ticklethepanda.activity.fitbit;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.gson.Gson;

import java.io.IOException;

/**
 * Created by panda on 07/11/2016.
 */
public class RateLimitStatusRepositoryFitbit {

    private static final Gson GSON = new Gson();

    private final HttpRequestFactory requestFactory;

    public RateLimitStatusRepositoryFitbit(HttpRequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    public RateLimitStatus getRateLimitStatus() throws IOException {
        final GenericUrl url = new GenericUrl(FitbitApi.RATE_LIMIT_STATUS);

        HttpRequest request = requestFactory.buildGetRequest(url);

        return GSON.fromJson(
                request.execute().parseAsString(),
                RateLimitStatus.class);
    }
}
