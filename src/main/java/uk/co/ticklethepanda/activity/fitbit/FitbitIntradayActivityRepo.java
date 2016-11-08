package uk.co.ticklethepanda.activity.fitbit;

import java.time.LocalDate;

public interface FitbitIntradayActivityRepo {

    FitbitIntradayActivity getDayActivity(LocalDate date) throws DaoException;

}