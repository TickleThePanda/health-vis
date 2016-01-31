package uk.co.ticklethepanda.fitbit.webapi;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.Credential.Builder;
import com.google.api.client.auth.oauth2.DataStoreCredentialRefreshListener;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.common.primitives.Ints;

import uk.co.ticklethepanda.fitbit.webapi.verifier.LocalVerifierCodeServer;
import uk.co.ticklethepanda.fitbit.webapi.verifier.LocalVerifierCodeServer.LocalVerifierServerException;

public class UserCredentialManager {

  private static final Logger logger = LogManager.getLogger();

  private static final int USAGE_LIMIT_REACHED = 429;
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

  private static final String AUTHORIZE_URL = "https://www.fitbit.com/oauth2/authorize";
  private static final String TOKEN_ENDPOINT = "https://api.fitbit.com/oauth2/token";
  public static final String BASE_URL = "https://api.fitbit.com/1";

  private static final HttpTransport transport = new NetHttpTransport();

  private static final JsonFactory gsonFactory = new GsonFactory();
  private static final GenericUrl tokenEndPoint = new GenericUrl(TOKEN_ENDPOINT);
  private final ClientCredentials credentials;
  private final BasicAuthentication basicAuthentication;

  private final AuthorizationCodeFlow flow;

  private final Builder credentialBuilder;

  public UserCredentialManager(ClientCredentials credentials) throws IOException {

    this.credentials = credentials;

    this.basicAuthentication =
        new BasicAuthentication(credentials.getId(), credentials.getSecret());

    this.flow = this.initialiseAuthorizationCodeFlow();
    this.credentialBuilder = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
        .setClientAuthentication(this.basicAuthentication)
        .setJsonFactory(gsonFactory)
        .setTransport(transport)
        .setTokenServerUrl(tokenEndPoint);
  }

  public Credential getCredentialsForUser(String userId) throws IOException, LocalVerifierServerException {

    if (!this.flow.getCredentialDataStore().containsKey(userId)) {
      this.getCredentialsFromRemote();
    }

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

      request.setUnsuccessfulResponseHandler((unsuccessfulRequest, response, supportsRetry) -> {
        if (response.getStatusCode() == USAGE_LIMIT_REACHED) {
          this.waitForUsageLimitReset(response);
          return true;
        }
        logger.info("using the credentials to handle unsuccessful response");
        return credential.handleResponse(unsuccessfulRequest, response, supportsRetry);
      });
    });
  }

  private void getCredentialsFromRemote() throws LocalVerifierServerException, IOException {
    final String verificationCode = LocalVerifierCodeServer.getVerificationCodeUsingSingleUseServer();

    final AuthorizationCodeTokenRequest request =
        this.flow.newTokenRequest(verificationCode);

    final TokenResponse response = request.execute();

    this.flow.createAndStoreCredential(response, "me");
  }

  private AuthorizationCodeFlow initialiseAuthorizationCodeFlow() throws IOException {
    return new AuthorizationCodeFlow.Builder(
        BearerToken.authorizationHeaderAccessMethod(),
        transport,
        gsonFactory,
        tokenEndPoint,
        this.basicAuthentication,
        this.credentials.getId(),
        AUTHORIZE_URL)
            .setScopes(Arrays.asList( "activity" ))
            .setDataStoreFactory(DATA_STORE)
            .build();
  }

  private void waitForUsageLimitReset(HttpResponse response) {
    final String retryAfterText = response.getHeaders().getRetryAfter();
    if (retryAfterText != null) {
      final int retryAfterSeconds = Ints.tryParse(retryAfterText);
      final long retryAfterMilliseconds = retryAfterSeconds * 1000 + 1000;
      logger.info("about to wait for " + retryAfterMilliseconds);
      try {
        Thread.sleep(retryAfterMilliseconds);
      } catch (final Exception e) {
        e.printStackTrace();
      }
    }
  }

}
