package uk.co.ticklethepanda.health.activity.repositories;

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
public class ActivityEntity implements Serializable {

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
    private Long steps;

    public ActivityEntity() {

    }

    public ActivityEntity(LocalDate date, LocalTime time, Long steps) {
        this.date = date;
        this.time = time;
        this.steps = steps;
    }

    public ActivityEntity(LocalTime time, Long steps) {
        this.time = time;
        this.steps = steps;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate dayActivity) {
        this.date = date;
    }

    public Long getSteps() {
        return steps;
    }

    public void setSteps(Long steps) {
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
            Collection<ActivityEntity> activity) {

        List<LocalDate> dates = activity.stream()
                .map(ActivityEntity::getDate)
                .distinct()
                .collect(toList());

        return dates.size() == 1;
    }
}
