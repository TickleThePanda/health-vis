package uk.co.ticklethepanda.fitbit;

import com.google.api.client.http.HttpRequestFactory;

public class IntradayActivityDaoFactory {

    private IntradayActivityDaoFactory() {
    }

    public static IntradayActivityDao getDao(HttpRequestFactory factory) {
        return new IntradayActivityDaoWebApi(factory);
    }

}
