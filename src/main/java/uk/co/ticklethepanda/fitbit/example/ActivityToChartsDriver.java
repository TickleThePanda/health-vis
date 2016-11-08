package uk.co.ticklethepanda.fitbit.example;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpRequestFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ticklethepanda.fitbit.*;

import java.io.IOException;
import java.time.LocalDate;

public class ActivityToChartsDriver {
    private final static LocalDate firstDay = LocalDate.of(2014, 7, 15);

    private final static Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws DaoException, IOException {

        logger.info("setting up connections, etc");

        final ClientTokenLoader loader = new ClientTokenLoader();
        final ClientCredentials clientCredentials = loader.loadFromProperties();

        final UserCredentialManager manager = new UserCredentialManager(clientCredentials);

        final Credential credentials = manager.getCredentialsForUser("me");
        credentials.refreshToken();

        final HttpRequestFactory requestFactory = manager.getHttpRequestFactory(credentials);

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