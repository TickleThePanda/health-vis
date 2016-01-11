package uk.co.ticklethepanda.fitbit.example;

import java.io.IOException;
import java.time.LocalDate;

import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.logging.log4j.LogManager;

import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;

import uk.co.ticklethepanda.fitbit.activity.IntradayActivityRange;
import uk.co.ticklethepanda.fitbit.activity.MinuteActivitySeries;
import uk.co.ticklethepanda.fitbit.webapi.ClientCredentials;
import uk.co.ticklethepanda.fitbit.webapi.ClientTokenLoader;
import uk.co.ticklethepanda.fitbit.webapi.UserCredentialManager;
import uk.co.ticklethepanda.fitbit.webapi.DaoException;
import uk.co.ticklethepanda.fitbit.webapi.verifier.LocalVerifierCodeServer.LocalVerifierServerException;
import uk.co.ticklethepanda.utility.Profiler;
import uk.co.ticklethepanda.fitbit.activity.IntradayActivityDao;
import uk.co.ticklethepanda.fitbit.activity.IntradayActivityDaoWebApi;

public class ActivityToChartsDriver {
  private final static LocalDate firstDay = LocalDate.of(2014, 07, 15);

  public static void main(String[] args) throws DaoException, IOException, LocalVerifierServerException {
    
    ClientTokenLoader loader = new ClientTokenLoader();
    ClientCredentials clientCreds = loader.loadFromProperties();
    
    UserCredentialManager manager = new UserCredentialManager(clientCreds);
    
    Credential creds = manager.getCredentialsForUser("me");

    HttpRequestFactory requestFactory = manager.getHttpRequestFactoryForUser(creds);
    
    Profiler.printTimeElapsed("starting");

    final IntradayActivityDao intradayActivityDao = new IntradayActivityDaoWebApi(requestFactory);

    IntradayActivityRange intradayActivityRange =
        intradayActivityDao.getIntradayActivityRange(firstDay,
            LocalDate.now());
    Profiler.printTimeElapsed("getting activity");

    intradayActivityRange.getAverageDayActivity();
    Profiler.printTimeElapsed("calculating average activity");

    MinuteActivitySeries series =
        intradayActivityRange.getCumulativeDayActivity();
    Profiler.printTimeElapsed("calculating cumulative activity");

    intradayActivityRange.getTotalSteps();
    Profiler.printTimeElapsed("getting total steps from series");

    System.out.println("totalSteps: " + series.getTotalSteps());
    Profiler.printTimeElapsed("getting total steps from one day");
  }

}