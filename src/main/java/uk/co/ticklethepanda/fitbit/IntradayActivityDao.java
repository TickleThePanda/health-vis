package uk.co.ticklethepanda.fitbit;

import java.time.LocalDate;

public interface IntradayActivityDao {

  IntradayActivity getDayActivity(LocalDate date) throws DaoException;

  IntradayActivityRange getIntradayActivityRange(LocalDate start, LocalDate end)
      throws DaoException;

}