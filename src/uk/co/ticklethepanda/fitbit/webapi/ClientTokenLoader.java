package uk.co.ticklethepanda.fitbit.webapi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ClientTokenLoader {

  private final Properties properties;

  public ClientTokenLoader() throws IOException {
    this.properties = this.loadProperties();
  }

  public ClientCredentials loadFromProperties() {
    final String userKey = this.properties.getProperty("clientKey");
    final String userSecret = this.properties.getProperty("clientSecret");

    return new ClientCredentials(userKey, userSecret);
  }

  private Properties loadProperties() throws IOException {
    final Properties prop = new Properties();
    final String propFileName = "config.properties";

    final InputStream inputStream = new FileInputStream(propFileName);

    prop.load(inputStream);

    return prop;
  }

}
