package uk.co.ticklethepanda.health.weight.dtos.prediction;

import uk.co.ticklethepanda.health.weight.dtos.log.AverageWeightDto;

public class PredictedWeightLossDto {
    private AverageWeightDto heaviest;
    private AverageWeightDto lightest;
    private AverageWeightDto latest;
    private double lossPerDay;
    private PredictedWeightLossToTargetDto target;
    private PredictedWeightLossToTargetDto intermediateTarget;

    public PredictedWeightLossDto(
            AverageWeightDto heaviest,
            AverageWeightDto lightest,
            AverageWeightDto latest,
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


    public AverageWeightDto getHeaviest() {
        return heaviest;
    }

    public AverageWeightDto getLightest() {
        return lightest;
    }

    public AverageWeightDto getLatest() {
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


    public void setHeaviest(AverageWeightDto heaviest) {
        this.heaviest = heaviest;
    }

    public void setLightest(AverageWeightDto lightest) {
        this.lightest = lightest;
    }

    public void setLatest(AverageWeightDto latest) {
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
