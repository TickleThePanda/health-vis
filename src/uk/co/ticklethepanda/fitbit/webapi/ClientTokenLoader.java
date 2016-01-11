package uk.co.ticklethepanda.fitbit.webapi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ClientTokenLoader {

  private final Properties properties;

  public ClientTokenLoader() throws FileNotFoundException, IOException {
    this.properties = this.loadProperties();
  }

  public ClientCredentials loadFromProperties() throws IOException {
    final String userKey = this.properties.getProperty("clientKey");
    final String userSecret = this.properties.getProperty("clientSecret");

    final ClientCredentials userToken = new ClientCredentials(userKey, userSecret);
    return userToken;
  }

  private Properties loadProperties() throws IOException, FileNotFoundException {
    final Properties prop = new Properties();
    final String propFileName = "config.properties";

    final InputStream inputStream = new FileInputStream(propFileName);

    prop.load(inputStream);

    return prop;
  }

}
