package uk.co.ticklethepanda.health.activity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;

public interface ActivityRepo extends JpaRepository<MinuteActivity, Long> {

    String DATE_HAS_STEPS = " activity.date" +
            " in (select activity.date" +
            " from MinuteActivity as activity" +
            " group by activity.date having sum(activity.steps) > 0)";


    @Query("from MinuteActivity as activity "
            + "where activity.date = :date and activity.time = :time")
    MinuteActivity findByDateAndTime(
            @Param("date") LocalDate date,
            @Param("time") LocalTime time
    );

    @Query("from MinuteActivity as activity where activity.date = :date")
    List<MinuteActivity> findByDate(
            @Param("date") LocalDate date
    );

    @Query("select count(activity) from MinuteActivity as activity where activity.date = :date")
    int countByDate(
            @Param("date") LocalDate date
    );

    @Query("select new MinuteActivity(activity.time, avg(activity.steps))"
            + " from MinuteActivity as activity"
            + " where " + DATE_HAS_STEPS
            + " group by activity.time"
            + " order by activity.time")
    List<MinuteActivity> getAverageDay();

    @Query("select new MinuteActivity(activity.time, avg(activity.steps))"
            + " from MinuteActivity as activity"
            + " where (:start is null or activity.date > :start)"
            + " and (:end is null or activity.date < :end)"
            + " and " + DATE_HAS_STEPS
            + " group by activity.time"
            + " order by activity.time")
    List<MinuteActivity> getAverageDayBetweenDates(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    /*
     * {@code weekday(activity.date) + 1}:
     *  weekday is 0-6 from database but 1-7 in {@code java.time}.
     */
    @Query("select new uk.co.ticklethepanda.health.activity.MinuteActivityByWeekday("
            + "weekday(activity.date) + 1, activity.time, avg(activity.steps))"
            + " from MinuteActivity as activity"
            + " where " + DATE_HAS_STEPS
            + " group by weekday(activity.date), activity.time"
            + " order by weekday(activity.date), activity.time")
    List<MinuteActivityFacet<DayOfWeek>> getAverageDayByWeekday();

    @Query("select new uk.co.ticklethepanda.health.activity.MinuteActivityByMonth(" +
            "month(activity.date), activity.time, avg(activity.steps))"
            + " from MinuteActivity as activity"
            + " where " + DATE_HAS_STEPS
            + " group by month(activity.date), activity.time"
            + " order by month(activity.date), activity.time")
    List<MinuteActivityFacet<Month>> getAverageDayByMonth();

    @Query("select min(activity.date) from MinuteActivity as activity")
    LocalDate getEarliestDateOfActivity();
}
