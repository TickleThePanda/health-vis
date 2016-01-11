package uk.co.ticklethepanda.fitbit.activity;

import java.time.LocalDate;

import uk.co.ticklethepanda.fitbit.webapi.DaoException;

public interface IntradayActivityDao {

  IntradayActivity getDayActivity(LocalDate date) throws DaoException;

  IntradayActivityRange getIntradayActivityRange(LocalDate start, LocalDate end)
      throws DaoException;

}