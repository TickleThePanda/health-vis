package uk.co.ticklethepanda.fitbit.dao;

public class DAOException extends Exception {

    /**
    * 
    */
    private static final long serialVersionUID = 8328466959562972043L;

    public DAOException() {
	super();
    }

    public DAOException(String message) {
	super(message);
    }

    public DAOException(String message, Throwable cause) {
	super(message, cause);
    }

    public DAOException(Throwable cause) {
	super(cause);
    }

}
