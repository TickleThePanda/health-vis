package uk.co.ticklethepanda.health.activity.services.events.activity;

import uk.co.ticklethepanda.health.activity.repositories.ActivityEntity;
import uk.co.ticklethepanda.health.activity.services.events.UpdatedEvent;

public class UpdatedActivityEvent implements UpdatedEvent<ActivityEntity> {

    private final ActivityEntity oldActivity;
    private final ActivityEntity newActivity;

    public UpdatedActivityEvent(ActivityEntity oldActivity, ActivityEntity newActivity) {
        this.oldActivity = oldActivity;
        this.newActivity = newActivity;
    }

    @Override
    public ActivityEntity getOld() {
        return oldActivity;
    }

    @Override
    public ActivityEntity getNew() {
        return newActivity;
    }
}
