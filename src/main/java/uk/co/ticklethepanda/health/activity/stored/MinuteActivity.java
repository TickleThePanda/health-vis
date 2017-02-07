package uk.co.ticklethepanda.health.activity.stored;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 *
 */
@Entity
@Table(name = "MINUTE_ACTIVITY",
        uniqueConstraints = @UniqueConstraint(columnNames = {"DATE", "TIME"}))
@NamedQueries({
        @NamedQuery(name = "findByDateAndTime",
                query = "from MinuteActivity as activity "
                        + "where activity.date = :date and activity.time = :time"),
        @NamedQuery(name = "findByDate",
                query = "from MinuteActivity as activity where activity.date = :date"),
        @NamedQuery(name = "countByDate",
                query = "select count(activity) from MinuteActivity as activity where activity.date = :date"),
        @NamedQuery(name = "getAverageDay",
                query = "select activity.time, avg(activity.steps)"
                        + " from MinuteActivity as activity"
                        + " where " + MinuteActivity.DATE_HAS_STEPS
                        + " group by activity.time"
                        + " order by activity.time"),
        @NamedQuery(name = "getAverageDaySinceDate",
                query = "select activity.time, avg(activity.steps)"
                        + " from MinuteActivity as activity"
                        + " where activity.date > :date and " + MinuteActivity.DATE_HAS_STEPS
                        + " group by activity.time"
                        + " order by activity.time"),
        @NamedQuery(name = "getAverageDayBetweenDates",
                query = "select activity.time, avg(activity.steps)"
                        + " from MinuteActivity as activity"
                        + " where (:start is null or activity.date > :start)"
                            + " and (:end is null or activity.date < :end)"
                            + " and " + MinuteActivity.DATE_HAS_STEPS
                        + " group by activity.time"
                        + " order by activity.time"),
        @NamedQuery(name = "getAverageDayByWeekday",
                query = "select weekday(activity.date), activity.time, avg(activity.steps)"
                        + " from MinuteActivity as activity"
                        + " where " + MinuteActivity.DATE_HAS_STEPS
                        + " group by weekday(activity.date), activity.time"
                        + " order by weekday(activity.date), activity.time"),
        @NamedQuery(name = "getAverageDayByMonth",
                query = "select month(activity.date), activity.time, avg(activity.steps)"
                        + " from MinuteActivity as activity"
                        + " where " + MinuteActivity.DATE_HAS_STEPS
                        + " group by month(activity.date), activity.time"
                        + " order by month(activity.date), activity.time"),
        @NamedQuery(name = "getEarliestDateOfActivity",
                query = "select min(activity.date) from MinuteActivity as activity")

})
public class MinuteActivity {

    public static final String DATE_HAS_STEPS = " activity.date" +
            " in (select activity.date" +
            " from MinuteActivity as activity" +
            " group by activity.date having sum(activity.steps) > 0)";
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name="increment", strategy = "increment")
    @Column(name = "MINUTE_ACTIVITY_ID", updatable = false, nullable = false)
    private long id;

    @Column(name = "DATE", updatable = false, nullable = false)
    private LocalDate date;

    @Column(name = "TIME", updatable = false, nullable = false)
    private LocalTime time;

    @Column(name = "STEPS")
    private double steps;

    public MinuteActivity() {

    }

    public MinuteActivity(LocalDate date, LocalTime time, double steps) {
        this.date = date;
        this.time = time;
        this.steps = steps;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate dayActivity) {
        this.date = date;
    }

    public double getSteps() {
        return steps;
    }

    public void setSteps(double steps) {
        this.steps = steps;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public static boolean representsOneDay(
            Collection<MinuteActivity> activity) {

        List<LocalDate> dates = activity.stream()
                .map(MinuteActivity::getDate)
                .distinct()
                .collect(toList());

        return dates.size() == 1;
    }
}
