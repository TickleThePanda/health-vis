package uk.co.ticklethepanda.fitbit.client.repos;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ticklethepanda.fitbit.client.FitbitClientException;
import uk.co.ticklethepanda.fitbit.client.FitbitApiConfig;
import uk.co.ticklethepanda.fitbit.client.model.FitbitUser;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

public class FitbitUserClient {
    private static final Logger logger = LogManager.getLogger();

    private static final String FITBIT_USER_URL = FitbitApiConfig.BASE_URL
            + "/user/-/profile.json";
    private final HttpRequestFactory requestFactory;

    private final static Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                    LocalDate.parse(json.getAsString()))
            .registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>) (json, typeOfT, context) ->
                    LocalTime.parse(json.getAsString()))
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    public FitbitUserClient(HttpRequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    public FitbitUser getAuthorisedUser() throws FitbitClientException {
        final GenericUrl url = new GenericUrl(FITBIT_USER_URL);

        try {
            final HttpResponse response = this.requestFactory.buildGetRequest(url).execute();

            String responseText = response.parseAsString();

            logger.debug("Retrieved response {}", responseText);

            JsonObject rootElement = GSON.fromJson(responseText, JsonObject.class);

            return GSON.fromJson(rootElement.get("user"), FitbitUser.class);

        } catch (final IOException e) {
            throw new FitbitClientException(e);
        }
    }
}
