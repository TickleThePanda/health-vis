package uk.co.ticklethepanda.activity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

/**
 *
 */
public class DayActivityDto {

    @JsonFormat(pattern = "yyyy-MM-dd")
    public final LocalDate date;
    public final List<MinuteActivityDto> minuteActivity;

    public DayActivityDto(LocalDate date, List<MinuteActivityDto> minuteActivity) {
        this.date = date;
        this.minuteActivity = Collections.unmodifiableList(minuteActivity);
    }

    public DayActivityDto(List<MinuteActivityDto> minuteActivity) {
        this.date = null;
        this.minuteActivity = minuteActivity;
    }
}
