package uk.co.ticklethepanda.activity.local;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.util.Set;

/**
 * Created by panda on 08/11/2016.
 */
@NamedQueries({
        @NamedQuery(
                name = "findDayActivityByDate",
                query = "from DayActivity d where d.date = :date"
        ),
        @NamedQuery(
                name = "getAllDayActivity",
                query = "from DayActivity d"
        )
})

@Entity
@Table(name = "DAY_ACTIVITY")
public class DayActivity {

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    @Column(name = "DAY_ACTIVITY_ID", updatable = false, nullable = false)
    private long id;

    @Column(name = "DATE", unique = true)
    private LocalDate date;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dayActivity")
    private Set<MinuteActivity> minuteActivityEntities;

    public DayActivity() {
    }

    public DayActivity(LocalDate date, Set<MinuteActivity> minuteActivity) {
        this.date = date;
        this.minuteActivityEntities = minuteActivity;
    }


    public Set<MinuteActivity> getMinuteActivityEntities() {
        return minuteActivityEntities;
    }

    public void setMinuteActivityEntities(Set<MinuteActivity> minuteActivityEntities) {
        this.minuteActivityEntities = minuteActivityEntities;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
