package uk.co.ticklethepanda.fitbit.webapi;

public class ClientCredentials {

  private final String id;
  private final String secret;

  public ClientCredentials(String id, String secret) {
    this.id = id;
    this.secret = secret;
  }

  public String getId() {
    return this.id;
  }

  public String getSecret() {
    return this.secret;
  }

}
