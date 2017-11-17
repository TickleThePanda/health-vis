package uk.co.ticklethepanda.health.activity.fitbit.activity;

import com.google.api.client.http.*;
import com.google.api.client.util.Charsets;
import com.google.common.io.Resources;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.ticklethepanda.fitbit.client.FitbitClientException;
import uk.co.ticklethepanda.fitbit.client.repos.FitbitIntradayActivityClient;
import uk.co.ticklethepanda.health.activity.fitbit.TestHttpTransports;
import uk.co.ticklethepanda.fitbit.client.model.FitbitIntradayActivity;
import uk.co.ticklethepanda.fitbit.client.model.FitbitMinuteActivity;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.co.ticklethepanda.health.activity.fitbit.TestHttpTransports.IO_ERROR_TRANSPORT;


@DisplayName("FitbitIntradayActivityClient tests")
public class FitbitIntradayActivityClientTest {

    private FitbitIntradayActivityClient fitbitIntradayActivityClient;

    @BeforeEach
    public void setup() throws IOException {
        URL url = Resources.getResource("fitbit/response/intraday.json");
        String response = Resources.toString(url, Charsets.UTF_8);

        HttpTransport transport = TestHttpTransports.getSingleResultTransport(response);
        HttpRequestFactory requestFactory = transport.createRequestFactory();
        this.fitbitIntradayActivityClient = new FitbitIntradayActivityClient(requestFactory);

    }

    @Test
    public void getDayActivity_correctResponse_noError() throws Exception {
        FitbitIntradayActivity activity = this.fitbitIntradayActivityClient.getDayActivity(LocalDate.now());
        assertThat(activity.getDate().toString(), CoreMatchers.equalTo("2014-09-05"));
        assertThat(activity.getIntradayMinuteActivitySeries().getElements(), CoreMatchers.hasItems(
                new FitbitMinuteActivity(LocalTime.of(0, 0), 0L),
                new FitbitMinuteActivity(LocalTime.of(0, 1), 22L)
        ));
    }

    @Test
    public void getAuthorisedUser_error_throwsDaoException() throws FitbitClientException {
        FitbitIntradayActivityClient fitbitIntradayActivityClient =
                new FitbitIntradayActivityClient(IO_ERROR_TRANSPORT.createRequestFactory());

        assertThrows(FitbitClientException.class,
                () -> fitbitIntradayActivityClient.getDayActivity(LocalDate.now()));
    }

}