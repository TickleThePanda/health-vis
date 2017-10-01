package uk.co.ticklethepanda.health;

import com.google.api.client.util.store.FileDataStoreFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.ticklethepanda.fitbit.client.FitbitClientCredentials;
import uk.co.ticklethepanda.fitbit.client.FitbitUserCredentialManager;

import java.io.File;
import java.io.IOException;

@Configuration
public class FitbitClientConfig {

    private final String id;
    private final String secret;

    public FitbitClientConfig(
            @Value("${fitbit.api.client.key}") String id,
            @Value("${fitbit.api.client.secret}") String secret) {
        this.id = id;
        this.secret = secret;
    }

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
    @Bean
    public FitbitUserCredentialManager clientCredentials() throws IOException {
        FileDataStoreFactory dataStore = new FileDataStoreFactory(new File("auths"));

        FitbitClientCredentials fitbitFitbitClientCredentials = new FitbitClientCredentials(id, secret);

        return new FitbitUserCredentialManager(dataStore, fitbitFitbitClientCredentials);
    }


}
