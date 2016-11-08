package uk.co.ticklethepanda.activity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.Collections;
import java.util.SortedSet;

/**
 * @author Lovingly hand crafted by the ISIS Business Applications Team
 */
public class DayActivityDto {

    @JsonFormat(pattern = "yyyy-MM-dd")
    public final LocalDate date;
    public final SortedSet<MinuteActivityDto> minuteActivity;

    public DayActivityDto(LocalDate date, SortedSet<MinuteActivityDto> minuteActivity) {
        this.date = date;
        this.minuteActivity = Collections.unmodifiableSortedSet(minuteActivity);
    }

    public DayActivityDto(SortedSet<MinuteActivityDto> minuteActivity) {
        this.date = null;
        this.minuteActivity = minuteActivity;
    }
}
