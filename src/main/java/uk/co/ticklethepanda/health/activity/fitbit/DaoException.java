package uk.co.ticklethepanda.health.activity.fitbit;

public class DaoException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 8328466959562972043L;

    public DaoException() {
        super();
    }

    public DaoException(String message) {
        super(message);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }

    public DaoException(Throwable cause) {
        super(cause);
    }

}
