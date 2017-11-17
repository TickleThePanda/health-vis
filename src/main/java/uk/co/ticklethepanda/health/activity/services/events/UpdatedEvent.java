package uk.co.ticklethepanda.health.activity.services.events;

public interface UpdatedEvent<E> {

    E getOld();

    E getNew();

}
