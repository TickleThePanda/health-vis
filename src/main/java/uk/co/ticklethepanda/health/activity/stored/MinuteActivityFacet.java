package uk.co.ticklethepanda.health.activity.stored;

import java.time.temporal.TemporalAccessor;

public interface MinuteActivityFacet<T extends TemporalAccessor> {

    T getFacet();

    MinuteActivity getActivity();

}
