package uk.co.ticklethepanda.activity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

/**
 * @author Lovingly hand crafted by the ISIS Business Applications Team
 */
public class MinuteActivityDto {

    @JsonFormat(pattern = "HH:mm:ss")
    public final LocalTime time;
    public final int steps;

    public MinuteActivityDto(LocalTime time, int steps) {
        this.time = time;
        this.steps = steps;
    }
}
