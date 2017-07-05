package uk.co.ticklethepanda.health.weight;

import org.junit.jupiter.api.Test;
import uk.co.ticklethepanda.health.weight.domain.entities.Weight;
import uk.co.ticklethepanda.health.weight.dtos.PredictedWeightDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class PredictedWeightDtoTest {

    private static final LocalDate today = LocalDate.now();

    private static final LocalDate twoDaysAgo = today.minusDays(2);
    private static final LocalDate yesterday = today.minusDays(1);

    private static final LocalDate tomorrow = today.plusDays(1);
    private static final LocalDate twoDaysAway = today.plusDays(2);

    @Test
    public void predictWeights_emptyList_emptyListReturned() {
        List<Weight> weights = new ArrayList<>();

        List<PredictedWeightDto> predictedWeightDtos = PredictedWeightDto.predictWeights(weights);

        assertThat(predictedWeightDtos, is(new ArrayList<>()));
    }

    @Test
    public void predictWeights_allValues$singleDay_correctAverages() {

        List<Weight> weights = Arrays.asList(
                new Weight(today, 2.0, 4.0)
        );

        List<PredictedWeightDto> predictedWeightDtos = PredictedWeightDto.predictWeights(weights);

        assertThat(predictedWeightDtos, hasItems(
                new PredictedWeightDto(today, 3.0)
        ));
    }

    @Test
    public void predictWeights_allValues$multipleDays_correctAverages() {

        List<Weight> weights = Arrays.asList(
                new Weight(yesterday, 1.0, 3.0),
                new Weight(today, 2.0, 4.0),
                new Weight(tomorrow, 3.0, 5.0)
        );

        List<PredictedWeightDto> predictedWeightDtos = PredictedWeightDto.predictWeights(weights);

        assertThat(predictedWeightDtos, hasItems(
                new PredictedWeightDto(yesterday, 2.0),
                new PredictedWeightDto(today, 3.0),
                new PredictedWeightDto(tomorrow, 4.0)
        ));
    }

    @Test
    public void predictWeights_dayWithMissingEntry$singleDayAmMissing_correctAverages() {

        List<Weight> weights = Arrays.asList(
                new Weight(today, null, 2.0)
        );

        List<PredictedWeightDto> predictedWeightDtos = PredictedWeightDto.predictWeights(weights);

        assertThat(predictedWeightDtos, hasItems(
                new PredictedWeightDto(today, 2.0)
        ));
    }

    @Test
    public void predictWeights_dayWithMissingEntry$singleDayPmMissing_correctAverages() {

        List<Weight> weights = Arrays.asList(
                new Weight(today, 2.0, null)
        );

        List<PredictedWeightDto> predictedWeightDtos = PredictedWeightDto.predictWeights(weights);

        assertThat(predictedWeightDtos, hasItems(
                new PredictedWeightDto(today, 2.0)
        ));
    }

    @Test
    public void predictWeights_dayWithMissingEntry$fullDayNotNeighbouring_correctAverages() {

        List<Weight> weights = Arrays.asList(
                new Weight(twoDaysAgo, 1.0, 3.0),
                new Weight(today, 2.0, null)
        );

        List<PredictedWeightDto> predictedWeightDtos = PredictedWeightDto.predictWeights(weights);

        assertThat(predictedWeightDtos, hasItems(
                new PredictedWeightDto(twoDaysAgo, 2.0),
                new PredictedWeightDto(today, 3.0)
        ));
    }

    @Test
    public void predictWeights_dayWithMissingEntry$fullDayBefore$withDiffCalculated_correctAverages() {

        List<Weight> weights = Arrays.asList(
                new Weight(twoDaysAgo, 1.0, 3.0),
                new Weight(yesterday, 1.0, 3.0),
                new Weight(today, null, 3.0)
        );

        List<PredictedWeightDto> predictedWeightDtos = PredictedWeightDto.predictWeights(weights);

        assertThat(predictedWeightDtos, hasItems(
                new PredictedWeightDto(twoDaysAgo, 2.0),
                new PredictedWeightDto(yesterday, 2.0),
                new PredictedWeightDto(today, 2.0)
        ));
    }

    @Test
    public void predictWeights_dayWithMissingEntry$fullDayAfter$withDiffCalculated_errorThrown() {

        List<Weight> weights = Arrays.asList(
                new Weight(today, 1.0, null),
                new Weight(tomorrow, 1.0, 3.0),
                new Weight(twoDaysAway, 1.0, 3.0)
        );

        List<PredictedWeightDto> predictedWeightDtos = PredictedWeightDto.predictWeights(weights);

        assertThat(predictedWeightDtos, hasItems(
                new PredictedWeightDto(today, 2.0),
                new PredictedWeightDto(tomorrow, 2.0),
                new PredictedWeightDto(twoDaysAway, 2.0)
        ));
    }

    @Test
    public void predictWeights_dayWithMissingEntry$fullDayAfter$withoutDiffCalculated_usesOnly1Diff() {

        List<Weight> weights = Arrays.asList(
                new Weight(today, 1.0, null),
                new Weight(tomorrow, 1.0, 3.0)
        );

        List<PredictedWeightDto> predictedWeightDtos = PredictedWeightDto.predictWeights(weights);

        assertThat(predictedWeightDtos, hasItems(
                new PredictedWeightDto(today, 2.0),
                new PredictedWeightDto(tomorrow, 2.0)
        ));
    }

    @Test
    public void predictWeights_dayWithMissingEntry$fullDayBefore$withoutDiffCalculated_usesOnly1Diff() {

        List<Weight> weights = Arrays.asList(
                new Weight(yesterday, 1.0, 3.0),
                new Weight(today, null, 3.0)
        );

        List<PredictedWeightDto> predictedWeightDtos = PredictedWeightDto.predictWeights(weights);

        assertThat(predictedWeightDtos, hasItems(
                new PredictedWeightDto(yesterday, 2.0),
                new PredictedWeightDto(today, 2.0)
        ));
    }
}
