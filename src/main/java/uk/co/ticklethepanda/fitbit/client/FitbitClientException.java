package uk.co.ticklethepanda.fitbit.client;

public class FitbitClientException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 8328466959562972043L;

    public FitbitClientException() {
        super();
    }

    public FitbitClientException(String message) {
        super(message);
    }

    public FitbitClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public FitbitClientException(Throwable cause) {
        super(cause);
    }

}
