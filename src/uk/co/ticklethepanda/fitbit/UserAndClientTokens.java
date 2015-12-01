package uk.co.ticklethepanda.fitbit;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.scribe.model.Token;

public class UserAndClientTokens {
    private final Token clientToken;
    private final Token userToken;

    public UserAndClientTokens(Token clientToken, Token userToken) {
	this.clientToken = clientToken;
	this.userToken = userToken;
    }

    private static Properties getProperties() throws IOException, FileNotFoundException {
	Properties prop = new Properties();
	String propFileName = "config.properties";

	InputStream inputStream = UserAndClientTokens.class.getClassLoader().getResourceAsStream(propFileName);

	if (inputStream != null) {
	    prop.load(inputStream);
	} else {
	    throw new FileNotFoundException("The file \"" + propFileName + "\" was not found. If this is the first time running this software, this file must be created by hand");
	}
	return prop;
    }
    
    public static UserAndClientTokens loadTokensFromProperties() throws IOException {
	Properties prop = getProperties();
	
	String userKey = prop.getProperty("userKey");
	String userSecret = prop.getProperty("userSecret");
	
	String clientKey = prop.getProperty("clientKey");
	String clientSecret = prop.getProperty("clientSecret");
	
	Token userToken = new Token(userKey, userSecret);
	Token clientToken = new Token(clientKey, clientSecret);
	
	return new UserAndClientTokens(clientToken, userToken);
    }
    
    public Token getClientToken() {
	return clientToken;
    }
    
    public Token getUserToken() {
	return userToken;
    }
}