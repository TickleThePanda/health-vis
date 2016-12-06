package uk.co.ticklethepanda.health.weight;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.http.client.fluent.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.List;

/**
 *
 */
@Controller
public class WeightController {

    private static final Logger LOG = LogManager.getLogger();
    private final WeightService weightService;
    private final String healthUrl;

    public WeightController(
            @Autowired WeightService weightService,
            @Value("${health.spreadsheet.url}") String healthUrl) {
        this.weightService = weightService;
        this.healthUrl = healthUrl;
    }

    @Scheduled(initialDelay = 0, fixedRate = 1000 * 60 * 60)
    public void cacheWeightData() throws IOException {
        LOG.info("caching weight data");
        String response = Request.Get(healthUrl)
                .execute()
                .returnContent()
                .asString();

        Reader in = new StringReader(response);
        CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader());
        List<CSVRecord> csvRecords = parser.getRecords();

        for(CSVRecord r : csvRecords) {
            String dateText = r.get("Date");
            String weightAmText = r.get("Weight AM");
            String weightPmText = r.get("Weight PM");

            LocalDate localDate = LocalDate.parse(dateText);
            Double weightAm = weightAmText.equals("") ? null : Double.parseDouble(weightAmText);
            Double weightPm = weightPmText.equals("") ? null : Double.parseDouble(weightPmText);

            weightService.createWeightEntry(new Weight(localDate, weightAm, weightPm));
        }

    }
}
