package uk.co.ticklethepanda.activity.fitbit;

import java.time.LocalDate;

public interface FitbitIntradayActivityRepo {

    FitbitIntradayActivity getDayActivity(LocalDate date) throws DaoException;

    FitbitIntradayActivityRange getIntradayActivityRange(LocalDate start, LocalDate end)
            throws DaoException;

}