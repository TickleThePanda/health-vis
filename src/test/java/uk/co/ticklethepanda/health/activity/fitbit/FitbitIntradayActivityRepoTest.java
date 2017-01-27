package uk.co.ticklethepanda.health.activity.fitbit;

import com.google.api.client.http.*;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.google.api.client.util.Charsets;
import com.google.common.io.Resources;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import uk.co.ticklethepanda.health.activity.fitbit.activity.FitbitIntradayActivity;
import uk.co.ticklethepanda.health.activity.fitbit.activity.FitbitIntradayActivityRepo;
import uk.co.ticklethepanda.health.activity.fitbit.activity.FitbitMinuteActivity;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.Assert.assertThat;

/**
 * Created by panda on 26/01/2017.
 */
public class FitbitIntradayActivityRepoTest {

    private FitbitIntradayActivityRepo fitbitIntradayActivityRepo;

    @Before
    public void setup() throws IOException {
        URL url = Resources.getResource("fitbit/response/intraday.txt");
        String response = Resources.toString(url, Charsets.UTF_8);

        HttpTransport transport = new MockHttpTransport() {
            @Override
            public LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
                return new MockLowLevelHttpRequest() {
                    @Override
                    public LowLevelHttpResponse execute() throws IOException {
                        MockLowLevelHttpResponse result = new MockLowLevelHttpResponse();
                        result.setContent(response);
                        return result;
                    }
                };
            }
        };
        HttpRequestFactory requestFactory = transport.createRequestFactory();
        this.fitbitIntradayActivityRepo = new FitbitIntradayActivityRepo(requestFactory);

    }

    @Test
    public void getDayActivity_correctResponse_noError() throws Exception {
        FitbitIntradayActivity activity = this.fitbitIntradayActivityRepo.getDayActivity(LocalDate.now());
        assertThat(activity.getDate().toString(), CoreMatchers.equalTo("2014-09-05"));
        assertThat(activity.getIntradayMinuteActivitySeries().getElements(), CoreMatchers.hasItems(
                new FitbitMinuteActivity(LocalTime.of(0, 0), 0.0),
                new FitbitMinuteActivity(LocalTime.of(0, 1), 22.0)
        ));
    }

}