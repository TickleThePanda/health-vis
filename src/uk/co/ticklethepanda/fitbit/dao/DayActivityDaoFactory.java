package uk.co.ticklethepanda.fitbit.dao;

import uk.co.ticklethepanda.fitbit.UserAndClientTokens;

public class DayActivityDaoFactory {

    private DayActivityDaoFactory() {
    }

    public static DayActivityDao getAvailable(UserAndClientTokens userToken) {
	DayActivityDaoWebApi remote = new DayActivityDaoWebApi(userToken);
	if (remote.isAvailable()) {
	    return remote;
	} else {
	    return new DayActivityDaoFileSystem();
	}
    }

    public static DayActivityDao getLocal() {
	return new DayActivityDaoFileSystem();
    }

}
