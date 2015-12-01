package uk.co.ticklethepanda.fitbit.dao;


import java.time.LocalDate;

import uk.co.ticklethepanda.fitbit.ActivityCollection;
import uk.co.ticklethepanda.fitbit.ActivityForDate;

public interface DayActivityDao {

    ActivityForDate getDayActivity(LocalDate date) throws DAOException;

    ActivityCollection getActivityRange(LocalDate start, LocalDate end)
	    throws DAOException;

    void saveDayActivity(ActivityForDate date) throws DAOException;

}