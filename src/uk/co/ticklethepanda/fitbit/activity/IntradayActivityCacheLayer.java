package uk.co.ticklethepanda.fitbit.activity;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import uk.co.ticklethepanda.fitbit.caching.CacheLayer;
import uk.co.ticklethepanda.fitbit.caching.CacheLayerException;
import uk.co.ticklethepanda.fitbit.webapi.DaoException;
import uk.co.ticklethepanda.utility.LocalDateRange;

public class IntradayActivityCacheLayer
	implements CacheLayer<LocalDate, IntradayActivity> {
    
    private final static String DEFAULT_CACHE_LOC = System
	    .getProperty("user.home") + File.separator + ".fitbit" + File.separator;

    private final String cacheDir;
    private final Gson gson;

    public IntradayActivityCacheLayer() {
	this.cacheDir = DEFAULT_CACHE_LOC;
	this.gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
		.create();
	new File(cacheDir).mkdirs();
    }

    @Override
    public IntradayActivity getValue(LocalDate key) throws CacheLayerException {
	try {
	    return this.getDayActivity(key);
	} catch (DaoException e) {
	    throw CacheLayerException.createLoadException(e);
	}
    }

    @Override
    public void save(IntradayActivity value) throws CacheLayerException {
	try {
	    this.saveDayActivity(value);
	} catch (DaoException e) {
	    throw CacheLayerException.createSaveException(e);
	}
    }

    public IntradayActivity getDayActivity(LocalDate date) throws DaoException {
	File file = new File(getCacheFileName(date));
	if (file.exists()) {
	    try (Reader reader = new FileReader(file)) {
		return gson.fromJson(reader, IntradayActivity.class);
	    } catch (IOException e) {
		throw new DaoException("Could not access the cached activity.", e);
	    }
	} else {
	    return null;
	}
    }

    public boolean isDateCached(LocalDate date) {
	File file = new File(getCacheFileName(date));
	return file.exists();
    }

    public String getCacheFileName(LocalDate date) {
	return cacheDir + date.toString() + ".json";
    }

    public IntradayActivityRange getIntradayActivityRange(LocalDate start, LocalDate end)
	    throws DaoException {
	List<IntradayActivity> range = new ArrayList<IntradayActivity>();
	for (LocalDate date : new LocalDateRange(start, end)) {
	    IntradayActivity activity = this.getDayActivity(date);
	    if (activity != null) {
		range.add(this.getDayActivity(date));
	    }
	}
	return new IntradayActivityRange(range);
    }

    public void saveDayActivity(IntradayActivity activity) throws DaoException {
	try (FileWriter fileWriter = new FileWriter(
		getCacheFileName(activity.getDate()))) {
	    fileWriter.write(gson.toJson(activity, IntradayActivity.class));
	} catch (IOException e) {
	    throw new DaoException("Could not write activty out", e);
	}
    }
}