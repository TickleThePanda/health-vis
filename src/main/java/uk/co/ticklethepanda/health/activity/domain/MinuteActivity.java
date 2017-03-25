package uk.co.ticklethepanda.health.activity.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
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
public class MinuteActivity implements Serializable {

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
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

    public MinuteActivity(LocalTime time, double steps) {
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
