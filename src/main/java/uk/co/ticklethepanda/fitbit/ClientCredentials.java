package uk.co.ticklethepanda.fitbit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ClientCredentials {
  private final String id;
  private final String secret;

  public ClientCredentials(@Value("${clientKey}") String id, @Value("${clientSecret}")String secret) {
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
