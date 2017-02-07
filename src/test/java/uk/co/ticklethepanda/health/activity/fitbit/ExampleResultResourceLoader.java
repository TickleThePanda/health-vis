package uk.co.ticklethepanda.health.activity.fitbit;

import com.google.api.client.util.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;

/**
 * Created by panda on 04/02/2017.
 */
public class ExampleResultResourceLoader {

    public static String getResultFromResources(String resourceFilePath) throws IOException {
        URL url = Resources.getResource("fitbit/response/user.json");
        return Resources.toString(url, Charsets.UTF_8);
    }
}
