package uk.co.ticklethepanda.health.weight.dtos.prediction;

import uk.co.ticklethepanda.health.weight.dtos.log.WeightForDayDto;

public class PredictedWeightLossDto {
    private WeightForDayDto heaviest;
    private WeightForDayDto lightest;
    private WeightForDayDto latest;
    private double lossPerDay;
    private PredictedWeightLossToTargetDto target;
    private PredictedWeightLossToTargetDto intermediateTarget;

    public PredictedWeightLossDto(
            WeightForDayDto heaviest,
            WeightForDayDto lightest,
            WeightForDayDto latest,
            double lossPerDay,
            PredictedWeightLossToTargetDto target,
            PredictedWeightLossToTargetDto intermediateTarget) {

        this.heaviest = heaviest;
        this.lightest = lightest;
        this.latest = latest;
        this.lossPerDay = lossPerDay;
        this.target = target;
        this.intermediateTarget = intermediateTarget;
    }


    public WeightForDayDto getHeaviest() {
        return heaviest;
    }

    public WeightForDayDto getLightest() {
        return lightest;
    }

    public WeightForDayDto getLatest() {
        return latest;
    }

    public double getLossPerDay() {
        return lossPerDay;
    }

    public PredictedWeightLossToTargetDto getTarget() {
        return target;
    }

    public PredictedWeightLossToTargetDto getIntermediateTarget() {
        return intermediateTarget;
    }


    public void setHeaviest(WeightForDayDto heaviest) {
        this.heaviest = heaviest;
    }

    public void setLightest(WeightForDayDto lightest) {
        this.lightest = lightest;
    }

    public void setLatest(WeightForDayDto latest) {
        this.latest = latest;
    }

    public void setLossPerDay(double lossPerDay) {
        this.lossPerDay = lossPerDay;
    }

    public void setTarget(PredictedWeightLossToTargetDto target) {
        this.target = target;
    }

    public void setIntermediateTarget(PredictedWeightLossToTargetDto immediateTarget) {
        this.intermediateTarget = intermediateTarget;
    }
}
