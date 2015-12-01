package uk.co.ticklethepanda.fitbit.dao.cache;


import java.time.LocalDate;

import uk.co.ticklethepanda.fitbit.ActivityForDate;
import uk.co.ticklethepanda.fitbit.dao.DAOException;
import uk.co.ticklethepanda.fitbit.dao.DayActivityDao;
import uk.co.ticklethepanda.fitbit.dao.DayActivityDaoFactory;

public class DayActivityCacheLayer
	implements CacheLayer<LocalDate, ActivityForDate> {

    private final DayActivityDao dao;

    public DayActivityCacheLayer() {
	this.dao = DayActivityDaoFactory.getLocal();
    }

    @Override
    public ActivityForDate getValue(LocalDate key) throws CacheLayerException {
	try {
	    return dao.getDayActivity(key);
	} catch (DAOException e) {
	    throw CacheLayerException.createLoadException(e);
	}
    }

    @Override
    public void save(ActivityForDate value) throws CacheLayerException {
	try {
	    dao.saveDayActivity(value);
	} catch (DAOException e) {
	    throw CacheLayerException.createSaveException(e);
	}
    }
}