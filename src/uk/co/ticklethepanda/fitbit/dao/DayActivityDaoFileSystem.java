package uk.co.ticklethepanda.fitbit.dao;

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

import uk.co.ticklethepanda.fitbit.ActivityCollection;
import uk.co.ticklethepanda.fitbit.time.LocalDateRange;
import uk.co.ticklethepanda.fitbit.ActivityForDate;

public class DayActivityDaoFileSystem implements DayActivityDao {

    private final static String DEFAULT_CACHE_LOC = System
	    .getProperty("user.home") + File.separator + ".fitbit" + File.separator;

    private final String cacheDir;
    private final Gson gson;

    public DayActivityDaoFileSystem() {
	this.cacheDir = DEFAULT_CACHE_LOC;
	this.gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
		.create();
	new File(cacheDir).mkdirs();
    }

    @Override
    public ActivityForDate getDayActivity(LocalDate date) throws DAOException {
	File file = new File(getCacheFileName(date));
	if (file.exists()) {
	    try (Reader reader = new FileReader(file)) {
		return gson.fromJson(reader, ActivityForDate.class);
	    } catch (IOException e) {
		throw new DAOException("Could not access the cached activity.", e);
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

    @Override
    public ActivityCollection getActivityRange(LocalDate start, LocalDate end)
	    throws DAOException {
	List<ActivityForDate> range = new ArrayList<ActivityForDate>();
	for (LocalDate date : new LocalDateRange(start, end)) {
	    ActivityForDate activity = this.getDayActivity(date);
	    if (activity != null) {
		range.add(this.getDayActivity(date));
	    }
	}
	return new ActivityCollection(range);
    }

    @Override
    public void saveDayActivity(ActivityForDate activity) throws DAOException {
	try (FileWriter fileWriter = new FileWriter(
		getCacheFileName(activity.getDate()))) {
	    fileWriter.write(gson.toJson(activity, ActivityForDate.class));
	} catch (IOException e) {
	    throw new DAOException("Could not write activty out", e);
	}
    }

}
