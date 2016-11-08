package uk.co.ticklethepanda.activity.local;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.time.LocalTime;
import java.util.Set;

/**
 * Created by panda on 08/11/2016.
 */
@Entity
@Table(name = "MINUTE_ACTIVITY",
        uniqueConstraints = @UniqueConstraint(columnNames = {"DAY_ACTIVITY_ID", "TIME"}))
public class MinuteActivity {

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name="increment", strategy = "increment")
    @Column(name = "MINUTE_ACTIVITY_ID", updatable = false, nullable = false)
    private long id;

    @Column(name = "TIME")
    private LocalTime time;

    @Column(name = "STEPS")
    private int steps;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "DAY_ACTIVITY_ID", nullable = false)
    private DayActivity dayActivity;

    public MinuteActivity() {
    }

    public DayActivity getDayActivity() {
        return dayActivity;
    }

    public void setDayActivity(DayActivity dayActivity) {
        this.dayActivity = dayActivity;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
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
}
