package uk.co.ticklethepanda.fitbit.webapi;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;

public class FitbitApi extends DefaultApi10a {
    public static final String AUTHORIZE_URL = "https://www.fitbit.com/oauth/authorize?oauth_token=%s";
    public static final String BASE_URL = "https://api.fitbit.com/1";
    
    public String getAccessTokenEndpoint() {
	return "https://api.fitbit.com/oauth/access_token";
    }

    public String getRequestTokenEndpoint() {
	return "https://api.fitbit.com/oauth/request_token";
    }

    public String getAuthorizationUrl(Token requestToken) {
	return String.format(AUTHORIZE_URL, requestToken.getToken());
    }
}
