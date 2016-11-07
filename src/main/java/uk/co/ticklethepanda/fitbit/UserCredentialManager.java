package uk.co.ticklethepanda.fitbit;

import com.google.api.client.auth.oauth2.*;
import com.google.api.client.auth.oauth2.Credential.Builder;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Component
public class UserCredentialManager {

  private static final Logger logger = LogManager.getLogger();

  private static final FileDataStoreFactory DATA_STORE;

  static {
    FileDataStoreFactory factory = null;
    try {
      factory = new FileDataStoreFactory(new File("auths"));
    } catch (final IOException e) {
      e.printStackTrace();
    }

    DATA_STORE = factory;
  }

  private static final HttpTransport transport = new NetHttpTransport();

  private static final JsonFactory gsonFactory = new GsonFactory();
  private static final GenericUrl tokenEndPoint = new GenericUrl(FitbitApi.TOKEN_ENDPOINT);
  private final ClientCredentials credentials;
  private final BasicAuthentication basicAuthentication;

  private final AuthorizationCodeFlow flow;

  private final Builder credentialBuilder;

  public UserCredentialManager(@Autowired ClientCredentials credentials) throws IOException {

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
            FitbitApi.AUTHORIZE_URL)
            .setScopes(Arrays.asList( "activity" ))
            .setDataStoreFactory(DATA_STORE)
            .build();

    this.credentialBuilder = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
        .setClientAuthentication(this.basicAuthentication)
        .setJsonFactory(gsonFactory)
        .setTransport(transport)
        .setTokenServerUrl(tokenEndPoint);
  }

  public Credential getCredentialsForUser(String userId) throws IOException {
    final StoredCredential storedCred = this.flow.getCredentialDataStore().get("me");

    return this.credentialBuilder
        .addRefreshListener(new DataStoreCredentialRefreshListener(userId, DATA_STORE))
        .build()
        .setAccessToken(storedCred.getAccessToken())
        .setRefreshToken(storedCred.getRefreshToken());
  }

  public HttpRequestFactory getHttpRequestFactory(final Credential credential) {
    return transport.createRequestFactory(request -> {
      credential.initialize(request);
    });
  }

  public void addVerifiedUser(String user, String verificationCode) throws IOException {
    final AuthorizationCodeTokenRequest request =
            this.flow.newTokenRequest(verificationCode);

    final TokenResponse response = request.execute();

    this.flow.createAndStoreCredential(response, user);

  }

  public ClientCredentials getClientCredentials() {
    return this.credentials;
  }
}
