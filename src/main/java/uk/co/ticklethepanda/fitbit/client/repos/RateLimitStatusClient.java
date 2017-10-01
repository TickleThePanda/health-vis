package uk.co.ticklethepanda.fitbit.client.repos;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.gson.Gson;
import uk.co.ticklethepanda.fitbit.client.FitbitApiConfig;
import uk.co.ticklethepanda.fitbit.client.model.RateLimitStatus;

import java.io.IOException;

/**
 *
 */
public class RateLimitStatusClient {

    private static final Gson GSON = new Gson();

    private final HttpRequestFactory requestFactory;

    public RateLimitStatusClient(HttpRequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    public RateLimitStatus getRateLimitStatus() throws IOException {
        final GenericUrl url = new GenericUrl(
                FitbitApiConfig.BASE_URL + "/account/clientAndViewerRateLimitStatus.json");

        HttpRequest request = requestFactory.buildGetRequest(url);

        return GSON.fromJson(
                request.execute().parseAsString(),
                RateLimitStatus.class);
    }
}
