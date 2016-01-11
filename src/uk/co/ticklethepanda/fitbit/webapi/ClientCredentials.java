package uk.co.ticklethepanda.fitbit.webapi;

public class ClientCredentials {

  private String id;
  private String secret;

  public ClientCredentials(String id, String secret) {
    this.id = id;
    this.secret = secret;
  }

  public String getId() {
    return id;
  }

  public String getSecret() {
    return secret;
  }
  
  
}
