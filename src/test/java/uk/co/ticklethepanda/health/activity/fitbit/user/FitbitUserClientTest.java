package uk.co.ticklethepanda.health.activity.fitbit.user;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;

import org.junit.jupiter.api.*;
import uk.co.ticklethepanda.fitbit.client.FitbitClientException;
import uk.co.ticklethepanda.fitbit.client.repos.FitbitUserClient;
import uk.co.ticklethepanda.health.activity.fitbit.ExampleResultResourceLoader;
import uk.co.ticklethepanda.health.activity.fitbit.TestHttpTransports;
import uk.co.ticklethepanda.fitbit.client.model.FitbitUser;

import java.io.IOException;
import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.co.ticklethepanda.health.activity.fitbit.TestHttpTransports.IO_ERROR_TRANSPORT;

@DisplayName("FitbitUserClient tests")
public class FitbitUserClientTest {

    private FitbitUserClient fitbitUserClient;

    @BeforeEach
    public void setup() throws IOException {
        String response = ExampleResultResourceLoader.getResultFromResources("fitbit/response/user.json");

        HttpTransport transport = TestHttpTransports.getSingleResultTransport(response);
        HttpRequestFactory requestFactory = transport.createRequestFactory();
        this.fitbitUserClient = new FitbitUserClient(requestFactory);
    }

    @Test
    public void getAuthorisedUser_memberSince_correctResult() throws FitbitClientException {
        FitbitUser fitbitUser = fitbitUserClient.getAuthorisedUser();
        assertThat(fitbitUser.getMemberSince(), equalTo(LocalDate.of(2014, 1, 10)));
    }

    @Test()
    public void getAuthorisedUser_() throws FitbitClientException {
        FitbitUserClient fitbitUserClient = new FitbitUserClient(IO_ERROR_TRANSPORT.createRequestFactory());
        Assertions.assertThrows(FitbitClientException.class, fitbitUserClient::getAuthorisedUser);
    }
}
