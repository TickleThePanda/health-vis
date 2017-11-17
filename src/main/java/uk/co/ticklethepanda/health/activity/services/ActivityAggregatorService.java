package uk.co.ticklethepanda.health.activity.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uk.co.ticklethepanda.health.activity.repositories.ActivityEntity;
import uk.co.ticklethepanda.health.activity.services.events.CreatedEvent;
import uk.co.ticklethepanda.health.activity.services.events.UpdatedEvent;
import uk.co.ticklethepanda.health.activity.repositories.ActivityAggregationInMemoryRepo;
import uk.co.ticklethepanda.health.activity.repositories.ActivityRepo;

import java.util.List;

@Component
public class ActivityAggregatorService implements InitializingBean {

    private static final Logger LOG = LogManager.getLogger();

    @Autowired
    private ActivityAggregationInMemoryRepo activityAggregationRepo;

    @Autowired
    private ActivityRepo activityRepo;

    @EventListener
    public void handleUpdatedActivity(UpdatedEvent<ActivityEntity> minuteActivityUpdatedEvent) {
        activityAggregationRepo.remove(minuteActivityUpdatedEvent.getOld());
        activityAggregationRepo.add(minuteActivityUpdatedEvent.getNew());
    }

    @EventListener
    public void handleCreatedActivity(CreatedEvent<ActivityEntity> minuteActivityCreatedEvent) {
        activityAggregationRepo.add(minuteActivityCreatedEvent.getCreated());
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        LOG.info("loading activity data for aggregation");
        List<ActivityEntity> activityEntities = activityRepo.findAllWithSomeActivityInTheDay();
        LOG.info("adding activity data to aggregation repo");
        activityAggregationRepo.addAll(activityEntities);
    }
}
