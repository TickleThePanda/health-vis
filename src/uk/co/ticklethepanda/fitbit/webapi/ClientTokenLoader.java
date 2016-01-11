package uk.co.ticklethepanda.fitbit.webapi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ClientTokenLoader {

  private Properties properties;

  public ClientTokenLoader() throws FileNotFoundException, IOException {
    this.properties = loadProperties();
  }

  private Properties loadProperties() throws IOException, FileNotFoundException {
    Properties prop = new Properties();
    String propFileName = "config.properties";

    InputStream inputStream = new FileInputStream(propFileName);

    prop.load(inputStream);
    
    return prop;
  }

  public ClientCredentials loadFromProperties() throws IOException {
    String userKey = properties.getProperty("clientKey");
    String userSecret = properties.getProperty("clientSecret");

    ClientCredentials userToken = new ClientCredentials(userKey, userSecret);
    return userToken;
  }

}
