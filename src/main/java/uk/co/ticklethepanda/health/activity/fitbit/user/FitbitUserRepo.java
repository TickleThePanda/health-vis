package uk.co.ticklethepanda.health.activity.fitbit.user;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import uk.co.ticklethepanda.health.activity.fitbit.DaoException;
import uk.co.ticklethepanda.health.activity.fitbit.FitbitApi;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

public class FitbitUserRepo {
    private static final String FITBIT_USER_URL = FitbitApi.BASE_URL
            + "/user/-/profile.json";
    private final HttpRequestFactory requestFactory;

    private final static Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                    LocalDate.parse(json.getAsString()))
            .registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>) (json, typeOfT, context) ->
                    LocalTime.parse(json.getAsString()))
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    public FitbitUserRepo(HttpRequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    public FitbitUser getAuthorisedUser() throws DaoException {
        final GenericUrl url = new GenericUrl(FITBIT_USER_URL);

        try {
            final HttpResponse response = this.requestFactory.buildGetRequest(url).execute();
            return GSON.fromJson(response.parseAsString(), FitbitUser.class);

        } catch (final IOException e) {
            throw new DaoException(e);
        }
    }
}
