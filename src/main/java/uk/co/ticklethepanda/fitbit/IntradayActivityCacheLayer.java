package uk.co.ticklethepanda.fitbit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import uk.co.ticklethepanda.utility.LocalDateRange;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    new File(this.cacheDir).mkdirs();
  }

  private String getCacheFileName(LocalDate date) {
    return this.cacheDir + date.toString() + ".json";
  }

  private IntradayActivity getDayActivity(LocalDate date) throws DaoException {
    final File file = new File(this.getCacheFileName(date));
    if (file.exists()) {
      try (Reader reader = new FileReader(file)) {
        return this.gson.fromJson(reader, IntradayActivity.class);
      } catch (final IOException e) {
        throw new DaoException("Could not access the cached activity.", e);
      }
    } else {
      return null;
    }
  }

  public IntradayActivityRange getIntradayActivityRange(LocalDate start, LocalDate end)
      throws DaoException {
    final List<IntradayActivity> range = new ArrayList<>();
    for (final LocalDate date : new LocalDateRange(start, end)) {
      final IntradayActivity activity = this.getDayActivity(date);
      if (activity != null) {
        range.add(this.getDayActivity(date));
      }
    }
    return new IntradayActivityRange(range);
  }

  @Override
  public IntradayActivity getValue(LocalDate key) throws CacheLayerException {
    try {
      return this.getDayActivity(key);
    } catch (final DaoException e) {
      throw CacheLayerException.createLoadException(e);
    }
  }

  public boolean isDateCached(LocalDate date) {
    final File file = new File(this.getCacheFileName(date));
    return file.exists();
  }

  @Override
  public void save(IntradayActivity value) throws CacheLayerException {
    try {
      this.saveDayActivity(value);
    } catch (final DaoException e) {
      throw CacheLayerException.createSaveException(e);
    }
  }

  private void saveDayActivity(IntradayActivity activity) throws DaoException {
    try (FileWriter fileWriter = new FileWriter(
        this.getCacheFileName(activity.getDate()))) {
      fileWriter.write(this.gson.toJson(activity, IntradayActivity.class));
    } catch (final IOException e) {
      throw new DaoException("Could not write activity out", e);
    }
  }
}