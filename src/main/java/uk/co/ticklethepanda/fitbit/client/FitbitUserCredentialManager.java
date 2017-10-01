package uk.co.ticklethepanda.fitbit.client;

import com.google.api.client.auth.oauth2.*;
import com.google.api.client.auth.oauth2.Credential.Builder;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;

public class FitbitUserCredentialManager {

    private static final Logger logger = LogManager.getLogger();

    private static final HttpTransport transport = new NetHttpTransport();
    private static final JsonFactory gsonFactory = new GsonFactory();
    private static final GenericUrl tokenEndPoint = new GenericUrl(FitbitApiConfig.TOKEN_ENDPOINT);


    private final FitbitClientCredentials credentials;
    private final BasicAuthentication basicAuthentication;

    private final AuthorizationCodeFlow flow;

    private final Builder credentialBuilder;
    private final DataStoreFactory dataStoreFactory;

    public FitbitUserCredentialManager(
            DataStoreFactory dataStoreFactory,
            FitbitClientCredentials credentials) throws IOException {

        this.dataStoreFactory = dataStoreFactory;
        this.credentials = credentials;

        this.basicAuthentication =
                new BasicAuthentication(credentials.getId(), credentials.getSecret());

        this.flow = new AuthorizationCodeFlow.Builder(
                BearerToken.authorizationHeaderAccessMethod(),
                transport,
                gsonFactory,
                tokenEndPoint,
                this.basicAuthentication,
                this.credentials.getId(),
                FitbitApiConfig.AUTHORIZE_URL)
                .setScopes(Arrays.asList("activity"))
                .setDataStoreFactory(dataStoreFactory)
                .build();

        this.credentialBuilder = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                .setClientAuthentication(this.basicAuthentication)
                .setJsonFactory(gsonFactory)
                .setTransport(transport)
                .setTokenServerUrl(tokenEndPoint);
    }

    public Credential getCredentialsForUser(String userId) throws IOException {
        final StoredCredential storedCred = this.flow.getCredentialDataStore().get(userId);

        return this.credentialBuilder
                .addRefreshListener(new DataStoreCredentialRefreshListener(userId, dataStoreFactory))
                .build()
                .setAccessToken(storedCred.getAccessToken())
                .setRefreshToken(storedCred.getRefreshToken());
    }

    public HttpRequestFactory getHttpRequestFactory(final Credential credential) {
        return transport.createRequestFactory(credential::initialize);
    }

    public HttpRequestFactory getHttpRequestFactoryForUser(String userId) throws IOException {
        return getHttpRequestFactory(getCredentialsForUser(userId));
    }

    public void addVerifiedUser(String user, String verificationCode) throws IOException {
        final AuthorizationCodeTokenRequest request =
                this.flow.newTokenRequest(verificationCode);

        final TokenResponse response = request.execute();

        this.flow.createAndStoreCredential(response, user);

    }

    public FitbitClientCredentials getClientCredentials() {
        return this.credentials;
    }
}
