package uk.co.ticklethepanda.health.activity;

import java.time.temporal.TemporalAccessor;

public interface MinuteActivityFacet<T extends TemporalAccessor> {

    T getFacet();

    MinuteActivity getActivity();

}
