package uk.co.ticklethepanda.activity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;

/**
 *
 */
public class DayActivityDto {

    @JsonFormat(pattern = "yyyy-MM-dd")
    public final LocalDate date;
    public final Set<MinuteActivityDto> minuteActivity;

    public DayActivityDto(LocalDate date, Set<MinuteActivityDto> minuteActivity) {
        this.date = date;
        this.minuteActivity = Collections.unmodifiableSet(minuteActivity);
    }

    public DayActivityDto(SortedSet<MinuteActivityDto> minuteActivity) {
        this.date = null;
        this.minuteActivity = minuteActivity;
    }
}
