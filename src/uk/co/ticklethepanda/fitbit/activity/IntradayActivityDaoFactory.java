package uk.co.ticklethepanda.fitbit.activity;

import uk.co.ticklethepanda.fitbit.webapi.UserAndClientTokens;

public class IntradayActivityDaoFactory {

    private IntradayActivityDaoFactory() {
    }

    public static IntradayActivityDao getDao(UserAndClientTokens userToken) {
	return new IntradayActivityDaoWebApi(userToken);
    }


}
