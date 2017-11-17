package uk.co.ticklethepanda.health.activity.services.events.activity;

import uk.co.ticklethepanda.health.activity.repositories.ActivityEntity;
import uk.co.ticklethepanda.health.activity.services.events.CreatedEvent;

public class CreatedActivityEvent implements CreatedEvent<ActivityEntity> {

    private final ActivityEntity created;

    public CreatedActivityEvent(ActivityEntity created) {
        this.created = created;
    }

    @Override
    public ActivityEntity getCreated() {
        return created;
    }
}
