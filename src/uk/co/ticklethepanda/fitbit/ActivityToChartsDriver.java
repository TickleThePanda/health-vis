package uk.co.ticklethepanda.fitbit;

import java.io.IOException;
import java.time.LocalDate;

import uk.co.ticklethepanda.fitbit.dao.DAOException;
import uk.co.ticklethepanda.fitbit.dao.DayActivityDao;
import uk.co.ticklethepanda.fitbit.dao.DayActivityDaoWebApi;

public class ActivityToChartsDriver {
    private final static LocalDate firstDay = LocalDate.of(2014, 07, 15);

    public static void main(String[] args) throws DAOException, IOException {
	
	UserAndClientTokens tokens = UserAndClientTokens.loadTokensFromProperties();
	
	Profiler.printTimeElapsed("starting");

	final DayActivityDao dayActivityDao = new DayActivityDaoWebApi(tokens);

	ActivityCollection activityRange = dayActivityDao.getActivityRange(firstDay,
		LocalDate.now());
	Profiler.printTimeElapsed("getting activity");

	activityRange.getAverageDayActivity();
	Profiler.printTimeElapsed("calculating average activity");

	MinuteActivitySeries series = activityRange.getCumulativeDayActivity();
	Profiler.printTimeElapsed("calculating cumulative activity");

	activityRange.getTotalSteps();
	Profiler.printTimeElapsed("getting total steps from series");

	System.out.println("totalSteps: " + series.getTotalSteps());
	Profiler.printTimeElapsed("getting total steps from one day");
    }

    
    
}