package uk.co.ticklethepanda.health.weight.dtos.prediction;

/**
 * Created by panda on 05/07/2017.
 */
public class PredictedWeightLossToTargetDto {
    private double target;
    private double days;

    public PredictedWeightLossToTargetDto(
            double target,
            double days
    ) {
        this.target = target;
        this.days = days;
    }

    public double getTarget() {
        return target;
    }

    public void setTarget(double target) {
        this.target = target;
    }

    public double getDays() {
        return days;
    }

    public void setDays(double days) {
        this.days = days;
    }
}
