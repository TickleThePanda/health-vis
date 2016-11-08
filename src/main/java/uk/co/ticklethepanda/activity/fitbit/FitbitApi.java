package uk.co.ticklethepanda.activity.fitbit;

public class FitbitApi {
    public static final String AUTHORIZE_URL = "https://www.fitbit.com/oauth2/authorize";
    public static final String TOKEN_ENDPOINT = "https://api.fitbit.com/oauth2/token";
    public static final String BASE_URL = "https://api.fitbit.com/1";

    public final static String RATE_LIMIT_STATUS = FitbitApi.BASE_URL
            + "/account/clientAndViewerRateLimitStatus.json";
}
