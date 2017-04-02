package uk.co.ticklethepanda.health.activity.fitbit.user;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;

import org.junit.jupiter.api.*;
import uk.co.ticklethepanda.health.activity.fitbit.DaoException;
import uk.co.ticklethepanda.health.activity.fitbit.ExampleResultResourceLoader;
import uk.co.ticklethepanda.health.activity.fitbit.TestHttpTransports;

import java.io.IOException;
import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.co.ticklethepanda.health.activity.fitbit.TestHttpTransports.IO_ERROR_TRANSPORT;

@DisplayName("FitbitUserRepo tests")
public class FitbitUserRepoTest {

    private FitbitUserRepo fitbitUserRepo;

    @BeforeEach
    public void setup() throws IOException {
        String response = ExampleResultResourceLoader.getResultFromResources("fitbit/response/user.json");

        HttpTransport transport = TestHttpTransports.getSingleResultTransport(response);
        HttpRequestFactory requestFactory = transport.createRequestFactory();
        this.fitbitUserRepo = new FitbitUserRepo(requestFactory);
    }

    @Test
    public void getAuthorisedUser_memberSince_correctResult() throws DaoException {
        FitbitUser fitbitUser = fitbitUserRepo.getAuthorisedUser();
        assertThat(fitbitUser.getMemberSince(), equalTo(LocalDate.of(2014, 1, 10)));
    }

    @Test()
    public void getAuthorisedUser_() throws DaoException {
        FitbitUserRepo fitbitUserRepo = new FitbitUserRepo(IO_ERROR_TRANSPORT.createRequestFactory());
        Assertions.assertThrows(DaoException.class, fitbitUserRepo::getAuthorisedUser);
    }
}
