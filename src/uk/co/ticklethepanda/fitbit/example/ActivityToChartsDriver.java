package uk.co.ticklethepanda.fitbit.example;

import java.io.IOException;
import java.time.LocalDate;

import uk.co.ticklethepanda.fitbit.activity.IntradayActivityRange;
import uk.co.ticklethepanda.fitbit.activity.MinuteActivitySeries;
import uk.co.ticklethepanda.fitbit.webapi.DaoException;
import uk.co.ticklethepanda.fitbit.webapi.UserAndClientTokens;
import uk.co.ticklethepanda.utility.Profiler;
import uk.co.ticklethepanda.fitbit.activity.IntradayActivityDao;
import uk.co.ticklethepanda.fitbit.activity.IntradayActivityDaoWebApi;

public class ActivityToChartsDriver {
  private final static LocalDate firstDay = LocalDate.of(2014, 07, 15);

  public static void main(String[] args) throws DaoException, IOException {

    UserAndClientTokens tokens = UserAndClientTokens.loadTokensFromProperties();

    Profiler.printTimeElapsed("starting");

    final IntradayActivityDao intradayActivityDao = new IntradayActivityDaoWebApi(tokens);

    IntradayActivityRange intradayActivityRange = intradayActivityDao.getIntradayActivityRange(firstDay,
        LocalDate.now());
    Profiler.printTimeElapsed("getting activity");

    intradayActivityRange.getAverageDayActivity();
    Profiler.printTimeElapsed("calculating average activity");

    MinuteActivitySeries series = intradayActivityRange.getCumulativeDayActivity();
    Profiler.printTimeElapsed("calculating cumulative activity");

    intradayActivityRange.getTotalSteps();
    Profiler.printTimeElapsed("getting total steps from series");

    System.out.println("totalSteps: " + series.getTotalSteps());
    Profiler.printTimeElapsed("getting total steps from one day");
  }

}