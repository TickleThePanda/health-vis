package uk.co.ticklethepanda.health.activity.fitbit;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.google.api.client.util.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;

/**
 * Created by panda on 29/01/2017.
 */
public class TestHttpTransports {

    public static HttpTransport IO_ERROR_TRANSPORT = new HttpTransport() {
        @Override
        protected LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
            throw new IOException();
        }
    };

    public static HttpTransport getSingleResultTransport(String response) {
        return new MockHttpTransport() {
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
    }

}
