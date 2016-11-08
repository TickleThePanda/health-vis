package uk.co.ticklethepanda.activity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.SortedSet;

/**
 * @author Lovingly hand crafted by the ISIS Business Applications Team
 */
public class DayActivity {

    @JsonFormat(pattern = "yyyy-MM-dd")
    public final LocalDate date;
    public final SortedSet<MinuteActivity> minuteActivity;

    public DayActivity(LocalDate date, SortedSet<MinuteActivity> minuteActivity) {
        this.date = date;
        this.minuteActivity = Collections.unmodifiableSortedSet(minuteActivity);
    }

    public DayActivity(SortedSet<MinuteActivity> minuteActivity) {
        this.date = null;
        this.minuteActivity = minuteActivity;
    }
}
