package uk.co.ticklethepanda.fitbit.example;

import java.io.IOException;
import java.time.LocalDate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpRequestFactory;

import uk.co.ticklethepanda.fitbit.activity.IntradayActivityDao;
import uk.co.ticklethepanda.fitbit.activity.IntradayActivityDaoWebApi;
import uk.co.ticklethepanda.fitbit.activity.IntradayActivityRange;
import uk.co.ticklethepanda.fitbit.activity.MinuteActivitySeries;
import uk.co.ticklethepanda.fitbit.webapi.ClientCredentials;
import uk.co.ticklethepanda.fitbit.webapi.ClientTokenLoader;
import uk.co.ticklethepanda.fitbit.webapi.DaoException;
import uk.co.ticklethepanda.fitbit.webapi.UserCredentialManager;
import uk.co.ticklethepanda.fitbit.webapi.verifier.LocalVerifierCodeServer.LocalVerifierServerException;

public class ActivityToChartsDriver {
  private final static LocalDate firstDay = LocalDate.of(2014, 07, 15);

  private final static Logger logger = LogManager.getLogger();

  public static void main(String[] args) throws DaoException, IOException, LocalVerifierServerException {

    logger.info("setting up connections, etc");

    final ClientTokenLoader loader = new ClientTokenLoader();
    final ClientCredentials clientCreds = loader.loadFromProperties();

    final UserCredentialManager manager = new UserCredentialManager(clientCreds);

    final Credential creds = manager.getCredentialsForUser("me");
    creds.refreshToken();

    final HttpRequestFactory requestFactory = manager.getHttpRequestFactory(creds);

    logger.info("getting activity");

    final IntradayActivityDao intradayActivityDao = new IntradayActivityDaoWebApi(requestFactory);

    final IntradayActivityRange intradayActivityRange =
        intradayActivityDao.getIntradayActivityRange(firstDay,
            LocalDate.now());

    logger.info("calculating average activity");
    intradayActivityRange.getAverageDayActivity();

    logger.info("calculating cumulative activity");
    final MinuteActivitySeries series =
        intradayActivityRange.getCumulativeDayActivity();

    logger.info("getting total steps from series");
    intradayActivityRange.getTotalSteps();

    logger.info("totalSteps: " + series.getTotalSteps());
  }

}