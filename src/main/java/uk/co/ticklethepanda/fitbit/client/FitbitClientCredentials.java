package uk.co.ticklethepanda.fitbit.client;

public class FitbitClientCredentials {
    private final String id;
    private final String secret;

    public FitbitClientCredentials(
            String id,
            String secret) {
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
