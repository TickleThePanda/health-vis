package uk.co.ticklethepanda.fitbit.webapi;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.http.auth.Credentials;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialRefreshListener;
import com.google.api.client.auth.oauth2.DataStoreCredentialRefreshListener;
import com.google.api.client.auth.oauth2.Credential.Builder;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.auth.oauth2.TokenErrorResponse;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import uk.co.ticklethepanda.fitbit.webapi.verifier.LocalVerifierCodeServer;
import uk.co.ticklethepanda.fitbit.webapi.verifier.LocalVerifierCodeServer.LocalVerifierServerException;

public class UserCredentialManager {

  public static final FileDataStoreFactory DATA_STORE;

  static {
    FileDataStoreFactory factory = null;
    try {
      factory = new FileDataStoreFactory(new File("auths"));
    } catch (IOException e) {
      e.printStackTrace();
    }

    DATA_STORE = factory;
  }

  public static final String AUTHORIZE_URL = "https://www.fitbit.com/oauth2/authorize";
  public static final String TOKEN_ENDPOINT = "https://api.fitbit.com/oauth2/token";
  public static final String BASE_URL = "https://api.fitbit.com/1";

  private ClientCredentials credentials;

  private static HttpTransport transport = new NetHttpTransport();
  private static JsonFactory gsonFactory = new GsonFactory();
  private static GenericUrl tokenEndPoint = new GenericUrl(TOKEN_ENDPOINT);
  private BasicAuthentication basicAuthentication;

  private AuthorizationCodeFlow flow;

  private final Builder credentialBuilder;

  public UserCredentialManager(ClientCredentials credentials) throws IOException {

    this.credentials = credentials;

    this.basicAuthentication =
        new BasicAuthentication(credentials.getId(), credentials.getSecret());

    this.flow = initialiseAuthorizationCodeFlow();
    this.credentialBuilder = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
        .setClientAuthentication(basicAuthentication)
        .setJsonFactory(gsonFactory)
        .setTransport(transport)
        .setTokenServerUrl(tokenEndPoint);
  }

  private AuthorizationCodeFlow initialiseAuthorizationCodeFlow() throws IOException {
    return new AuthorizationCodeFlow.Builder(
        BearerToken.authorizationHeaderAccessMethod(),
        transport,
        gsonFactory,
        tokenEndPoint,
        basicAuthentication,
        credentials.getId(),
        AUTHORIZE_URL)
            .setScopes(Arrays.asList(new String[] { "activity" }))
            .setDataStoreFactory(DATA_STORE)
            .build();
  }

  public Credential getCredentialsForUser(String userId) throws IOException, LocalVerifierServerException {

    if (!flow.getCredentialDataStore().containsKey(userId)) {
      getCredentialsFromRemote();
    }

    StoredCredential storedCred = flow.getCredentialDataStore().get("me");

    return credentialBuilder
        .addRefreshListener(new DataStoreCredentialRefreshListener(userId, DATA_STORE))
        .build()
        .setAccessToken(storedCred.getAccessToken())
        .setRefreshToken(storedCred.getRefreshToken());
  }

  private void getCredentialsFromRemote() throws LocalVerifierServerException, IOException {
    String verificationCode = LocalVerifierCodeServer.getVerificationCodeUsingSingleUseServer();

    AuthorizationCodeTokenRequest request =
        flow.newTokenRequest(verificationCode);

    TokenResponse response = request.execute();

    flow.createAndStoreCredential(response, "me");
  }

  public HttpRequestFactory getHttpRequestFactoryForUser(final Credential creds) {
    return transport.createRequestFactory(request -> {

      creds.initialize(request);
      request.setResponseInterceptor(response -> {
        creds.handleResponse(request, response, true);
      });
    });
  }

}
